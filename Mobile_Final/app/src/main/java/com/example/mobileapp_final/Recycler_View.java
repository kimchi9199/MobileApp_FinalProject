package com.example.mobileapp_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import Adapter.DeviceAdapter;
import Model.Devices;

public class Recycler_View extends AppCompatActivity {

    private RecyclerView rvDevices;
    private DeviceAdapter mDeviceAdapter;
    private List<Devices> mDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        rvDevices = (RecyclerView) findViewById(R.id.Rv_Devices);

        mDevices = new ArrayList<>();
        mDevices.add(new Devices("C01","Camera01","CamBedRoom"));
        mDevices.add(new Devices("C02","Camera02","CamLivingRoom"));
        mDevices.add(new Devices("C03","Camera03","CamKitchenRoom"));
        mDevices.add(new Devices("C04","Camera04","CamGarden"));

        mDeviceAdapter = new DeviceAdapter(this, mDevices);
        rvDevices.setAdapter(mDeviceAdapter);

        mDeviceAdapter.setListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                Intent intent = new Intent(Recycler_View.this, Stream_Video_Activity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rvDevices.setLayoutManager(linearLayoutManager);
    }
}