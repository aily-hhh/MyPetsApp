package com.hhh.mypetsapp.ui.reproduction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

import java.util.List;

public class ReproductionListAdapter extends RecyclerView.Adapter<ReproductionViewHolder> {

    Context context;
    List<Reproduction> list;
    ReproductionClickListener listener;

    public ReproductionListAdapter(ReproductionFragment context, List<Reproduction> list,
                                   ReproductionClickListener listener){
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReproductionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReproductionViewHolder(LayoutInflater.from(context).inflate(R.layout.reproduction_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReproductionViewHolder holder, int position) {
        holder.dateOfHeatList.setText(list.get(position).getDateOfHeat());
        holder.dateOfHeatList.setSelected(true);
        holder.dateOfMatingList.setText(list.get(position).getDateOfMating());
        holder.dateOfMatingList.setSelected(true);
        holder.dateOfBirthList.setText(list.get(position).getDateOfBirth());
        holder.dateOfBirthList.setSelected(true);
        holder.numberOfTheLitterList.setText(list.get(position).getNumberOfTheLitter());
        holder.numberOfTheLitterList.setSelected(true);

        holder.reproductionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.reproductionContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.reproductionContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
