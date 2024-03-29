package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.media.MediaPlayer;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hhh.mypetsapp.databinding.ActivityPetProfileBinding;

import java.io.IOException;
import java.util.UUID;

public class PetProfileActivity extends BaseActivity {

    private ActivityPetProfileBinding binding;

    TextInputEditText petSpecies, petBreed, petHair;
    EditText petBirthday;
    Spinner spinnerSex;
    Button buttonBack, buttonUpdate, buttonDelete;

    private String petName;

    ImageView petPhoto;
    private Uri filePath;
    private SharedPreferences defPref;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PERMISSION_REQUEST = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ArrayAdapter<CharSequence> adapterSex;
    MediaPlayer mDelete;

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
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        FloatingActionButton fab = binding.changeImagePetFab;
        fab.setOnClickListener(new View.OnClickListener() {
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
        petName = intent.getStringExtra("petName");
        Toolbar toolbar = binding.toolbarPet;
        toolbar.setTitle(petName);
        setSupportActionBar(toolbar);

        infoFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mDelete = MediaPlayer.create(this, R.raw.delete);
        }
        else {
            mDelete = null;
        }
    }

    private void deletePetProfile() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setTitle(R.string.deletePetProfile);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference deletePet = db.collection("users").document(uID)
                        .collection("pets").document(petName);
                Toast.makeText(PetProfileActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PetProfileActivity.this, MainActivity.class);
                startActivity(intent);
                deletePet.delete();
                if (mDelete != null)
                    mDelete.start();
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
                .collection("pets").document(petName);
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
                .collection("pets").document(petName);
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
        intent.putExtra("petName", petName);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
        finish();
    }

    public void updatePetPhoto(){
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    setImage(uri);
                }
            });

    private void setImage(Uri uri)
    {
        filePath = uri;
        if (filePath != null) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                petPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
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
                    .collection("pets").document(petName);
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
                            Toast.makeText(PetProfileActivity.this, R.string.imageUploaded,
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
        intent.putExtra("petName", petName);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
        finish();
    }
}