package com.hhh.mypetsapp.sideBar.surgical;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SurgicalProceduresViewModel extends ViewModel {

    private final MutableLiveData<List<SurgicalProcedures>> mText;

    public SurgicalProceduresViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<List<SurgicalProcedures>> getText() {
        return mText;
    }
}