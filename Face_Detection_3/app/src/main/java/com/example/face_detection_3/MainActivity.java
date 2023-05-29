package com.example.face_detection_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {

    JavaCameraView javaCameraView;
    File cascFile;
    CascadeClassifier faceDetector;
    private Mat mRgba,mGray;
    private CascadeClassifier cascadeClassifier;

    final String SERVER_IP = "192.168.1.11"; // Server IP address
    final int SERVER_PORT = 9999; // Server port number
    final int BUFFER_SIZE = 65536; // Buffer size in bytes
    final int CLIENT_PORT = 9090;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();

        javaCameraView=(JavaCameraView) findViewById(R.id.javaCamView);
        javaCameraView.setCameraIndex(1);
        try {
            // Load Haar cascade
            InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);

            //create a new folder to save classifier
            File cascadeDir = getDir("cascade",Context.MODE_PRIVATE);

            //create a new cascade file in that folder
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt");

            //Define output stream to save haarcascade_frontalface_alt in mCascadefile
            FileOutputStream outputStream=new FileOutputStream(mCascadeFile);

            //create a empty file buffer to store byte
            byte[] buffer=new byte[4096];
            int byteRead;

            //Read byte in loop, when it read -1 that means no data to read
            while ((byteRead=inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer,0,byteRead);
            }

            //when reading file was completed
            inputStream.close();
            outputStream.close();

            //load cascade classifier
            faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            Log.d("face_recognition","Classifier is loaded");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        javaCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                mRgba = new Mat();
                mGray = new Mat();
            }

            @Override
            public void onCameraViewStopped() {
                mRgba.release();
                mGray.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();
                DatagramSocket udpSocket = null;
                Mat mat = null;
//                try {
//
//                    udpSocket = new DatagramSocket(CLIENT_PORT);
//
//                    byte[] buffer = new byte[BUFFER_SIZE];
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                    String message = "hello";
//                    byte[] data = message.getBytes();
//                    InetAddress serverAddress = null;
//                    serverAddress = InetAddress.getByName(SERVER_IP);
//
//                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
//                    udpSocket.send(sendPacket);
//                } catch (IOException e)
//                {
//                    throw new RuntimeException(e);
//                }
//
//                // Received video frame from server and display them
//                try {
//                    byte[] videoBuffer = new byte[BUFFER_SIZE];
//                    DatagramPacket videoFramePacket = new DatagramPacket(videoBuffer, videoBuffer.length);
//                    while (true) {
//                        udpSocket.receive(videoFramePacket);
//                        String lText = new String(videoBuffer, 0, videoFramePacket.getLength());
//                        byte[] decodeDataImg = Base64.decode(lText, Base64.DEFAULT);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeDataImg, 0, decodeDataImg.length);
//                        Log.d("1", "OK");
//                        // Convert Bitmap to Mat
//                        mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
//                        Utils.bitmapToMat(bitmap, mat);
//                        Mat result_mat;
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }



//                try {
//                    // Load Haar cascade
//                    InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
//
//                    //create a new folder to save classifier
//                    File cascadeDir = getDir("cascade",Context.MODE_PRIVATE);
//
//                    //create a new cascade file in that folder
//                    File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt");
//
//                    //Define output stream to save haarcascade_frontalface_alt in mCascadefile
//                    FileOutputStream outputStream=new FileOutputStream(mCascadeFile);
//
//                    //create a empty file buffer to store byte
//                    byte[] buffer=new byte[4096];
//                    int byteRead;
//
//                    //Read byte in loop, when it read -1 that means no data to read
//                    while ((byteRead=inputStream.read(buffer)) != -1)
//                    {
//                        outputStream.write(buffer,0,byteRead);
//                    }
//
//                    //when reading file was completed
//                    inputStream.close();
//                    outputStream.close();
//
//                    //load cascade classifier
//                    faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//                    Log.d("face_recognition","Classifier is loaded");
//
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }


                double SCALE_FACTOR = 2.0;
                Mat scaledMat = new Mat();
                Size newSize = new Size(mRgba.cols() / SCALE_FACTOR, mRgba.rows() / SCALE_FACTOR);
                Imgproc.resize(mRgba, scaledMat, newSize, 0, 0, Imgproc.INTER_LINEAR);

                // Detect Face
                MatOfRect facDetections = new MatOfRect();
                faceDetector.detectMultiScale(scaledMat,facDetections);
                for (Rect rect: facDetections.toArray())
                {
                    Imgproc.rectangle(mRgba,
                            new Point(rect.x * SCALE_FACTOR, rect.y * SCALE_FACTOR),
                            new Point((rect.x  + rect.width) * SCALE_FACTOR, (rect.y  + rect.height) * SCALE_FACTOR),
                            new Scalar(255,0,0),
                            3);
                }

                return mRgba;
//                return mat;
            }
        });

        if(OpenCVLoader.initDebug())
        {
            Log.d("OPENCV", "OK");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,this, baseCallback);
            javaCameraView.enableView();
        }
        else
        {
            baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        FileOutputStream fileOutputStream;

        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir= getDir("cascade", Context.MODE_PRIVATE);
                    cascFile = new File(cascadeDir,"haarcascade_frontalface_alt2");

                    try {
                        fileOutputStream = new FileOutputStream(cascFile);
                        byte[] buffer = new byte[65536];
                        int bytesRead;
                        while((bytesRead = inputStream.read(buffer)) != -1)
                        {

                            fileOutputStream.write(buffer,0,bytesRead);
                        }
                        inputStream.close();
                        fileOutputStream.close();

                        faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());

                        if (faceDetector.empty())
                        {
                            faceDetector=null;
                        }
                        else
                        {
                            cascadeDir.delete();
                            javaCameraView.enableView();

                        }
                        break;


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }
                default:
                {
                    super.onManagerConnected(status);
                }
                break;

            }

        }
    };

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(javaCameraView);
    }

    void getPermission() {
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 101);
        }

        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }
}