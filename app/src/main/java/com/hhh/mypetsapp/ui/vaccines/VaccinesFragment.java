package com.hhh.mypetsapp.ui.vaccines;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.hhh.mypetsapp.databinding.FragmentVaccinesBinding;

import java.util.ArrayList;
import java.util.List;

public class VaccinesFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private FragmentVaccinesBinding binding;
    RecyclerView recyclerVaccines;
    FloatingActionButton addVaccineButton;
    VaccinesListAdapter vaccinesListAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<Vaccine> vaccines = new ArrayList<>();
    private ItemViewModel viewModel;
    private String name;
    Vaccine selectedVaccine;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        VaccinesViewModel vaccinesViewModel =
                new ViewModelProvider(this).get(VaccinesViewModel.class);

        binding = FragmentVaccinesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerVaccines = binding.recyclerVaccines;
        addVaccineButton = binding.addVaccineButton;

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        addVaccineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVaccine();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        vaccines.clear();
        infoFromDataBase();
    }

    private void addVaccine() {
        Intent intent = new Intent(VaccinesFragment.this.getActivity(), VaccinesTakerActivity.class);
        intent.putExtra("petName", name.toString());
        startActivity(intent);
    }

    private void updateRecycler() {
        recyclerVaccines.setHasFixedSize(true);
        recyclerVaccines.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        vaccinesListAdapter = new VaccinesListAdapter(VaccinesFragment.this, vaccines, vaccinesClickListener);
        recyclerVaccines.setAdapter(vaccinesListAdapter);
    }

    private final VaccinesClickListener vaccinesClickListener = new VaccinesClickListener() {
        @Override
        public void onClick(Vaccine currentVaccine) {
            Intent intent = new Intent(VaccinesFragment.this.getActivity(), VaccinesTakerActivity.class);
            intent.putExtra("oldVaccine", currentVaccine.getId());
            intent.putExtra("petName", name.toString());
            startActivity(intent);
        }

        @Override
        public void onLongClick(Vaccine currentVaccine, CardView cardView) {
            selectedVaccine = new Vaccine();
            selectedVaccine = currentVaccine;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.vaccines_menu);
        popupMenu.show();
    }

    private void infoFromDataBase(){
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("vaccines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Vaccine newVaccine = document.toObject(Vaccine.class);
                                vaccines.add(newVaccine);
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
            case R.id.deleteMenuVaccines:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setTitle(R.string.delete);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("users").document(uID)
                                .collection("pets").document(name)
                                .collection("vaccines").document(selectedVaccine.getId()).delete();
                        Toast.makeText(VaccinesFragment.this.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        vaccines.clear();
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