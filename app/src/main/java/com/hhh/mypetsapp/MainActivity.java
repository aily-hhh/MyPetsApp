package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayAdapter<String> adapter;
    private SharedPreferences defPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_main);

        Button signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutMethod();
            }
        });

        Button vetPassportButton = (Button) findViewById(R.id.vetPassportButton);
        vetPassportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToVetPass(adapter);
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSettings();
            }
        });

        Button aboutMeButton = (Button) findViewById(R.id.aboutMeButton);
        aboutMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserProfile();
            }
        });
    }

    @Override
    protected void onResume() {
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

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<String> pets = new ArrayList<>();
        db.collection("users").document(uID).collection("pets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("name") != null) {
                                pets.add(doc.getString("name"));
                            }
                        }
                        Log.d(TAG, "Current pets: " + pets);
                    }
                });

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, pets);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clear();
    }

    private void signOutMethod(){
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, R.string.userSignedOut, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void goToVetPass(ArrayAdapter adapter){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setTitle(R.string.chooseYourPet);
        alertDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, VetPassportActivity.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                intent.putExtra("petName", adapter.getItem(i).toString());
                startActivity(intent);
            }
        });
        alertDialog.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNeutralButton(R.string.addNewPet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, NewPetActivity.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                startActivity(intent);
                adapter.clear();
            }
        });
        alertDialog.show();
    }

    private void goToSettings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
    }

    private void goToUserProfile(){
        Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
    }
}