package com.hhh.mypetsapp.ui.surgical;

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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.ui.vaccines.Vaccine;
import com.hhh.mypetsapp.ui.vaccines.VaccinesFragment;
import com.hhh.mypetsapp.ui.vaccines.VaccinesTakerActivity;

import java.util.UUID;

public class SurgicalProceduresTakerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String name;
    boolean isOld = false;
    String idSurgical;
    ArrayAdapter<CharSequence> adapterType;

    private SharedPreferences defPref;
    MediaPlayer mClick;
    MediaPlayer mAdd;

    int DIALOG_DATE = 1;
    int myYear = 2020;
    int myMonth = 1;
    int myDay = 1;

    Spinner spinnerTypeSurgicalProcedure;
    EditText nameSurgicalProcedure, anesthesiaSurgicalProcedure, dateSurgicalProcedure,
            veterinarianSurgicalProcedure, descriptionSurgicalProcedure;
    ImageView saveSurgicalProcedures, backSurgicalProcedures;
    LinearLayout surgicalProceduresTakerLayout;

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
        setContentView(R.layout.activity_surgical_procedures_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        spinnerTypeSurgicalProcedure = (Spinner) findViewById(R.id.spinnerTypeSurgicalProcedure);
        nameSurgicalProcedure = (EditText) findViewById(R.id.nameSurgicalProcedure);
        anesthesiaSurgicalProcedure = (EditText) findViewById(R.id.anesthesiaSurgicalProcedure);
        dateSurgicalProcedure = (EditText) findViewById(R.id.dateSurgicalProcedure);
        veterinarianSurgicalProcedure = (EditText) findViewById(R.id.veterinarianSurgicalProcedure);
        descriptionSurgicalProcedure = (EditText) findViewById(R.id.descriptionSurgicalProcedure);
        saveSurgicalProcedures = (ImageView) findViewById(R.id.saveSurgicalProcedures);
        backSurgicalProcedures = (ImageView) findViewById(R.id.backSurgicalProcedures);
        surgicalProceduresTakerLayout = (LinearLayout) findViewById(R.id.surgicalProceduresTakerLayout);

        if (key){
            //dark
            saveSurgicalProcedures.setColorFilter(R.color.forButtons);
            backSurgicalProcedures.setColorFilter(R.color.forButtons);
            surgicalProceduresTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        idSurgical = getIntent().getStringExtra("oldSurgical");
        infoFromOld();

        adapterType = ArrayAdapter.createFromResource(this,
                R.array.typeSurgicalArray, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeSurgicalProcedure.setAdapter(adapterType);
        int pos = adapterType.getPosition(spinnerTypeSurgicalProcedure.getSelectedItem().toString());
        spinnerTypeSurgicalProcedure.setSelection(pos);

        backSurgicalProcedures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToTheSurgical();
            }
        });

        saveSurgicalProcedures.setOnClickListener(new View.OnClickListener() {
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
            mClick = MediaPlayer.create(this, R.raw.click);
            mAdd = MediaPlayer.create(this, R.raw.add);
        }
        else {
            mClick = null;
            mAdd = null;
        }
    }

    private void infoFromOld() {
        if (idSurgical != null) {
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("surgical").document(idSurgical);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                    @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        if(snapshot.get("name") != null){
                            nameSurgicalProcedure.setText(snapshot.get("name").toString());
                        }
                        if(snapshot.get("description") != null){
                            descriptionSurgicalProcedure.setText(snapshot.get("description").toString());
                        }
                        if(snapshot.get("date") != null){
                            dateSurgicalProcedure.setText(snapshot.get("date").toString());
                        }
                        if(snapshot.get("veterinarian") != null){
                            veterinarianSurgicalProcedure.setText(snapshot.get("veterinarian").toString());
                        }
                        if(snapshot.get("anesthesia") != null){
                            veterinarianSurgicalProcedure.setText(snapshot.get("anesthesia").toString());
                        }
                        if(snapshot.get("type") != null){
                            int pos = adapterType.getPosition(snapshot.get("type").toString());
                            spinnerTypeSurgicalProcedure.setSelection(pos);
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
            DatePickerDialog dpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return dpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            dateSurgicalProcedure.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void addingToDataBase() {
        if (mAdd != null)
            mAdd.start();
        if (!isOld) {
            SurgicalProcedures newSurgicalProcedures = new SurgicalProcedures();

            newSurgicalProcedures.setId(UUID.randomUUID().toString().trim());
            newSurgicalProcedures.setName(nameSurgicalProcedure.getText().toString().trim());
            newSurgicalProcedures.setDescription(descriptionSurgicalProcedure.getText().toString().trim());
            newSurgicalProcedures.setVeterinarian(veterinarianSurgicalProcedure.getText().toString().trim());
            newSurgicalProcedures.setType(spinnerTypeSurgicalProcedure.getSelectedItem().toString().trim());
            newSurgicalProcedures.setDate(dateSurgicalProcedure.getText().toString().trim());
            newSurgicalProcedures.setAnesthesia(anesthesiaSurgicalProcedure.getText().toString());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("surgical").document(newSurgicalProcedures.getId())
                    .set(newSurgicalProcedures);
            finish();
        }
        else {
            DocumentReference updateVaccine = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("surgical").document(idSurgical.toString());
            updateVaccine.update("name", nameSurgicalProcedure.getText().toString().trim());
            updateVaccine.update("description", descriptionSurgicalProcedure.getText().toString().trim());
            updateVaccine.update("type", spinnerTypeSurgicalProcedure.getSelectedItem().toString().trim());
            updateVaccine.update("date", dateSurgicalProcedure.getText().toString().trim());
            updateVaccine.update("anesthesia", anesthesiaSurgicalProcedure.getText().toString().trim());
            updateVaccine.update("veterinarian", veterinarianSurgicalProcedure.getText().toString().trim());
        }

        Intent intent = new Intent(SurgicalProceduresTakerActivity.this, SurgicalProceduresFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void backToTheSurgical(){
        if (mClick != null)
            mClick.start();
        finish();
    }

}
