package com.hhh.mypetsapp.ui.reproduction;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.R;

import java.util.UUID;

public class ReproductionTakerActivity extends Activity {

    ImageView saveReproduction, backReproduction;
    EditText dateOfHeatTaker, dateOfMatingTaker, dateOfBirthTaker, numberOfTheLitterTaker;
    LinearLayout reproductionTakerLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private String name;
    private boolean isOld = false;
    String idReproduction;
    private SharedPreferences defPref;
    MediaPlayer mClick;
    MediaPlayer mAdd;

    int DIALOG_DATE_HEAT = 1;
    int DIALOG_DATE_MATING = 2;
    int DIALOG_DATE_BIRTH = 3;
    int myYear = 2022;
    int myMonth = 1;
    int myDay = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_reproduction_taker);

        saveReproduction = (ImageView) findViewById(R.id.saveReproduction);
        backReproduction = (ImageView) findViewById(R.id.backReproduction);
        dateOfHeatTaker = (EditText) findViewById(R.id.dateOfHeatTaker);
        dateOfMatingTaker = (EditText) findViewById(R.id.dateOfMatingTaker);
        dateOfBirthTaker = (EditText) findViewById(R.id.dateOfBirthTaker);
        numberOfTheLitterTaker = (EditText) findViewById(R.id.numberOfTheLitterTaker);
        reproductionTakerLayout = (LinearLayout) findViewById(R.id.reproductionTakerLayout);

        if (key){
            //dark
            saveReproduction.setColorFilter(R.color.forButtons);
            backReproduction.setColorFilter(R.color.forButtons);
            reproductionTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        idReproduction = getIntent().getStringExtra("oldReproduction");
        infoFromOld();

        saveReproduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToDataBase();
            }
        });

        backReproduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClick != null)
                    mClick.start();
                finish();
            }
        });

        dateOfHeatTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDateHeat();
            }
        });

        dateOfMatingTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDateMating();
            }
        });

        dateOfBirthTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDateBirth();
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

    private void infoFromOld() {
        if (idReproduction != null){
            DocumentReference documentReference = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("reproduction").document(idReproduction);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if(snapshot.get("dateOfHeat") != null){
                            dateOfHeatTaker.setText(snapshot.get("dateOfHeat").toString());
                        }
                        if(snapshot.get("dateOfMating") != null){
                            dateOfMatingTaker.setText(snapshot.get("dateOfMating").toString());
                        }
                        if(snapshot.get("dateOfBirth") != null){
                            dateOfBirthTaker.setText(snapshot.get("dateOfBirth").toString());
                        }
                        if(snapshot.get("numberOfTheLitter") != null){
                            numberOfTheLitterTaker.setText(snapshot.get("numberOfTheLitter").toString());
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            isOld = true;
        }
    }

    private void onClickDateBirth() {
        showDialog(DIALOG_DATE_BIRTH);
    }

    private void onClickDateMating() {
        showDialog(DIALOG_DATE_MATING);
    }

    private void onClickDateHeat() {
        showDialog(DIALOG_DATE_HEAT);
    }

    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE_HEAT){
            DatePickerDialog dpd = new DatePickerDialog(this, myCallBackHeat, myYear, myMonth, myDay);
            return dpd;
        }
        if (id == DIALOG_DATE_MATING){
            DatePickerDialog dpd = new DatePickerDialog(this, myCallBackMating, myYear, myMonth, myDay);
            return dpd;
        }
        if (id == DIALOG_DATE_BIRTH){
            DatePickerDialog dpd = new DatePickerDialog(this, myCallBackBirth, myYear, myMonth, myDay);
            return dpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBackHeat = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            dateOfHeatTaker.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    DatePickerDialog.OnDateSetListener myCallBackMating = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            dateOfMatingTaker.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    DatePickerDialog.OnDateSetListener myCallBackBirth = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            dateOfBirthTaker.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void addingToDataBase() {
        if (!isOld){
            if (mAdd != null)
                mAdd.start();
            Reproduction newReproduction = new Reproduction();

            newReproduction.setId(UUID.randomUUID().toString().trim());
            newReproduction.setDateOfHeat(dateOfHeatTaker.getText().toString().trim());
            newReproduction.setDateOfMating(dateOfMatingTaker.getText().toString().trim());
            newReproduction.setDateOfBirth(dateOfBirthTaker.getText().toString().trim());
            newReproduction.setNumberOfTheLitter(numberOfTheLitterTaker.getText().toString().trim());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("reproduction").document(newReproduction.getId())
                    .set(newReproduction);
        }
        else {
            DocumentReference documentReference = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("reproduction").document(idReproduction);
            documentReference.update("dateOfHeat", dateOfHeatTaker.getText().toString().trim());
            documentReference.update("dateOfMating", dateOfMatingTaker.getText().toString().trim());
            documentReference.update("dateOfBirth", dateOfBirthTaker.getText().toString().trim());
            documentReference.update("numberOfTheLitter", numberOfTheLitterTaker.getText().toString().trim());
        }

        Intent intent = new Intent(ReproductionTakerActivity.this, ReproductionFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
