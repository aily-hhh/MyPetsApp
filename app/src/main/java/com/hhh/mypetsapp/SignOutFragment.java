package com.hhh.mypetsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignOutFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthUI.getInstance()
                .signOut(this.getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignOutFragment.this.getContext(), R.string.userSignedOut, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignOutFragment.this.getContext(), AuthActivity.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                        startActivity(intent);
                    }
                });
        getActivity().finish();
    }
}
