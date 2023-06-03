package com.example.facerecognitionmobileclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.facerecognitionmobileclient.Model_Detect.face_Recognition;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class StreamVideoActivity extends AppCompatActivity  {

    private ImageView mImageView;
    private boolean mIsReceiving = false;
    DatagramSocket udpSocket = null;
    private face_Recognition faceRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamvideo);

        mImageView = (ImageView) findViewById(R.id.image_view);

//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        String SERVER_IP = sharedPreferences.getString("serverAdress", "");
        final String SERVER_IP = "192.168.1.11"; // Server IP address
        final int SERVER_PORT = 9999; // Server port number
        final int BUFFER_SIZE = 65536; // Buffer size in bytes
        final int CLIENT_PORT = 9090;

        if (OpenCVLoader.initDebug())
        {
            Log.d("OpenCV", "onCreate: break");
        }

        try
        {
            int intputSize=96;
            faceRecognition = new face_Recognition(
                    StreamVideoActivity.this.getAssets(),
                    StreamVideoActivity.this,
                    "MobileNet.tflite",
                    intputSize);
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.d("Stream_Video_Act","Model is not loaded");
        }

        Thread receiveVideoThread = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    udpSocket = new DatagramSocket(CLIENT_PORT);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                String message = "hello";
                byte[] data = message.getBytes();
                InetAddress serverAddress = null;
                try {
                    serverAddress = InetAddress.getByName(SERVER_IP);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
                try {
                    udpSocket.send(sendPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

//              Received video frame from server and display them
                try {
                    byte[] videoBuffer = new byte[BUFFER_SIZE];
                    DatagramPacket videoFramePacket = new DatagramPacket(videoBuffer, videoBuffer.length);
                    while (true) {
                        udpSocket.receive(videoFramePacket);
                        String lText = new String(videoBuffer, 0, videoFramePacket.getLength());
                        byte[] decodeDataImg = Base64.decode(lText, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeDataImg, 0, decodeDataImg.length);

                        // Convert Bitmap to Mat
                        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(bitmap, mat);

                        // Start Recognize Face process
                        Mat result_mat = faceRecognition.recognizeImage(mat);
                        Log.d("OK2", "1");

                        //show img view -> convert to bitmap

                        // Convert Mat to Bitmap
                        Bitmap result_bitmap = Bitmap.createBitmap(result_mat.cols(), result_mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(result_mat, result_bitmap);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImageView.setImageBitmap(result_bitmap);
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        receiveVideoThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (udpSocket != null) {
            udpSocket.close();
        }
    }
}
