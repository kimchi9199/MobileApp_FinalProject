package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp_final.R;
import com.example.mobileapp_final.Recycler_View;

import java.util.List;

import Model.Devices;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private static final String TAG = "DeviceAdapter";
    private List<Devices> mDevices; // Show list devices
    private Context mContext; //
    private LayoutInflater mLayoutInflater;//Sap xep du lieu vao Card View

    public DeviceAdapter(Context context, List<Devices> datas){
        mContext = context;
        mDevices = datas;
        mLayoutInflater=LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.activity_recycler_view, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {

        //get devices in mDevices via position
        Devices devices = mDevices.get(position);

        String name = devices.getName();

        //bind data to view holder
//        holder.tvName.setText(name);
//        holder.tvDescription.setText(devices.getDescription());

        Log.d("TEST", "OK");
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
        }
    }
}
