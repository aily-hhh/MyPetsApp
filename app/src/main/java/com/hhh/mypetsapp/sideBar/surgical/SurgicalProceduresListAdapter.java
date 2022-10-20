package com.hhh.mypetsapp.sideBar.surgical;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

import java.util.List;

public class SurgicalProceduresListAdapter extends RecyclerView.Adapter<SurgicalProceduresViewHolder>{

    Context context;
    List<SurgicalProcedures> list;
    SurgicalProceduresClickListener listener;

    public SurgicalProceduresListAdapter(SurgicalProceduresFragment context, List<SurgicalProcedures> list,
                                         SurgicalProceduresClickListener listener) {
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SurgicalProceduresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SurgicalProceduresViewHolder(LayoutInflater.from(context).inflate(R.layout.surgical_procedures_list,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurgicalProceduresViewHolder holder, int position) {
        holder.typeSurgicalProcedureL.setText(list.get(position).getType());
        holder.typeSurgicalProcedureL.setSelected(true);
        holder.veterinarianSurgicalProcedureL.setText(list.get(position).getVeterinarian());
        holder.veterinarianSurgicalProcedureL.setSelected(true);
        holder.anesthesiaSurgicalProcedureL.setText(list.get(position).getAnesthesia());
        holder.anesthesiaSurgicalProcedureL.setSelected(true);
        holder.nameSurgicalProcedureL.setText(list.get(position).getName());
        holder.nameSurgicalProcedureL.setSelected(true);
        holder.dateSurgicalProcedureL.setText(list.get(position).getDate());
        holder.dateSurgicalProcedureL.setSelected(true);

        holder.surgicalProceduresContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.surgicalProceduresContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.surgicalProceduresContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
