package com.hhh.mypetsapp.ui.vaccines;

import androidx.cardview.widget.CardView;

public interface VaccinesClickListener {
    void onClick(Vaccine vaccine);
    void onLongClick(Vaccine vaccine, CardView cardView);
}
