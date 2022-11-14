package com.hhh.mypetsapp.sideBar.gallery;

import android.view.View;

public interface PhotoListener {
    void onClick(int pos);
    void onLongClick(String gallery, View cardView);
}
