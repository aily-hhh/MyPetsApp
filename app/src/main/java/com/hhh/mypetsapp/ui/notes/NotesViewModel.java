package com.hhh.mypetsapp.ui.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhh.mypetsapp.ItemViewModel;

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
