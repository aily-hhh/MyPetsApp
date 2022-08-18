package com.hhh.mypetsapp.ui.treatment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.ui.vaccines.Vaccine;
import com.hhh.mypetsapp.ui.vaccines.VaccinesClickListener;
import com.hhh.mypetsapp.ui.vaccines.VaccinesFragment;
import com.hhh.mypetsapp.ui.vaccines.VaccinesViewHolder;

import java.util.List;

public class TreatmentsListAdapter extends RecyclerView.Adapter<TreatmentsViewHolder>{

    Context context;
    List<Treatment> list;
    TreatmentsClickListener listener;

    public TreatmentsListAdapter(TreatmentFragment context, List<Treatment> list, TreatmentsClickListener listener) {
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TreatmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TreatmentsViewHolder(LayoutInflater.from(context).inflate(R.layout.treatments_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentsViewHolder holder, int position) {
        holder.nameTreatment.setText(list.get(position).getName());
        holder.nameTreatment.setSelected(true);
        holder.manufacturerTreatment.setText(list.get(position).getManufacturer());
        holder.manufacturerTreatment.setSelected(true);
        holder.dateTreatment.setText(list.get(position).getDate());
        holder.dateTreatment.setSelected(true);
        holder.veterinarianTreatment.setText(list.get(position).getVeterinarian());
        holder.veterinarianTreatment.setSelected(true);

        holder.treatmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.treatmentContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.treatmentContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
