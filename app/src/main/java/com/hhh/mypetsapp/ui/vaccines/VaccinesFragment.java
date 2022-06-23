package com.hhh.mypetsapp.ui.vaccines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hhh.mypetsapp.databinding.FragmentVaccinesBinding;

public class VaccinesFragment extends Fragment {

    private FragmentVaccinesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VaccinesViewModel vaccinesViewModel =
                new ViewModelProvider(this).get(VaccinesViewModel.class);

        binding = FragmentVaccinesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textVaccines;
        vaccinesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}