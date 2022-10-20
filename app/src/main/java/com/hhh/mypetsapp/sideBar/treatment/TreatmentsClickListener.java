package com.hhh.mypetsapp.sideBar.treatment;

import androidx.cardview.widget.CardView;

public interface TreatmentsClickListener {
    void onClick(Treatment treatment);
    void onLongClick(Treatment treatment, CardView cardView);
}
