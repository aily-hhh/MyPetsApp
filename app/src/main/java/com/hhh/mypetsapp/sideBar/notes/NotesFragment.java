package com.hhh.mypetsapp.sideBar.notes;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhh.mypetsapp.ItemViewModel;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.FragmentNotesBinding;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    private FragmentNotesBinding binding;
    private SharedPreferences defPref;
    MediaPlayer mDelete;
    MediaPlayer mPin;
    RecyclerView recyclerNotes;
    FloatingActionButton addNotesButton;
    TextView notElemNotes;
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
        recyclerNotes = binding.recyclerNotes;
        addNotesButton = binding.addNotesButton;
        notElemNotes = binding.notElemNotes;

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
    public void onResume() {
        super.onResume();

        notes.clear();
        infoFromDataBase();

        defPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            this.getView().setBackgroundResource(R.drawable.side_nav_bar_dark);
        }
        else {
            //light
            this.getView().setBackgroundResource(R.drawable.side_nav_bar);
        }

        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mDelete = MediaPlayer.create(this.getContext(), R.raw.delete);
            mPin = MediaPlayer.create(this.getContext(), R.raw.pin_unpin);
        }
        else {
            mDelete = null;
            mPin = null;
        }
    }

    public void addNotes() {
        Intent intent = new Intent(NotesFragment.this.getActivity(), NotesTakerActivity.class);
        intent.putExtra("petName", name.toString());
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
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
            intent.putExtra("oldNote", currentNote.id);
            intent.putExtra("petName", name.toString());
            intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
            startActivity(intent);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onLongClick(Notes currentNote, CardView cardView) {
            selectedNote = currentNote;
            showPopUp(cardView);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.notes_menu);
        popupMenu.setForceShowIcon(true);
        popupMenu.show();
    }

    private void infoFromDataBase(){
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("notes")
                .orderBy("date", Query.Direction.DESCENDING)
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
                            if (notes.isEmpty())
                                notElemNotes.setText(R.string.notElem);
                            else
                                notElemNotes.setText("");
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
                            .collection("notes").document(selectedNote.getId());
                    docRef.update("pinned", false);
                    Toast.makeText(NotesFragment.this.getContext(), R.string.unpinned, Toast.LENGTH_SHORT).show();
                    if (mPin != null)
                        mPin.start();
                }
                else{
                    DocumentReference docRef = db.collection("users").document(uID)
                            .collection("pets").document(name)
                            .collection("notes").document(selectedNote.getId());
                    docRef.update("pinned", true);
                    Toast.makeText(NotesFragment.this.getContext(), R.string.pinned, Toast.LENGTH_SHORT).show();
                    if (mPin != null)
                        mPin.start();
                }
                notes.clear();
                infoFromDataBase();
                return true;

            case R.id.deleteMenuNotes:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setTitle(R.string.deleteQuestion);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mDelete != null)
                            mDelete.start();
                        db.collection("users").document(uID)
                                .collection("pets").document(name)
                                .collection("notes").document(selectedNote.getId()).delete();
                        Toast.makeText(NotesFragment.this.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        notes.clear();
                        infoFromDataBase();
                        updateRecycler();
                    }
                });
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
                return true;

            default: return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}