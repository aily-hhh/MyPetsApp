package com.hhh.mypetsapp.ui.notes;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhh.mypetsapp.ItemViewModel;
import com.hhh.mypetsapp.NotesTakerActivity;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.FragmentNotesBinding;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    private FragmentNotesBinding binding;

    RecyclerView recyclerNotes;
    FloatingActionButton addNotesButton;
    NotesListAdapter notesListAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<Notes> notes = new ArrayList<>();
    private ItemViewModel viewModel;
    private String name;
    Notes selectedNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View view = inflater.inflate(R.layout.fragment_notes, null);
        recyclerNotes = binding.recyclerNotes;
        addNotesButton = binding.addNotesButton;

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        addNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotes();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        notes.clear();
        infoFromDataBase();
    }

    public void addNotes() {
        Intent intent = new Intent(NotesFragment.this.getActivity(), NotesTakerActivity.class);
        intent.putExtra("petName", name.toString());
        startActivity(intent);
    }

    private void updateRecycler() {
        recyclerNotes.setHasFixedSize(true);
        recyclerNotes.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(NotesFragment.this, notes, notesClickListener);
        recyclerNotes.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes currentNote) {
            Intent intent = new Intent(NotesFragment.this.getActivity(), NotesTakerActivity.class);
            intent.putExtra("oldNote", currentNote.title);
            intent.putExtra("petName", name.toString());
            startActivity(intent);
        }

        @Override
        public void onLongClick(Notes currentNote, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = currentNote;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.notes_menu);
        popupMenu.show();
    }

    private void infoFromDataBase(){
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Notes newNote = document.toObject(Notes.class);
                                notes.add(newNote);
                                updateRecycler();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.pinMenuNotes:
                if (selectedNote.isPinned()){
                    DocumentReference docRef = db.collection("users").document(uID)
                            .collection("pets").document(name)
                            .collection("notes").document(selectedNote.getTitle());
                    docRef.update("pinned", false);
                    Toast.makeText(NotesFragment.this.getContext(), "Unpinned", Toast.LENGTH_SHORT).show();
                }
                else{
                    DocumentReference docRef = db.collection("users").document(uID)
                            .collection("pets").document(name)
                            .collection("notes").document(selectedNote.getTitle());
                    docRef.update("pinned", true);
                    Toast.makeText(NotesFragment.this.getContext(), "Pinned", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                infoFromDataBase();
                return true;

            case R.id.deleteMenuNotes:
                db.collection("users").document(uID)
                        .collection("pets").document(name)
                        .collection("notes").document(selectedNote.getTitle()).delete();
                Toast.makeText(NotesFragment.this.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                notes.clear();
                infoFromDataBase();
                return true;

            default: return false;
        }
    }
}