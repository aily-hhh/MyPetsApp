package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hhh.mypetsapp.databinding.ActivityVetPassportBinding;
import com.hhh.mypetsapp.databinding.AppBarVetPassportBinding;
import com.hhh.mypetsapp.ui.notes.Notes;

import java.time.LocalDate;
import java.time.Period;

public class VetPassportActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityVetPassportBinding binding;
    private ItemViewModel viewModel;
    private String name;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    TextView namePetProfile;
    TextView agePetProfile;
    ImageView iconPetProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                R.id.nav_notes, R.id.nav_gallery, R.id.nav_vaccines, R.id.nav_procedures, R.id.nav_treatment)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_vet_passport);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        namePetProfile = (TextView) header.findViewById(R.id.namePetProfile);
        agePetProfile = (TextView) header.findViewById(R.id.agePetProfile);
        iconPetProfile = (ImageView) header.findViewById(R.id.iconPetProfile);

        infoFromDB();
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
                                int currentMonth = dateNow.getMonthValue();
                                int currentYear = dateNow.getYear();
                                int difDay = currentDay - birthdayDay;
                                int difMonth = currentMonth - birthdayMonth;
                                int difYear = currentYear - birthdayYear;
                                if (difMonth == 0 && difDay < 0) {
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else if (difMonth == 0) {
                                    difYear++;
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else if (difMonth > 0) {
                                    difYear++;
                                    String age = String.valueOf(difYear) + " y.o";
                                    agePetProfile.setText(age);
                                }
                                else {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vet_passport, menu);
        return true;
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
        startActivity(intent);
        finish();
    }
}