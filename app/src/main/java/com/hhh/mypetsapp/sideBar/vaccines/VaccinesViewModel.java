package com.hhh.mypetsapp.sideBar.vaccines;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VaccinesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VaccinesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is vaccines fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}