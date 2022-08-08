package com.hhh.mypetsapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HeaderFragment extends Fragment {

    static TextView namePetProfile, agePetProfile;
    private ItemViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.nav_header_vet_passport, null);
        namePetProfile = view.findViewById(R.id.namePetProfile);
        agePetProfile = view.findViewById(R.id.agePetProfile);

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        namePetProfile.setText(viewModel.namePet);
        System.out.println("ppp " + namePetProfile);


        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
