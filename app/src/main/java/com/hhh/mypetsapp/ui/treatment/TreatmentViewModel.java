package com.hhh.mypetsapp.ui.treatment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TreatmentViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TreatmentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is treatment from external parasites fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}