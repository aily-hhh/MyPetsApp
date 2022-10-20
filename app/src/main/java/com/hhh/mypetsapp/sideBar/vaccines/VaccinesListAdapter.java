package com.hhh.mypetsapp.sideBar.vaccines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

import java.util.List;

public class VaccinesListAdapter extends RecyclerView.Adapter<VaccinesViewHolder>{

    Context context;
    List<Vaccine> list;
    VaccinesClickListener listener;

    public VaccinesListAdapter(VaccinesFragment context, List<Vaccine> list, VaccinesClickListener listener) {
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public VaccinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VaccinesViewHolder(LayoutInflater.from(context).inflate(R.layout.vaccines_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VaccinesViewHolder holder, int position) {
        holder.typeVaccine.setText(list.get(position).getType());
        holder.typeVaccine.setSelected(true);
        holder.nameVaccine.setText(list.get(position).getName());
        holder.nameVaccine.setSelected(true);
        holder.manufacturerVaccine.setText(list.get(position).getManufacturer());
        holder.manufacturerVaccine.setSelected(true);
        holder.dateVaccine.setText(list.get(position).getDateOfVaccination() + "/" + list.get(position).getValidUntil());
        holder.dateVaccine.setSelected(true);
        holder.veterinarian.setText(list.get(position).getVeterinarian());
        holder.veterinarian.setSelected(true);

        holder.vaccinesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.vaccinesContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.vaccinesContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
