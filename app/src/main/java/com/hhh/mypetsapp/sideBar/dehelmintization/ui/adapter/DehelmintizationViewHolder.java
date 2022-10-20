package com.hhh.mypetsapp.sideBar.dehelmintization.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class DehelmintizationViewHolder extends RecyclerView.ViewHolder {

    CardView dehelmintizationContainer;
    TextView dateDehelmintizationList, nameDehelmintizationList,manufacturerDehelmintizationList,
            timeDehelmintizationList, doseDehelmintizationList, veterinarianDehelmintizationList,
            veterinarianDescript;

    public DehelmintizationViewHolder(@NonNull View itemView) {
        super(itemView);

        dehelmintizationContainer = itemView.findViewById(R.id.dehelmintizationContainer);
        dateDehelmintizationList = itemView.findViewById(R.id.dateDehelmintizationList);
        nameDehelmintizationList = itemView.findViewById(R.id.nameDehelmintizationList);
        manufacturerDehelmintizationList = itemView.findViewById(R.id.manufacturerDehelmintizationList);
        timeDehelmintizationList = itemView.findViewById(R.id.timeDehelmintizationList);
        doseDehelmintizationList = itemView.findViewById(R.id.doseDehelmintizationList);
        veterinarianDehelmintizationList = itemView.findViewById(R.id.veterinarianDehelmintizationList);
        veterinarianDescript = itemView.findViewById(R.id.veterinarianDescript);
    }
}
