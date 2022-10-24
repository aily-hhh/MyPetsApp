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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.hhh.mypetsapp.databinding.ActivityAboutMeBinding;
import com.hhh.mypetsapp.databinding.ActivityNewPetBinding;

import java.io.IOException;
import java.util.UUID;

public class AboutMeActivity extends BaseActivity {

    private ActivityAboutMeBinding binding;

    TextInputEditText userName, userEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ImageView userPhoto;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private SharedPreferences defPref;
    private final int GALLERY_REQUEST = 1;
    private final int PERMISSION_REQUEST = 0;

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
        binding = ActivityAboutMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userName = (TextInputEditText) findViewById(R.id.userName);
        userEmail = (TextInputEditText) findViewById(R.id.userEmail);
        userPhoto = (ImageView) findViewById(R.id.userPhoto);

        Toolbar toolbar = binding.toolbarUser;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayoutUser;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.changeImageUserFab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserPhoto();
            }
        });

        infoFromDatabase();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }

    private void infoFromDatabase() {
        final DocumentReference docRef = db.collection("users").document(uID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        docRef.set(new User());
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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
                    if (snapshot.get("photoUri") != null){
                        Task<Uri> storageReference = FirebaseStorage.getInstance().getReference()
                                .child("images/"+snapshot.get("photoUri").toString()).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(userPhoto);
                                    }
                                });

                    }
                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
                        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                        userEmail.setEnabled(false);
                        db.collection("users").document(uID)
                                .update("email", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                    }
                    if (snapshot.get("userName") != null)
                        userName.setText(snapshot.get("userName").toString());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void updateInformationForUser(View view){
        DocumentReference updateName = db.collection("users").document(uID);
        updateName.update("userName", userName.getText().toString());
        uploadImage();
    }

    public void updateUserPhoto(){
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
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            userPhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage()
    {
        if (filePath != null) {
            String photoStr = UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference.child("images/" + photoStr);
            DocumentReference updatePhoto = db.collection("users").document(uID);
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
                                    Toast.makeText(AboutMeActivity.this, "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(AboutMeActivity.this, "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}