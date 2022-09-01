package com.hhh.mypetsapp.ui.identification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class IdentificationViewModel extends ViewModel {
    private MutableLiveData<List<Identification>> mText;

    public IdentificationViewModel() {
        this.mText = new MutableLiveData<>();
    }

    public LiveData<List<Identification>> getText(){
        return mText;
    }
}
