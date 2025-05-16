package com.example.appweather.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.R;
import com.example.appweather.entities.Hourly;
import com.example.appweather.UpdateUI;

import java.util.ArrayList;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {
    ArrayList<Hourly> items;
    Context context;

    public HourlyAdapter(ArrayList<Hourly> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hourly, parent, false);
        return new HourlyViewHolder(view);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        Hourly item = items.get(position);
        context = holder.itemView.getContext();
        holder.textHour.setText(item.getHour());
        holder.textTemp.setText(context.getString(R.string.textTemperature, item.getTemp()));
        int iconResId = UpdateUI.getIconID(item.getPicPath());
        holder.imagePic.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textHour;
        private final TextView textTemp;
        private final ImageView imagePic;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHour = itemView.findViewById(R.id.textHour);
            textTemp = itemView.findViewById(R.id.textTemp);
            imagePic = itemView.findViewById(R.id.imagePic);
        }
    }
}