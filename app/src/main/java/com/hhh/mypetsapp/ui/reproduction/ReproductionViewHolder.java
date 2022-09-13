package com.hhh.mypetsapp.ui.reproduction;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class ReproductionViewHolder extends RecyclerView.ViewHolder {

    CardView reproductionContainer;
    TextView dateOfBirthList, dateOfHeatList, dateOfMatingList, numberOfTheLitterList;

    public ReproductionViewHolder(@NonNull View itemView) {
        super(itemView);

        reproductionContainer = itemView.findViewById(R.id.reproductionContainer);
        dateOfHeatList = itemView.findViewById(R.id.dateOfHeatList);
        dateOfMatingList = itemView.findViewById(R.id.dateOfMatingList);
        dateOfBirthList = itemView.findViewById(R.id.dateOfBirthList);
        numberOfTheLitterList = itemView.findViewById(R.id.numberOfTheLitterList);
    }
}
