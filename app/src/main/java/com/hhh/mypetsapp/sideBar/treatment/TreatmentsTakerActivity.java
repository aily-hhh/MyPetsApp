package com.hhh.mypetsapp.sideBar.treatment;

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

import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.BaseActivity;
import com.hhh.mypetsapp.R;

import java.util.UUID;

public class TreatmentsTakerActivity extends BaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String name;
    boolean isOld = false;
    String idTreatment;

    private SharedPreferences defPref;
    MediaPlayer mAdd;

    int DIALOG_DATE = 1;
    int myYear = 2020;
    int myMonth = 1;
    int myDay = 1;

    EditText nameTreatment, manufacturerTreatment, dateTreatment, veterinarianTreatment;
    ImageView saveTreatment, backTreatment;
    LinearLayout treatmentTakerLayout;

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
        setContentView(R.layout.activity_treatments_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        nameTreatment = (EditText) findViewById(R.id.nameTreatment);
        manufacturerTreatment = (EditText) findViewById(R.id.manufacturerTreatment);
        dateTreatment = (EditText) findViewById(R.id.dateTreatment);
        veterinarianTreatment = (EditText) findViewById(R.id.veterinarianTreatment);
        backTreatment = (ImageView) findViewById(R.id.backTreatment);
        saveTreatment = (ImageView) findViewById(R.id.saveTreatment);
        treatmentTakerLayout = (LinearLayout) findViewById(R.id.treatmentTakerLayout);

        if (key){
            //dark
            saveTreatment.setColorFilter(R.color.forButtons);
            backTreatment.setColorFilter(R.color.forButtons);
            treatmentTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        idTreatment = getIntent().getStringExtra("oldTreatment");
        infoFromOld();

        backTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToTheTreatments();
            }
        });

        saveTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToDataBase();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mAdd = MediaPlayer.create(this, R.raw.add);
        }
        else {
            mAdd = null;
        }
    }

    private void infoFromOld() {
        if (idTreatment != null) {
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("treatments").document(idTreatment);
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
                        if(snapshot.get("name") != null){
                            nameTreatment.setText(snapshot.get("name").toString());
                        }
                        if(snapshot.get("manufacturer") != null){
                            manufacturerTreatment.setText(snapshot.get("manufacturer").toString());
                        }
                        if(snapshot.get("date") != null){
                            dateTreatment.setText(snapshot.get("date").toString());
                        }
                        if(snapshot.get("veterinarian") != null){
                            veterinarianTreatment.setText(snapshot.get("veterinarian").toString());
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            isOld = true;
        }
    }

    public void onClickDate(View view){
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            dateTreatment.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void addingToDataBase() {
        if (mAdd != null)
            mAdd.start();
        if (!isOld) {
            Treatment newTreatment = new Treatment();

            newTreatment.setId(UUID.randomUUID().toString().trim());
            newTreatment.setName(nameTreatment.getText().toString().trim());
            newTreatment.setManufacturer(manufacturerTreatment.getText().toString().trim());
            newTreatment.setVeterinarian(veterinarianTreatment.getText().toString().trim());
            newTreatment.setDate(dateTreatment.getText().toString().trim());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("treatments").document(newTreatment.getId()).set(newTreatment);
            finish();
        }
        else {
            DocumentReference updateVaccine = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("treatments").document(idTreatment.toString());
            updateVaccine.update("name", nameTreatment.getText().toString().trim());
            updateVaccine.update("manufacturer", manufacturerTreatment.getText().toString().trim());
            updateVaccine.update("date", dateTreatment.getText().toString().trim());
            updateVaccine.update("veterinarian", veterinarianTreatment.getText().toString().trim());
        }

        Intent intent = new Intent(TreatmentsTakerActivity.this, TreatmentFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void backToTheTreatments() {
        finish();
    }
}
