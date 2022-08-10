package com.hhh.mypetsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhh.mypetsapp.ui.notes.Notes;
import com.hhh.mypetsapp.ui.notes.NotesFragment;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class NotesTakerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ItemViewModel viewModel;
    private String name;

    EditText titleNotesAdd, descriptionNotesAdd;
    ImageView saveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");
        titleNotesAdd = (EditText) findViewById(R.id.titleNotesAdd);
        descriptionNotesAdd = (EditText) findViewById(R.id.descriptionNotesAdd);
        saveNote = (ImageView) findViewById(R.id.saveNote);

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToDataBase();
            }
        });
    }

    private void addingToDataBase() {
        Notes note = new Notes();
        if(!titleNotesAdd.getText().toString().isEmpty())
            note.setTitle(titleNotesAdd.getText().toString());
        else
            Toast.makeText(this, "Enter the title", Toast.LENGTH_SHORT).show();
        note.setDescription(descriptionNotesAdd.getText().toString());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy ',' HH:mm:ss");
        note.setDate(date.toString());


        db.collection("users").document(uID)
                .collection("pets").document(name)
                .collection("notes").document(note.getTitle()).set(note);


        Intent intent = new Intent(NotesTakerActivity.this, NotesFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}