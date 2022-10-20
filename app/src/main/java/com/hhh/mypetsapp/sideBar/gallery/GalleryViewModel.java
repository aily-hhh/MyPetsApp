package com.hhh.mypetsapp.sideBar.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<List<String>> getText() {
        return mText;
    }
}