package com.hhh.mypetsapp.sideBar.treatment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TreatmentViewModel extends ViewModel {

    private final MutableLiveData<List<Treatment>> mText;

    public TreatmentViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<List<Treatment>> getText() {
        return mText;
    }
}