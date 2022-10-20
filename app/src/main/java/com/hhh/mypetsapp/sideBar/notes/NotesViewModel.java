package com.hhh.mypetsapp.sideBar.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class NotesViewModel extends ViewModel {
    private final MutableLiveData<List<Notes>> mText;

    public NotesViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<List<Notes>> getText() {
        return mText;
    }
}
