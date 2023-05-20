package com.example.facerecognitionmobileclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerIPActivity extends AppCompatActivity {

    private EditText ipadressEditText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverip);

        ipadressEditText = findViewById(R.id.ip_address_edittext);
        Button connectButton  = findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("serverAdress", ipadressEditText.getText().toString());
                editor.apply();

                Intent intent = new Intent(ServerIPActivity.this, StreamVideoActivity.class);
                startActivity(intent);
            }
        });
    }
    //function check server ip is valid or not
    private boolean isValidIpAddress(String ipAddress){
        try{
            //thu phan giai dia chi ip
            InetAddress inetAdress = InetAddress.getByName(ipAddress);

            if(inetAdress.isLoopbackAddress()){
                return false;

            }
            if(inetAdress.isMulticastAddress()){
                return false;
            }
            return true;
        } catch (UnknownHostException ex){
            Toast.makeText(this, "Server Ip is not Valid", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}