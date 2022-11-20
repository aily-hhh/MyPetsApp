package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.hhh.mypetsapp.databinding.ActivityVetPassportBinding;
import com.hhh.mypetsapp.sideBar.identification.IdentificationFragment;

import java.time.LocalDate;

public class VetPassportActivity extends BaseActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityVetPassportBinding binding;
    private ItemViewModel viewModel;
    private String name;
    private boolean rotate = true;
    private SharedPreferences defPref;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayAdapter<String> adapter;

    TextView namePetProfile;
    TextView agePetProfile;
    ImageView iconPetProfile, addedPets;
    LinearLayout layoutHeader;

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

        Intent intent = getIntent();
        name = intent.getStringExtra("petName");

        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.namePet = name;

        binding = ActivityVetPassportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarVetPassport.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_gallery, R.id.nav_vaccines, R.id.nav_procedures, R.id.nav_treatment,
                R.id.nav_dehelmintization, R.id.nav_reproduction, R.id.nav_identification, R.id.nav_settings,
                R.id.nav_aboutMe, R.id.nav_signOut)
                .setOpenableLayout(drawer)
                .build();           
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_vet_passport);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        namePetProfile = (TextView) header.findViewById(R.id.namePetProfile);
        agePetProfile = (TextView) header.findViewById(R.id.agePetProfile);
        iconPetProfile = (ImageView) header.findViewById(R.id.iconPetProfile);
        addedPets = (ImageView) header.findViewById(R.id.addedPets);
        layoutHeader = (LinearLayout) header.findViewById(R.id.layoutHeader);

        if (key){
            //dark
            layoutHeader.setBackgroundResource(R.drawable.side_nav_bar_dark);
            namePetProfile.setTextColor(Color.WHITE);
            agePetProfile.setTextColor(Color.WHITE);
            //addedPets.setColorFilter(R.color.forButtons);
        }
        else {
            //light
            layoutHeader.setBackgroundResource(R.drawable.side_nav_bar);
        }

        addedPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListOfPets();
            }
        });

        layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListOfPets();
            }
        });

        infoFromDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        infoFromDB();
    }

    private void showListOfPets() {
        if (rotate) {
            addedPets.animate().rotationBy(180).start();
            rotate = false;
        }
        else {
            addedPets.animate().rotationBy(-180).start();
            rotate = true;
        }
    }

    private void signOut(){
        AuthUI.getInstance()
                .signOut(VetPassportActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(VetPassportActivity.this, R.string.userSignedOut, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VetPassportActivity.this, AuthActivity.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                        startActivity(intent);
                    }
                });
    }

    private void infoFromDB() {
        db.collection("users").document(uID)
                .collection("pets").document(name)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            namePetProfile.setText(snapshot.get("name").toString());
                            if (snapshot.get("birthday").equals(null) ||
                                    snapshot.get("birthday").equals("")) {
                                agePetProfile.setText("");
                            }
                            else {
                                String birthdayStr = snapshot.get("birthday").toString();
                                String[] date = birthdayStr.split("\\.");
                                int birthdayDay = Integer.parseInt(date[0]);
                                int birthdayMonth = Integer.parseInt(date[1]);
                                int birthdayYear = Integer.parseInt(date[2]);
                                LocalDate dateNow = LocalDate.now();
                                int currentDay = dateNow.getDayOfMonth();
                                int currentMonth = dateNow.getMonthValue()+1;
                                int currentYear = dateNow.getYear();
                                int difDay = currentDay - birthdayDay;
                                int difMonth = currentMonth - birthdayMonth;
                                int difYear = currentYear - birthdayYear;
                                if (difMonth == 0 && difDay < 0) {
                                    difYear = difYear - 1;
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else if (difMonth == 0) {
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else if (difMonth > 0) {
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else {
                                    difYear = difYear - 1;
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                            }

                            if (snapshot.get("photoUri") != null){
                                Task<Uri> storageReference = FirebaseStorage.getInstance().getReference()
                                        .child("images/"+snapshot.get("photoUri").toString()).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(getApplicationContext())
                                                        .load(uri)
                                                        .into(iconPetProfile);
                                            }
                                        });
                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_vet_passport);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void goToPetProfile(View view){
        Intent intent = new Intent(VetPassportActivity.this, PetProfileActivity.class);
        intent.putExtra("petName", name);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
    }

    public void onDateReceive(int dd ,int mm, int yy){
        IdentificationFragment.onClickMicro(dd,mm,yy);
    }

    public void onDateReceive2(int dd ,int mm, int yy){
        IdentificationFragment.onClickTattoo(dd,mm,yy);
    }
}