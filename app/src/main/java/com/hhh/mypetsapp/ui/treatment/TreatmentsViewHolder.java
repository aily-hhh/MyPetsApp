package com.hhh.mypetsapp.ui.treatment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class TreatmentsViewHolder extends RecyclerView.ViewHolder{

    CardView treatmentContainer;
    TextView nameTreatment, manufacturerTreatment, dateTreatment, veterinarianTreatment;

    public TreatmentsViewHolder(@NonNull View itemView) {
        super(itemView);

        treatmentContainer = itemView.findViewById(R.id.treatmentContainer);
        nameTreatment = itemView.findViewById(R.id.nameTreatment);
        manufacturerTreatment = itemView.findViewById(R.id.manufacturerTreatment);
        dateTreatment = itemView.findViewById(R.id.dateTreatment);
        veterinarianTreatment = itemView.findViewById(R.id.veterinarianTreatment);
    }
}
