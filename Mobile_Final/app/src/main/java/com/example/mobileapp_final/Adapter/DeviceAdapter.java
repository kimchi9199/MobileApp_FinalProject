package com.example.mobileapp_final.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_final.Interface.RecyclerViewInterFace;
import com.example.mobileapp_final.R;

import java.util.List;

import com.example.mobileapp_final.Model.Devices;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final RecyclerViewInterFace recyclerViewInterFace;
    private static final String TAG = "DeviceAdapter";
    private List<Devices> mDevices; // Show list devices
    private Context mContext; //
    private LayoutInflater mLayoutInflater;//Sap xep du lieu vao Card View
    OnItemClickListener listener ;

    public interface OnItemClickListener {
        void onItemCLick(int position);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DeviceAdapter(Context context, List<Devices> datas, RecyclerViewInterFace recyclerViewInterFace){
        this.mContext = context;
        this.mDevices = datas;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.recyclerViewInterFace = recyclerViewInterFace;

    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = mLayoutInflater.inflate(R.layout.activity_recycler_view, parent, false);
        View itemView = mLayoutInflater.inflate(R.layout.activity_card_view, parent, false);

        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {

        //get devices in mDevices via position
        Devices devices = mDevices.get(position);

        String name = devices.getName();

//        bind data to view holder
        holder.tvName.setText(name);
        holder.tvDescription.setText(devices.getDescription());


    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDescription;

        public DeviceViewHolder (View itemView)
        {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.Card1_Text1);
            tvDescription= (TextView) itemView.findViewById(R.id.Card1_Text2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (recyclerViewInterFace != null)
                   {
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION)
                       {
                           recyclerViewInterFace.onItemClick(position);
                       }
                   }
                }
            });
        }
    }
}
