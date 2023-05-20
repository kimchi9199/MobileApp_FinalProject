package com.example.facerecognitionmobileclient.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.facerecognitionmobileclient.Interface.RecyclerViewInterFace;
import com.example.facerecognitionmobileclient.R;

import java.util.ArrayList;
import java.util.List;

import com.example.facerecognitionmobileclient.Adapter.DeviceAdapter;
import com.example.facerecognitionmobileclient.Model.Devices;
import com.example.facerecognitionmobileclient.StreamVideoActivity;


public class all_devices_fragment extends Fragment implements RecyclerViewInterFace {

    private RecyclerView rcAllDevices;
    private DeviceAdapter mAllDevicesAdapter;
    private List<Devices> mAllDevices;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_devices_fragment, container, false);

        rcAllDevices = (RecyclerView) view.findViewById(R.id.rv_allDevices);
        mAllDevices = new ArrayList<>();
        mAllDevices.add(new Devices("C01","Bed Room","Camera01"));
        mAllDevices.add(new Devices("C02","Living Room","Camera02"));
        mAllDevices.add(new Devices("C03","Kitchen","Camera03"));
        mAllDevices.add(new Devices("C04","Garden","Camera04"));

        mAllDevicesAdapter = new DeviceAdapter(view.getContext(), mAllDevices, all_devices_fragment.this);
        rcAllDevices.setAdapter(mAllDevicesAdapter);

        //De hien thi len gridview

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        rcAllDevices.setLayoutManager(gridLayoutManager);
        return view;
    }


    @Override
    public void onItemClick(int position) {
        Intent intent =new Intent( all_devices_fragment.this.getContext(), StreamVideoActivity.class);
        startActivity(intent);
    }
}