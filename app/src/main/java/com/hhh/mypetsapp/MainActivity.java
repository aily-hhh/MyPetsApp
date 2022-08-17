package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        alertDialog.setTitle("Choose your pet");
        alertDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, VetPassportActivity.class);
                intent.putExtra("petName", adapter.getItem(i).toString());
                startActivity(intent);
            }
        });
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNeutralButton("Add a new pet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, NewPetActivity.class);
                startActivity(intent);
                adapter.clear();
            }
        });
        alertDialog.show();
    }

    private void goToSettings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void goToUserProfile(){
        Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
        startActivity(intent);
    }
}