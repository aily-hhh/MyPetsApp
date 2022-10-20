package com.hhh.mypetsapp.sideBar.vaccines;

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

public class VaccinesTakerActivity extends BaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String name;
    boolean isOld = false;
    String idVaccine;
    ArrayAdapter<CharSequence> adapterType;

    private SharedPreferences defPref;
    MediaPlayer mClick;
    MediaPlayer mAdd;

    int DIALOG_DATE = 1;
    int DIALOG_VALID = 2;
    int myYear = 2020;
    int myMonth = 1;
    int myDay = 1;

    Spinner spinnerTypeVaccine;
    EditText nameVaccine, manufacturerVaccine, dateVaccine, validUntilVaccine, veterinarianVaccine;
    ImageView backVaccines, saveVaccine;
    LinearLayout vaccinesTakerLayout;

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
        setContentView(R.layout.activity_vaccines_taker);

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        spinnerTypeVaccine = (Spinner) findViewById(R.id.spinnerTypeVaccine);
        nameVaccine = (EditText) findViewById(R.id.nameVaccine);
        manufacturerVaccine = (EditText) findViewById(R.id.manufacturerVaccine);
        dateVaccine = (EditText) findViewById(R.id.dateVaccine);
        validUntilVaccine = (EditText) findViewById(R.id.validUntilVaccine);
        veterinarianVaccine = (EditText) findViewById(R.id.veterinarianVaccine);
        backVaccines = (ImageView) findViewById(R.id.backVaccines);
        saveVaccine = (ImageView) findViewById(R.id.saveVaccine);
        vaccinesTakerLayout = (LinearLayout) findViewById(R.id.vaccinesTakerLayout);

        if (key){
            //dark
            saveVaccine.setColorFilter(R.color.forButtons);
            backVaccines.setColorFilter(R.color.forButtons);
            vaccinesTakerLayout.setBackgroundResource(R.color.takerDark);
        }
        else {
            //light
            setTheme(R.style.Theme_MyPetsApp);
        }

        idVaccine = getIntent().getStringExtra("oldVaccine");
        infoFromOld();

        adapterType = ArrayAdapter.createFromResource(this,
                R.array.typeVaccineArray, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeVaccine.setAdapter(adapterType);
        int pos = adapterType.getPosition(spinnerTypeVaccine.getSelectedItem().toString());
        spinnerTypeVaccine.setSelection(pos);

        backVaccines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToTheVaccines();
            }
        });

        saveVaccine.setOnClickListener(new View.OnClickListener() {
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
        if (idVaccine != null) {
            DocumentReference docRef = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("vaccines").document(idVaccine);
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
                        if(snapshot.get("type") != null){
                            int pos = adapterType.getPosition(snapshot.get("type").toString());
                            spinnerTypeVaccine.setSelection(pos);
                        }
                        if(snapshot.get("name") != null){
                            nameVaccine.setText(snapshot.get("name").toString());
                        }
                        if(snapshot.get("manufacturer") != null){
                            manufacturerVaccine.setText(snapshot.get("manufacturer").toString());
                        }
                        if(snapshot.get("dateOfVaccination") != null){
                            dateVaccine.setText(snapshot.get("dateOfVaccination").toString());
                        }
                        if(snapshot.get("validUntil") != null){
                            validUntilVaccine.setText(snapshot.get("validUntil").toString());
                        }
                        if(snapshot.get("veterinarian") != null){
                            veterinarianVaccine.setText(snapshot.get("veterinarian").toString());
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
        if (id == DIALOG_VALID) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack2, myYear, myMonth, myDay);
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
            dateVaccine.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    public void onClickValidUntil(View view){
        showDialog(DIALOG_VALID);
    }

    DatePickerDialog.OnDateSetListener myCallBack2 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            validUntilVaccine.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void addingToDataBase() {
        if (!isOld) {
            Vaccine newVaccine = new Vaccine();

            newVaccine.setId(UUID.randomUUID().toString().trim());
            newVaccine.setName(nameVaccine.getText().toString().trim());
            newVaccine.setManufacturer(manufacturerVaccine.getText().toString().trim());
            newVaccine.setVeterinarian(veterinarianVaccine.getText().toString().trim());
            newVaccine.setType(spinnerTypeVaccine.getSelectedItem().toString().trim());
            newVaccine.setDateOfVaccination(dateVaccine.getText().toString().trim());
            newVaccine.setValidUntil(validUntilVaccine.getText().toString());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("vaccines").document(newVaccine.getId()).set(newVaccine);
            finish();
        }
        else {
            DocumentReference updateVaccine = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("vaccines").document(idVaccine.toString());
            updateVaccine.update("name", nameVaccine.getText().toString().trim());
            updateVaccine.update("manufacturer", manufacturerVaccine.getText().toString().trim());
            updateVaccine.update("type", spinnerTypeVaccine.getSelectedItem().toString().trim());
            updateVaccine.update("dateOfVaccination", dateVaccine.getText().toString().trim());
            updateVaccine.update("validUntil", validUntilVaccine.getText().toString().trim());
            updateVaccine.update("veterinarian", veterinarianVaccine.getText().toString().trim());
        }

        Intent intent = new Intent(VaccinesTakerActivity.this, VaccinesFragment.class);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void backToTheVaccines() {
        finish();
    }
}
