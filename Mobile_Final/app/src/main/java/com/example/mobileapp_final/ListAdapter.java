package com.example.mobileapp_final;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private Context context;
    private List<User> dataList;
    public ListAdapter(Context context, List<User> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recycleitem, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.inforImage);
        holder.inforDate.setText(dataList.get(position).getDate());
        holder.inforDesc.setText(dataList.get(position).getDesc());

        holder.inforCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDesc());
                intent.putExtra("Date", dataList.get(holder.getAdapterPosition()).getDate());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataList(ArrayList<User> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView inforImage;
    TextView inforDate, inforDesc;
    CardView inforCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        inforImage = itemView.findViewById(R.id.recImage);
        inforDate = itemView.findViewById(R.id.recDate);
        inforDesc = itemView.findViewById(R.id.recDesc);
        inforCard = itemView.findViewById(R.id.recCard);


    }

}
