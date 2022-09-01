package com.hhh.mypetsapp.ui.reproduction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ReproductionViewModel extends ViewModel {
    private final MutableLiveData<List<Reproduction>> mText;

    public ReproductionViewModel() {
        this.mText = new MutableLiveData<>();
    }

    public LiveData<List<Reproduction>> getText(){
        return mText;
    }
}
