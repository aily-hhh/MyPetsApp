package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.Date;

public class NewPetActivity extends AppCompatActivity {

    TextInputEditText petNewName, petNewSpecies, petNewBreed, petNewHair;
    EditText petNewBirthday;
    Spinner spinnerNewSex;

    ImageView petNewPhoto;
    private final int GALLERY_REQUEST = 1;
    private final int PERMISSION_REQUEST = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayAdapter<CharSequence> adapterNewSex;

    int DIALOG_DATE = 1;
    int myYear = 2020;
    int myMonth = 1;
    int myDay = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);

        petNewName = (TextInputEditText) findViewById(R.id.petNewName);
        petNewSpecies = (TextInputEditText) findViewById(R.id.petNewSpecies);
        petNewBreed = (TextInputEditText) findViewById(R.id.petNewBreed);
        petNewHair = (TextInputEditText) findViewById(R.id.petNewHair);
        petNewBirthday = (EditText) findViewById(R.id.petNewBirthday);
        spinnerNewSex = (Spinner) findViewById(R.id.spinnerNewSex);
        petNewPhoto = (ImageView) findViewById(R.id.petNewPhoto);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        petNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePetNewPhoto();
            }
        });

        adapterNewSex = ArrayAdapter.createFromResource(this,
                R.array.sexArray, android.R.layout.simple_spinner_item);
        adapterNewSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewSex.setAdapter(adapterNewSex);

        int pos = adapterNewSex.getPosition(spinnerNewSex.getSelectedItem().toString());
        spinnerNewSex.setSelection(pos);
    }

    public void createInformationForPet(View view){
        Pet pet = new Pet();
        pet.setName(petNewName.getText().toString().trim());
        pet.setSpecies(petNewSpecies.getText().toString().trim());
        pet.setBirthday(petNewBirthday.getText().toString().trim());
        pet.setBreed(petNewBreed.getText().toString().trim());
        pet.setHair(petNewHair.getText().toString().trim());
        pet.setSex(spinnerNewSex.getSelectedItem().toString().trim());

        db.collection("users").document(uID)
                .collection("pets").document(pet.getName()).set(pet);

        Intent intent = new Intent(NewPetActivity.this, PetProfileActivity.class);
        intent.putExtra("petName", petNewName.getText().toString());
        startActivity(intent);
        finish();
    }

    public void onClickBirthday(View view){
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
            petNewBirthday.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void updatePetNewPhoto(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    petNewPhoto.setImageBitmap(bitmap);
                }
        }
    }
}