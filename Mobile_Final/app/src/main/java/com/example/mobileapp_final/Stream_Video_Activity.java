package com.example.mobileapp_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Stream_Video_Activity extends AppCompatActivity {
    private ImageView mimageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_video);
        mimageView = findViewById(R.id.Iv_Streamvideo);

        final String SERVER_IP = "192.168.1.6"; // Server IP address
        final int SERVER_PORT = 9999; // Server port number
        final int BUFFER_SIZE = 65536; // Buffer size in bytes
        final int CLIENT_PORT = 9090;


        Thread receiveVideoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket udpSocket = null;
                 try {

                     udpSocket = new DatagramSocket(CLIENT_PORT);

                     byte[] buffer = new byte[BUFFER_SIZE];
                     DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                     String message = "hello";
                     byte[] data = message.getBytes();
                     InetAddress serverAddress = null;
                     serverAddress = InetAddress.getByName(SERVER_IP);

                     DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
                     udpSocket.send(sendPacket);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

//                Received video frame from server and display them
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
                                mimageView.setImageBitmap(bitmap);
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
}