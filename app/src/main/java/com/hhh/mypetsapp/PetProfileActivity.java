package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hhh.mypetsapp.ui.dehelmintization.DehelmintizationFragment;

import java.io.Console;
import java.io.IOException;
import java.util.UUID;

public class PetProfileActivity extends AppCompatActivity {

    TextInputEditText petName, petSpecies, petBreed, petHair;
    EditText petBirthday;
    Spinner spinnerSex;
    Button buttonBack, buttonUpdate, buttonDelete;

    ImageView petPhoto;
    private Uri filePath;
    private SharedPreferences defPref;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int GALLERY_REQUEST = 1;
    private final int PERMISSION_REQUEST = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayAdapter<CharSequence> adapterSex;

    int DIALOG_DATE = 1;
    int myYear = 2020;
    int myMonth = 1;
    int myDay = 1;

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
        setContentView(R.layout.activity_pet_profile);

        petName = (TextInputEditText) findViewById(R.id.petName);
        petSpecies = (TextInputEditText) findViewById(R.id.petSpecies);
        petBreed = (TextInputEditText) findViewById(R.id.petBreed);
        petHair = (TextInputEditText) findViewById(R.id.petHair);
        petBirthday = (EditText) findViewById(R.id.petBirthday);
        spinnerSex = (Spinner) findViewById(R.id.spinnerSex);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        petPhoto = (ImageView) findViewById(R.id.petPhoto);

        adapterSex = ArrayAdapter.createFromResource(this,
                R.array.sexArray, android.R.layout.simple_spinner_item);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapterSex);

        petPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePetPhoto();
            }
        });
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        int pos = adapterSex.getPosition(spinnerSex.getSelectedItem().toString());
        spinnerSex.setSelection(pos);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToVetPass();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePetProfile();
            }
        });

        Intent intent = getIntent();
        petName.setText(intent.getStringExtra("petName"));

        infoFromDatabase();
    }

    private void deletePetProfile() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setTitle(R.string.deletePetProfile);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference deletePet = db.collection("users").document(uID)
                        .collection("pets").document(petName.getText().toString());
                Toast.makeText(PetProfileActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PetProfileActivity.this, MainActivity.class);
                startActivity(intent);
                deletePet.delete();
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
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
                    if(snapshot.get("species") != null)
                        petSpecies.setText(snapshot.get("species").toString());
                    if(snapshot.get("breed") != null)
                        petBreed.setText(snapshot.get("breed").toString());
                    if(snapshot.get("hair") != null)
                        petHair.setText(snapshot.get("hair").toString());
                    if(snapshot.get("birthday") != null)
                        petBirthday.setText(snapshot.get("birthday").toString());
                    if(snapshot.get("sex") != null){
                        int pos = adapterSex.getPosition(snapshot.get("sex").toString());
                        spinnerSex.setSelection(pos);
                    }

                    if (snapshot.get("photoUri") != null){
                        Task<Uri> storageReference = FirebaseStorage.getInstance().getReference()
                                .child("images/"+snapshot.get("photoUri").toString()).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(petPhoto);
                                    }
                                });
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void updateInformationForPet(View view){
        DocumentReference updatePet = db.collection("users").document(uID)
                .collection("pets").document(petName.getText().toString());
        updatePet.update("breed", petBreed.getText().toString().trim());
        updatePet.update("hair", petHair.getText().toString().trim());
        updatePet.update("species", petSpecies.getText().toString().trim());
        updatePet.update("birthday", petBirthday.getText().toString().trim());
        updatePet.update("sex", spinnerSex.getSelectedItem().toString().trim());

        uploadImage();
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
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            petBirthday.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void backToVetPass(){
        Intent intent = new Intent(PetProfileActivity.this, VetPassportActivity.class);
        intent.putExtra("petName", petName.getText().toString());
        startActivity(intent);
        finish();
    }

    public void updatePetPhoto(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    filePath = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        petPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private void uploadImage()
    {
        if (filePath != null) {
            String photoStr = UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference.child("images/"+photoStr);
            DocumentReference updatePhoto = db.collection("users").document(uID)
                    .collection("pets").document(petName.getText().toString());
            updatePhoto.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        if (snapshot.get("photoUri") != null){
                            String photoDelete = snapshot.get("photoUri").toString();
                            StorageReference storageRef = storage.getReference();
                            StorageReference desertRef = storageRef.child("images/"+photoDelete);
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    Log.d(TAG, "onSuccess: deleted file");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Log.d(TAG, "onFailure: did not delete file");
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            updatePhoto.update("photoUri", photoStr);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(PetProfileActivity.this, "Image Uploaded!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(PetProfileActivity.this, "Failed " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PetProfileActivity.this, VetPassportActivity.class);
        intent.putExtra("petName", petName.getText().toString());
        startActivity(intent);
        finish();
    }
}