package com.hhh.mypetsapp.ui.treatment;

import androidx.cardview.widget.CardView;

public interface TreatmentsClickListener {
    void onClick(Treatment treatment);
    void onLongClick(Treatment treatment, CardView cardView);
}
