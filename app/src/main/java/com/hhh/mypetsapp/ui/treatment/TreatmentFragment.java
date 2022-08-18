package com.hhh.mypetsapp.ui.treatment;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhh.mypetsapp.ItemViewModel;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.TreatmentFragmentBinding;
import com.hhh.mypetsapp.ui.vaccines.Vaccine;
import com.hhh.mypetsapp.ui.vaccines.VaccinesClickListener;
import com.hhh.mypetsapp.ui.vaccines.VaccinesFragment;
import com.hhh.mypetsapp.ui.vaccines.VaccinesListAdapter;
import com.hhh.mypetsapp.ui.vaccines.VaccinesTakerActivity;

import java.util.ArrayList;
import java.util.List;

public class TreatmentFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private TreatmentFragmentBinding binding;
    RecyclerView recyclerTreatments;
    FloatingActionButton addTreatmentButton;
    TreatmentsListAdapter treatmentsListAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<Treatment> treatments = new ArrayList<>();
    private ItemViewModel viewModel;
    private String name;
    Treatment selectedTreatment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TreatmentViewModel treatmentViewModel =
                new ViewModelProvider(this).get(TreatmentViewModel.class);

        binding = TreatmentFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerTreatments = binding.recyclerTreatments;
        addTreatmentButton = binding.addTreatmentButton;

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        addTreatmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTreatment();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        treatments.clear();
        infoFromDataBase();
    }

    private void addTreatment() {
        Intent intent = new Intent(TreatmentFragment.this.getActivity(), TreatmentsTakerActivity.class);
        intent.putExtra("petName", name.toString());
        startActivity(intent);
    }

    private void updateRecycler() {
        recyclerTreatments.setHasFixedSize(true);
        recyclerTreatments.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        treatmentsListAdapter = new TreatmentsListAdapter(TreatmentFragment.this, treatments, treatmentsClickListener);
        recyclerTreatments.setAdapter(treatmentsListAdapter);
    }

    private final TreatmentsClickListener treatmentsClickListener = new TreatmentsClickListener() {
        @Override
        public void onClick(Treatment currentTreatment) {
            Intent intent = new Intent(TreatmentFragment.this.getActivity(), TreatmentsTakerActivity.class);
            intent.putExtra("oldTreatment", currentTreatment.getId());
            intent.putExtra("petName", name.toString());
            startActivity(intent);
        }

        @Override
        public void onLongClick(Treatment currentTreatment, CardView cardView) {
            selectedTreatment = new Treatment();
            selectedTreatment = currentTreatment;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.treatment_menu);
        popupMenu.show();
    }

    private void infoFromDataBase(){
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("treatments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Treatment newTreatment = document.toObject(Treatment.class);
                                treatments.add(newTreatment);
                                updateRecycler();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.deleteMenuTreatment:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setTitle(R.string.delete);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("users").document(uID)
                                .collection("pets").document(name)
                                .collection("treatments").document(selectedTreatment.getId()).delete();
                        Toast.makeText(TreatmentFragment.this.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        treatments.clear();
                        infoFromDataBase();
                        updateRecycler();
                    }
                });
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
                return true;

            default: return false;
        }
    }
}