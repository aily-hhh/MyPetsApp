package com.hhh.mypetsapp.sideBar.reproduction;

import androidx.cardview.widget.CardView;

public interface ReproductionClickListener {
    void onClick(Reproduction reproduction);
    void onLongClick(Reproduction reproduction, CardView cardView);
}
