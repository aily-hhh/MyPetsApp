package com.hhh.mypetsapp.ui.procedures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hhh.mypetsapp.databinding.ProceduresFragmentBinding;

public class ProceduresFragment extends Fragment {

    private ProceduresFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProceduresViewModel proceduresViewModel =
                new ViewModelProvider(this).get(ProceduresViewModel.class);

        binding = ProceduresFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProcedures;
        proceduresViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}