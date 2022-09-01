package com.hhh.mypetsapp.ui.dehelmintization;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DehelmintizationViewModel extends ViewModel {
    private final MutableLiveData<List<Dehelmintization>> mText;

    public DehelmintizationViewModel() {
        this.mText = new MutableLiveData<>();
    }

    public LiveData<List<Dehelmintization>> getText(){
        return mText;
    }
}
