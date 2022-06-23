package com.hhh.mypetsapp.ui.procedures;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProceduresViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProceduresViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is procedures fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}