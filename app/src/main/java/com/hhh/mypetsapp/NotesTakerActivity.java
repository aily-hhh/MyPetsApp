package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.ui.notes.Notes;
import com.hhh.mypetsapp.ui.notes.NotesFragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotesTakerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ItemViewModel viewModel;
    private String name;
    boolean isOld = false;

    EditText titleNotesAdd, descriptionNotesAdd;
    ImageView saveNote;
    ImageView backNote;
    String noteTitle;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ',' HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        titleNotesAdd = (EditText) findViewById(R.id.titleNotesAdd);
        descriptionNotesAdd = (EditText) findViewById(R.id.descriptionNotesAdd);
        saveNote = (ImageView) findViewById(R.id.saveNote);
        backNote = (ImageView) findViewById(R.id.backNote);

        noteTitle = getIntent().getStringExtra("oldNote");
        if (noteTitle != null) {
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(noteTitle);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        titleNotesAdd.setText(snapshot.get("title").toString());
                        titleNotesAdd.setEnabled(false);
                        if(snapshot.get("description") != null)
                            descriptionNotesAdd.setText(snapshot.get("description").toString());
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            isOld = true;
        }

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToDataBase();
            }
        });

        backNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToTheNotes();
            }
        });
    }

    private void backToTheNotes() {
        finish();
    }

    private void addingToDataBase() {
        if (!isOld) {
            Notes newNote = new Notes();

            if (!titleNotesAdd.getText().toString().isEmpty())
                newNote.setTitle(titleNotesAdd.getText().toString());
            else {
                Toast.makeText(this, "Enter the title", Toast.LENGTH_SHORT).show();
                return;
            }

            newNote.setDescription(descriptionNotesAdd.getText().toString());
            String date = dateFormat.format(calendar.getTime());
            newNote.setDate(date);

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(newNote.getTitle()).set(newNote);
        }
        else {
            String date = dateFormat.format(calendar.getTime());
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(noteTitle);
            docRef.update("description", descriptionNotesAdd.getText().toString());
            docRef.update("date", date);
        }


        Intent intent = new Intent(NotesTakerActivity.this, NotesFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}