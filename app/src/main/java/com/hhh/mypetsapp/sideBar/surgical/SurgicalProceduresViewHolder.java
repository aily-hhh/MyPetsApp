package com.hhh.mypetsapp.sideBar.surgical;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class SurgicalProceduresViewHolder extends RecyclerView.ViewHolder{

    CardView surgicalProceduresContainer;
    TextView typeSurgicalProcedureL, dateSurgicalProcedureL, nameSurgicalProcedureL,
            anesthesiaSurgicalProcedureL, veterinarianSurgicalProcedureL;

    public SurgicalProceduresViewHolder(@NonNull View itemView) {
        super(itemView);

        surgicalProceduresContainer = itemView.findViewById(R.id.surgicalProceduresContainer);
        dateSurgicalProcedureL = itemView.findViewById(R.id.dateSurgicalProcedureL);
        nameSurgicalProcedureL = itemView.findViewById(R.id.nameSurgicalProcedureL);
        anesthesiaSurgicalProcedureL = itemView.findViewById(R.id.anesthesiaSurgicalProcedureL);
        veterinarianSurgicalProcedureL = itemView.findViewById(R.id.veterinarianSurgicalProcedureL);
        typeSurgicalProcedureL = itemView.findViewById(R.id.typeSurgicalProcedureL);
    }
}
