package com.rivierasoft.makeyourprofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.AdapterViewHolder> {
    ArrayList<Level> levels;
    OnRecyclerViewItemClickListener listener;

    public LevelAdapter(ArrayList<Level> levels, OnRecyclerViewItemClickListener listener) {
        this.levels = levels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level, parent, false);
        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(view);
        return adapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        Level level = levels.get(position);
        holder.imageView.setImageResource(level.getImage());
        holder.textView.setText(level.getTitle());
        if (level.isOpen())
            holder.linearLayout.setEnabled(true);
        else  holder.linearLayout.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        listener.OnClickListener(getAdapterPosition(), linearLayout);
                }
            });
        }
    }
}
