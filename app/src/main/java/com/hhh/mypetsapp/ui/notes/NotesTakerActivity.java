package com.hhh.mypetsapp.ui.notes;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.BaseActivity;
import com.hhh.mypetsapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class NotesTakerActivity extends BaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String name;
    boolean isOld = false;
    private SharedPreferences defPref;
    MediaPlayer mClick;
    MediaPlayer mAdd;

    LinearLayout notesTakerLayout;
    EditText titleNotesAdd, descriptionNotesAdd;
    ImageView saveNote;
    ImageView backNote;
    String noteId;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ',' HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            setTheme(R.style.Theme_MyPetsApp_Dark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        titleNotesAdd = (EditText) findViewById(R.id.titleNotesAdd);
        descriptionNotesAdd = (EditText) findViewById(R.id.descriptionNotesAdd);
        saveNote = (ImageView) findViewById(R.id.saveNote);
        backNote = (ImageView) findViewById(R.id.backNote);
        notesTakerLayout = (LinearLayout) findViewById(R.id.notesTakerLayout);

        if (key){
            //dark
            saveNote.setColorFilter(R.color.forButtons);
            backNote.setColorFilter(R.color.forButtons);
            notesTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        noteId = getIntent().getStringExtra("oldNote");
        if (noteId != null) {
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(noteId);
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

    @Override
    protected void onResume() {
        super.onResume();
        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mClick = MediaPlayer.create(this, R.raw.click);
            mAdd = MediaPlayer.create(this, R.raw.add);
        }
        else {
            mClick = null;
            mAdd = null;
        }
    }

    private void backToTheNotes() {
        if (mClick != null)
            mClick.start();
        finish();
    }

    private void addingToDataBase() {
        if (mAdd != null)
            mAdd.start();

        if (!isOld) {
            Notes newNote = new Notes();

            if (!titleNotesAdd.getText().toString().isEmpty())
                newNote.setTitle(titleNotesAdd.getText().toString().trim());
            else {
                Toast.makeText(this, "Enter the title", Toast.LENGTH_SHORT).show();
                return;
            }

            newNote.setDescription(descriptionNotesAdd.getText().toString().trim());
            String date = dateFormat.format(calendar.getTime());
            newNote.setDate(date);

            newNote.setId(UUID.randomUUID().toString());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(newNote.getId()).set(newNote);
        }
        else {
            String date = dateFormat.format(calendar.getTime());
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("notes").document(noteId);
            docRef.update("title", titleNotesAdd.getText().toString().trim());
            docRef.update("description", descriptionNotesAdd.getText().toString().trim());
            docRef.update("date", date);
        }


        Intent intent = new Intent(NotesTakerActivity.this, NotesFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}