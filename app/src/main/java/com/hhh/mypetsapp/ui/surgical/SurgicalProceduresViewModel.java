package com.hhh.mypetsapp.ui.surgical;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hhh.mypetsapp.ui.treatment.Treatment;

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