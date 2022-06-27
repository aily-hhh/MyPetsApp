package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PetProfileActivity extends AppCompatActivity {

    TextInputEditText petName, petSpecies, petBreed, petHair;
    EditText petBirthday;
    RadioButton petMale, petFemale;
    RadioGroup petSex;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String petSexStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        petName = (TextInputEditText) findViewById(R.id.petName);
        petSpecies = (TextInputEditText) findViewById(R.id.petSpecies);
        petBreed = (TextInputEditText) findViewById(R.id.petBreed);
        petHair = (TextInputEditText) findViewById(R.id.petHair);
        petBirthday = (EditText) findViewById(R.id.petBirthday);
        petMale = (RadioButton) findViewById(R.id.petMale);
        petFemale = (RadioButton) findViewById(R.id.petFemale);
        petSex =(RadioGroup) findViewById(R.id.petSex);

        Intent intent = getIntent();
        petName.setText(intent.getStringExtra("petName"));

        infoFromDatabase();
    }

    private void infoFromDatabase() {
        final DocumentReference docRef = db.collection("users").document(uID)
                .collection("pets").document(petName.getText().toString());
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
                    petName.setText(snapshot.get("name").toString());
                    petName.setEnabled(false);
                    if(!snapshot.get("species").equals(null))
                        petSpecies.setText(snapshot.get("species").toString());
                    if(!snapshot.get("breed").equals(null))
                        petBreed.setText(snapshot.get("breed").toString());
                    if(!snapshot.get("hair").equals(null))
                        petHair.setText(snapshot.get("hair").toString());

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void updateInformationForPet(View view){
        petSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.petMale:
                        petSexStr = "Male";
                        break;
                    case R.id.petFemale:
                        petSexStr = "Female";
                        break;
                    default:
                        break;
                }
            }});

        DocumentReference updatePet = db.collection("users").document(uID)
                .collection("pets").document(petName.getText().toString());
        updatePet.update("breed", petBreed.getText().toString());
        updatePet.update("hair", petHair.getText().toString());
        updatePet.update("species", petSpecies.getText().toString());
        updatePet.update("sex", petSexStr);
    }
}