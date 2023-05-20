package com.example.facerecognitionmobileclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.facerecognitionmobileclient.fragment.all_devices_fragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    CardView cardView;
    FrameLayout profile;
    BottomNavigationView bottomNavigationView;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.page_1) {
            // Handle click on "Home" menu item
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.page_2) {
            // Handle click on "Profile" menu item
            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new all_devices_fragment());

        bottomNavigationView =  findViewById(R.id.bt_navigation_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.page_2) {
                    Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

//        profile = findViewById(R.id.frame_layout);
//
//
//        if (profile!= null) {
//            View item = profile.findViewById(R.id.page_2);
//            if (item != null) {
//                item.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
//                        startActivity(intent);
//
//                    }
//                });
//            }
//        }

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