package com.hhh.mypetsapp.sideBar.dehelmintization.ui.adapter;

import androidx.cardview.widget.CardView;

import com.hhh.mypetsapp.sideBar.dehelmintization.model.Dehelmintization;

public interface DehelmintizationClickListener {
    void onClick(Dehelmintization dehelmintization);
    void onLongClick(Dehelmintization dehelmintization, CardView cardView);
}
