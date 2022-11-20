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
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.hhh.mypetsapp.databinding.ActivityNewPetBinding;
import com.hhh.mypetsapp.sideBar.gallery.Gallery;
import com.hhh.mypetsapp.sideBar.gallery.GalleryFragment;

import java.io.IOException;
import java.util.UUID;

public class NewPetActivity extends BaseActivity {

    private ActivityNewPetBinding binding;

    TextInputEditText petNewName, petNewSpecies, petNewBreed, petNewHair;
    EditText petNewBirthday;
    Spinner spinnerNewSex;

    ImageView petNewPhoto;
    private Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    private final int GALLERY_REQUEST = 1;
    private final int PERMISSION_REQUEST = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private SharedPreferences defPref;

    ArrayAdapter<CharSequence> adapterNewSex;
    MediaPlayer mNewPet;

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
        binding = ActivityNewPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petNewName = (TextInputEditText) findViewById(R.id.petNewName);
        petNewSpecies = (TextInputEditText) findViewById(R.id.petNewSpecies);
        petNewBreed = (TextInputEditText) findViewById(R.id.petNewBreed);
        petNewHair = (TextInputEditText) findViewById(R.id.petNewHair);
        petNewBirthday = (EditText) findViewById(R.id.petNewBirthday);
        spinnerNewSex = (Spinner) findViewById(R.id.spinnerNewSex);
        petNewPhoto = (ImageView) findViewById(R.id.petNewPhoto);

        FloatingActionButton fab = binding.changeImageNewPetFab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePetNewPhoto();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        adapterNewSex = ArrayAdapter.createFromResource(this,
                R.array.sexArray, android.R.layout.simple_spinner_item);
        adapterNewSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewSex.setAdapter(adapterNewSex);

        int pos = adapterNewSex.getPosition(spinnerNewSex.getSelectedItem().toString());
        spinnerNewSex.setSelection(pos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mNewPet = MediaPlayer.create(this, R.raw.new_pet);
        }
        else {
            mNewPet = null;
        }
    }

    public void createInformationForPet(View view){
        Pet pet = new Pet();
        pet.setName(petNewName.getText().toString().trim());
        pet.setSpecies(petNewSpecies.getText().toString().trim());
        pet.setBirthday(petNewBirthday.getText().toString().trim());
        pet.setBreed(petNewBreed.getText().toString().trim());
        pet.setHair(petNewHair.getText().toString().trim());
        pet.setSex(spinnerNewSex.getSelectedItem().toString().trim());

        if (pet.getName().equals("") || pet.getName().isEmpty()){
            Toast.makeText(NewPetActivity.this, R.string.inputName, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.imageWarning, Toast.LENGTH_LONG).show();

            db.collection("users").document(uID)
                    .collection("pets").document(pet.getName()).set(pet);

            uploadImage();

            Intent intent = new Intent(NewPetActivity.this, VetPassportActivity.class);
            intent.putExtra("petName", petNewName.getText().toString().trim());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            if (mNewPet != null)
                mNewPet.start();
            finish();
        }
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
            petNewBirthday.setText(myDay + "." + myMonth + "." + myYear);
        }
    };

    private void updatePetNewPhoto(){
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
                petNewPhoto.setImageBitmap(bitmap);
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
                    = storageReference.child("images/" + photoStr);
            DocumentReference updatePhoto = db.collection("users").document(uID)
                    .collection("pets").document(petNewName.getText().toString().trim());
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
                            Toast.makeText(NewPetActivity.this, R.string.imageUploaded,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(NewPetActivity.this, "Failed " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}