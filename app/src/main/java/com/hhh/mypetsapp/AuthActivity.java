package com.hhh.mypetsapp;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends BaseActivity {

    // variable for Firebase Auth
    private FirebaseAuth mFirebaseAuth;

    // declaring a const int value which we
    // will be using in Firebase auth.
    public static final int RC_SIGN_IN = 1;

    // creating an auth listener for our Firebase auth
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // below is the list which we have created in which
    // we can add the authentication which we have to
    // display inside our app.
    List<AuthUI.IdpConfig> providers = Arrays.asList(

            //new AuthUI.IdpConfig.AnonymousBuilder().build(),

            // below is the line for adding
            // email and password authentication.
            //new AuthUI.IdpConfig.EmailBuilder().build(),

            // below line is used for adding google
            // authentication builder in our app.
            new AuthUI.IdpConfig.GoogleBuilder().build());

            // below line is used for adding phone
            // authentication builder in our app.
            //new AuthUI.IdpConfig.PhoneBuilder().build());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // below line is for getting instance
        // for our firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // below line is used for calling auth listener
        // for oue Firebase authentication.
        mAuthStateListener = firebaseAuth -> {

            // we are calling method for on authentication state changed.
            // below line is used for getting current user which is
            // authenticated previously.
            FirebaseUser user = firebaseAuth.getCurrentUser();

            // checking if the user
            // is null or not.
            if (user != null) {
                // if the user is already authenticated then we will
                // redirect our user to next screen which is our home screen.
                // we are redirecting to new screen via an intent.
                Intent i = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(i);
                // we are calling finish method to kill or
                // activity which is displaying our login ui.
                finish();
            } else {
                // this method is called when our
                // user is not authenticated previously.
                startActivityForResult(
                        // below line is used for getting
                        // our authentication instance.
                        AuthUI.getInstance()
                                // below line is used to
                                // create our sign in intent
                                .createSignInIntentBuilder()

                                // below line is used for adding smart
                                // lock for our authentication.
                                // smart lock is used to check if the user
                                // is authentication through different devices.
                                // currently we are disabling it.
                                .setIsSmartLockEnabled(false)

                                // we are adding different login providers which
                                // we have mentioned above in our list.
                                // we can add more providers according to our
                                // requirement which are available in firebase.
                                .setAvailableProviders(providers)

                                // below line is for customizing our theme for
                                // login screen and set logo method is used for
                                // adding logo for our login page.
                                .setLogo(R.drawable.icon).setTheme(R.style.Theme_MyPetsApp)

                                // after setting our theme and logo
                                // we are calling a build() method
                                // to build our login screen.
                                .build(),
                        // and lastly we are passing our const
                        // integer which is declared above.
                        RC_SIGN_IN
                );
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we are calling our auth
        // listener method on app resume.
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // here we are calling remove auth
        // listener method on stop.
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}