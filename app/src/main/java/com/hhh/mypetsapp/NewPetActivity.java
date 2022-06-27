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

import java.util.Date;

public class NewPetActivity extends AppCompatActivity {

    TextInputEditText petNewName, petNewSpecies, petNewBreed, petNewHair;
    EditText petNewBirthday;
    RadioGroup petNewSex;
    RadioButton petNewMale, petNewFemale;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);

        petNewName = (TextInputEditText) findViewById(R.id.petNewName);
        petNewSpecies = (TextInputEditText) findViewById(R.id.petNewSpecies);
        petNewBreed = (TextInputEditText) findViewById(R.id.petNewBreed);
        petNewHair = (TextInputEditText) findViewById(R.id.petNewHair);
        petNewBirthday = (EditText) findViewById(R.id.petNewBirthday);
        petNewSex = (RadioGroup) findViewById(R.id.petNewSex);
        petNewMale = (RadioButton) findViewById(R.id.petNewMale);
        petNewFemale = (RadioButton) findViewById(R.id.petNewFemale);
    }

    public void createInformationForPet(View view){
        Pet pet = new Pet();
        pet.setName(petNewName.getText().toString());
        pet.setSpecies(petNewSpecies.getText().toString());
        //pet.setBirthday((Date)petNewBirthday.getText());
        pet.setBreed(petNewBreed.getText().toString());
        pet.setHair(petNewHair.getText().toString());

        petNewSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.petNewMale:
                        pet.setSex("Male");
                        break;
                    case R.id.petNewFemale:
                        pet.setSex("Female");
                        break;
                    default:
                        break;
                }
            }});

        db.collection("users").document(uID)
                .collection("pets").document(pet.getName()).set(pet);

        Intent intent = new Intent(NewPetActivity.this, PetProfileActivity.class);
        intent.putExtra("petName", petNewName.getText().toString());
        startActivity(intent);
        finish();
    }
}