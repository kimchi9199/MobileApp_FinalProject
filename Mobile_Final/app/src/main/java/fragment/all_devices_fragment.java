package fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileapp_final.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.DeviceAdapter;
import Model.Devices;


public class all_devices_fragment extends Fragment {

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

        mAllDevicesAdapter = new DeviceAdapter(view.getContext(), mAllDevices);
        rcAllDevices.setAdapter(mAllDevicesAdapter);

        //De hien thi len gridview

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        rcAllDevices.setLayoutManager(gridLayoutManager);
        return view;
    }


}