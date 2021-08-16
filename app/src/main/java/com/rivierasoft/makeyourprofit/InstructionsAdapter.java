package com.rivierasoft.makeyourprofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.AdapterViewHolder> {
    ArrayList<String> instructions;

    public InstructionsAdapter(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instruction, parent, false);
        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(view);
        return adapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.textView.setText(instructions.get(position));
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
        }
    }
}
