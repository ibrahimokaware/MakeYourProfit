package com.rivierasoft.makeyourprofit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.AdapterViewHolder> {
    private Context context;
    private ArrayList<Ranking> rankings;

    public RankingAdapter(Context context, ArrayList<Ranking> rankings) {
        this.context = context;
        this.rankings = rankings;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking, parent, false);
        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(view);
        return adapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        Ranking ranking = rankings.get(position);
        holder.numberTextView.setText(ranking.getNumber()+"");
        //if (ranking.getNumber() == 1)
            //holder.numberTextView.setBackgroundResource(R.drawable.circle_green);
        holder.nameTextView.setText(ranking.getName());
        holder.pointsTextView.setText(ranking.getPoints());
        Picasso.with(context)
                .load(ranking.getPhoto())
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder {

        TextView numberTextView, nameTextView, pointsTextView;
        ImageView imageView;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            numberTextView = itemView.findViewById(R.id.textView23);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            pointsTextView = itemView.findViewById(R.id.pointsTextView);
            imageView = itemView.findViewById(R.id.profileImageView);

        }
    }
}
