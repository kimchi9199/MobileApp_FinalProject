package com.example.mobileapp_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mobileapp_final.fragment.all_devices_fragment;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new all_devices_fragment());
        if (OpenCVLoader.initDebug()){
            Log.d("OPENCV", "OK");
        }
    }

    public void openRecyclerView(View view)
    {
        Intent intent = new Intent(this, Recycler_View.class);
        startActivity(intent);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}