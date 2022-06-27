package com.hhh.mypetsapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AboutMeActivity extends AppCompatActivity {

    TextInputEditText userName, userEmail, userPhone;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        userName = (TextInputEditText) findViewById(R.id.userName);
        userEmail = (TextInputEditText) findViewById(R.id.userEmail);
        userPhone = (TextInputEditText) findViewById(R.id.userPhone);

        infoFromDatabase();
    }

    private void infoFromDatabase() {
        final DocumentReference docRef = db.collection("users").document(uID);
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
                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null)
                        userEmail.setEnabled(false);
                        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null)
                        userPhone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
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
    }
}