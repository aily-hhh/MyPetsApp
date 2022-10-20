package com.hhh.mypetsapp.sideBar.vaccines;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class VaccinesViewHolder extends RecyclerView.ViewHolder{

    CardView vaccinesContainer;
    TextView typeVaccine, nameVaccine, manufacturerVaccine, dateVaccine, veterinarian, treatmentHeader;

    public VaccinesViewHolder(@NonNull View itemView) {
        super(itemView);

        vaccinesContainer = itemView.findViewById(R.id.vaccinesContainer);
        typeVaccine = itemView.findViewById(R.id.typeVaccine);
        nameVaccine = itemView.findViewById(R.id.nameVaccine);
        manufacturerVaccine = itemView.findViewById(R.id.manufacturerVaccine);
        dateVaccine = itemView.findViewById(R.id.dateVaccine);
        veterinarian = itemView.findViewById(R.id.veterinarianVaccine);
    }
}
