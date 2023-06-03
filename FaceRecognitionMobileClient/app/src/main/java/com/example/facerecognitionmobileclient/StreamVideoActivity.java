package com.example.facerecognitionmobileclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamvideo);

        mImageView = (ImageView) findViewById(R.id.image_view);

//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        String SERVER_IP = sharedPreferences.getString("serverAdress", "");
        final String SERVER_IP = "192.168.2.9"; // Server IP address
        final int SERVER_PORT = 9999; // Server port number
        final int BUFFER_SIZE = 65536; // Buffer size in bytes
        final int CLIENT_PORT = 9090;

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImageView.setImageBitmap(bitmap);
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
