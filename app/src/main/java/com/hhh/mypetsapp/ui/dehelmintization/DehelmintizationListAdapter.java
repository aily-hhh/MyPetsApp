package com.hhh.mypetsapp.ui.dehelmintization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

import java.util.List;

public class DehelmintizationListAdapter extends RecyclerView.Adapter<DehelmintizationViewHolder> {

    Context context;
    List<Dehelmintization> list;
    DehelmintizationClickListener listener;

    public DehelmintizationListAdapter(DehelmintizationFragment context, List<Dehelmintization> list,
                                       DehelmintizationClickListener listener){
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DehelmintizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DehelmintizationViewHolder(LayoutInflater.from(context).inflate(R.layout.dehelmintization_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DehelmintizationViewHolder holder, int position) {
        holder.dateDehelmintizationList.setText(list.get(position).getDate());
        holder.dateDehelmintizationList.setSelected(true);
        holder.doseDehelmintizationList.setText(list.get(position).getDose());
        holder.doseDehelmintizationList.setSelected(true);
        holder.nameDehelmintizationList.setText(list.get(position).getName());
        holder.nameDehelmintizationList.setSelected(true);
        holder.manufacturerDehelmintizationList.setText(list.get(position).getManufacturer());
        holder.manufacturerDehelmintizationList.setSelected(true);
        holder.timeDehelmintizationList.setText(list.get(position).getTime());
        holder.timeDehelmintizationList.setSelected(true);
        if (list.get(position).getVeterinarian() == "") {
            holder.veterinarianDehelmintizationList.setVisibility(View.INVISIBLE);
            holder.veterinarianDescript.setVisibility(View.INVISIBLE);
        }
        else {
            holder.veterinarianDehelmintizationList.setText(list.get(position).getVeterinarian());
            holder.veterinarianDehelmintizationList.setSelected(true);
        }

        holder.dehelmintizationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.dehelmintizationContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.dehelmintizationContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
