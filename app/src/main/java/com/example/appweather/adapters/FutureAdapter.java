package com.example.appweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.R;
import com.example.appweather.entities.FutureDomain;
import com.example.appweather.UpdateUI;

import java.util.ArrayList;

public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.FutureViewHolder> {
    ArrayList<FutureDomain> items;
    Context context;

    public FutureAdapter(ArrayList<FutureDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FutureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_future, parent, false);
        return new FutureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FutureViewHolder holder, int position) {
        FutureDomain item = items.get(position);
        context = holder.itemView.getContext();
        holder.textDay.setText(item.getDay());
        holder.textStatus.setText(item.getStatus());
        holder.textHigh.setText(context.getString(R.string.temperature, item.getHighTemp()));
        holder.textLow.setText(context.getString(R.string.temperature, item.getLowTemp()));
        int iconResId = UpdateUI.getIconID(item.getPicPath());
        holder.imgPicNext.setImageResource(iconResId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FutureViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDay;
        private final TextView textStatus;
        private final TextView textHigh;
        private final TextView textLow;
        private final ImageView imgPicNext;

        public FutureViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textStatus = itemView.findViewById(R.id.textStatus);
            textHigh = itemView.findViewById(R.id.textHigh);
            textLow = itemView.findViewById(R.id.textLow);
            imgPicNext = itemView.findViewById(R.id.imgPicNext);
        }
    }
}