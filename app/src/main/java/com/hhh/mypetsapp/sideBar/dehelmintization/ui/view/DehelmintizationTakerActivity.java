package com.hhh.mypetsapp.sideBar.dehelmintization.ui.view;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.BaseActivity;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.sideBar.dehelmintization.model.Dehelmintization;

import java.util.UUID;

public class DehelmintizationTakerActivity extends BaseActivity {

    ImageView saveDehelmintization, backDehelmintization;
    EditText nameDehelmintizationTaker, manufacturerDehelmintizationTaker, doseDehelmintizationTaker,
            dateDehelmintizationTaker, timeDehelmintizationTaker, veterinarianDehelmintizationTaker,
            descriptionDehelmintizationTaker;
    LinearLayout dehelmintizationTakerLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private String name;
    private boolean isOld = false;
    String idDehelmintization;
    private SharedPreferences defPref;
    MediaPlayer mAdd;

    int DIALOG_DATE = 1;
    int DIALOG_TIME = 2;
    int myYear = 2022;
    int myMonth = 1;
    int myDay = 1;
    int myHour = 12;
    int myMin = 30;


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
        setContentView(R.layout.activity_dehelmintization_taker);

        saveDehelmintization = (ImageView) findViewById(R.id.saveDehelmintization);
        backDehelmintization = (ImageView) findViewById(R.id.backDehelmintization);
        nameDehelmintizationTaker = (EditText) findViewById(R.id.nameDehelmintizationTaker);
        manufacturerDehelmintizationTaker = (EditText) findViewById(R.id.manufacturerDehelmintizationTaker);
        doseDehelmintizationTaker = (EditText) findViewById(R.id.doseDehelmintizationTaker);
        dateDehelmintizationTaker = (EditText) findViewById(R.id.dateDehelmintizationTaker);
        timeDehelmintizationTaker = (EditText) findViewById(R.id.timeDehelmintizationTaker);
        veterinarianDehelmintizationTaker = (EditText) findViewById(R.id.veterinarianDehelmintizationTaker);
        descriptionDehelmintizationTaker = (EditText) findViewById(R.id.descriptionDehelmintizationTaker);
        dehelmintizationTakerLayout = (LinearLayout) findViewById(R.id.dehelmintizationTakerLayout);

        if (key){
            //dark
            saveDehelmintization.setColorFilter(R.color.forButtons);
            backDehelmintization.setColorFilter(R.color.forButtons);
            dehelmintizationTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        idDehelmintization = getIntent().getStringExtra("oldDehelmintization");
        infoFromOld();

        saveDehelmintization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToDataBase();
            }
        });

        backDehelmintization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dateDehelmintizationTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDate();
            }
        });

        timeDehelmintizationTaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTime();
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
        if (idDehelmintization != null){
            DocumentReference documentReference = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("dehelmintization").document(idDehelmintization);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if(snapshot.get("name") != null){
                            nameDehelmintizationTaker.setText(snapshot.get("name").toString());
                        }
                        if(snapshot.get("description") != null){
                            descriptionDehelmintizationTaker.setText(snapshot.get("description").toString());
                        }
                        if(snapshot.get("date") != null){
                            dateDehelmintizationTaker.setText(snapshot.get("date").toString());
                        }
                        if(snapshot.get("veterinarian") != null){
                            veterinarianDehelmintizationTaker.setText(snapshot.get("veterinarian").toString());
                        }
                        if(snapshot.get("manufacturer") != null){
                            manufacturerDehelmintizationTaker.setText(snapshot.get("manufacturer").toString());
                        }
                        if(snapshot.get("dose") != null){
                            doseDehelmintizationTaker.setText(snapshot.get("dose").toString());
                        }
                        if(snapshot.get("time") != null){
                            timeDehelmintizationTaker.setText(snapshot.get("time").toString());
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            isOld = true;
        }
    }

    private void onClickTime() {
        showDialog(DIALOG_TIME);
    }

    private void onClickDate() {
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE){
            DatePickerDialog dpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return dpd;
        }
        if (id == DIALOG_TIME){
            TimePickerDialog tpd = new TimePickerDialog(this, myTimeBack, myHour, myMin, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myTimeBack = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            myHour = hour;
            myMin = min;
            timeDehelmintizationTaker.setText(myHour + ":" + myMin);
        }
    };

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            dateDehelmintizationTaker.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void addingToDataBase() {
        if (!isOld){
            Dehelmintization newDehelmintization = new Dehelmintization();

            newDehelmintization.setId(UUID.randomUUID().toString().trim());
            newDehelmintization.setName(nameDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setManufacturer(manufacturerDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setDose(doseDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setVeterinarian(veterinarianDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setDescription(descriptionDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setDate(dateDehelmintizationTaker.getText().toString().trim());
            newDehelmintization.setTime(timeDehelmintizationTaker.getText().toString().trim());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("dehelmintization").document(newDehelmintization.getId())
                    .set(newDehelmintization);
        }
        else {
            DocumentReference documentReference = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("dehelmintization").document(idDehelmintization);
            documentReference.update("name", nameDehelmintizationTaker.getText().toString().trim());
            documentReference.update("manufacturer", manufacturerDehelmintizationTaker.getText().toString().trim());
            documentReference.update("dose", doseDehelmintizationTaker.getText().toString().trim());
            documentReference.update("date", dateDehelmintizationTaker.getText().toString().trim());
            documentReference.update("time", timeDehelmintizationTaker.getText().toString().trim());
            documentReference.update("veterinarian", veterinarianDehelmintizationTaker.getText().toString().trim());
            documentReference.update("description", descriptionDehelmintizationTaker.getText().toString());
        }

        Intent intent = new Intent(DehelmintizationTakerActivity.this, DehelmintizationFragment.class);
        setResult(Activity.RESULT_OK, intent);
        if (mAdd != null)
            mAdd.start();
        startActivity(intent);
        finish();
    }
}
