package com.rivierasoft.makeyourprofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.AdapterViewHolder> {
    ArrayList<Profit> profits;
    OnProfitRecyclerViewItemClickListener listener;

    public ProfitAdapter(ArrayList<Profit> profits, OnProfitRecyclerViewItemClickListener listener) {
        this.profits = profits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profit, parent, false);
        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(view);
        return adapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        Profit profit = profits.get(position);
        holder.imageView.setImageResource(profit.getImage());
        holder.textView.setText(profit.getName());
    }

    @Override
    public int getItemCount() {
        return profits.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        Button button;
        CardView cardView;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        listener.OnClickListener(getAdapterPosition());
                }
            });
        }
    }
}
