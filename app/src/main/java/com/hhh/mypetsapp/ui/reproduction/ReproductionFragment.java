package com.hhh.mypetsapp.ui.reproduction;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.hhh.mypetsapp.databinding.FragmentReproductionBinding;

import java.util.ArrayList;
import java.util.List;

public class ReproductionFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private FragmentReproductionBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ReproductionListAdapter reproductionListAdapter;
    List<Reproduction> reproductions = new ArrayList<>();
    private ItemViewModel viewModel;
    private String name;
    Reproduction selectedReproduction;

    RecyclerView recyclerReproduction;
    FloatingActionButton addReproductionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ReproductionViewModel reproductionViewModel =
                new ViewModelProvider(this).get(ReproductionViewModel.class);

        binding = FragmentReproductionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        recyclerReproduction = binding.recyclerReproduction;
        addReproductionButton = binding.addReproductionButton;

        addReproductionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingReproduction();
            }
        });

        return  root;
    }

    @Override
    public void onStart() {
        super.onStart();

        reproductions.clear();
        infoFromDataBase();
    }

    private void infoFromDataBase() {
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("reproduction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Reproduction newReproduction = document.toObject(Reproduction.class);
                                reproductions.add(newReproduction);
                                updateRecycler();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void updateRecycler(){
        recyclerReproduction.setHasFixedSize(true);
        recyclerReproduction.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        reproductionListAdapter = new ReproductionListAdapter(ReproductionFragment.this,
                reproductions, reproductionClickListener);
        recyclerReproduction.setAdapter(reproductionListAdapter);
    }

    private  final ReproductionClickListener reproductionClickListener = new ReproductionClickListener() {
        @Override
        public void onClick(Reproduction currentReproduction) {
            Intent intent = new Intent(ReproductionFragment.this.getActivity(), ReproductionTakerActivity.class);
            intent.putExtra("oldReproduction", currentReproduction.getId());
            intent.putExtra("petName", name);
            startActivity(intent);
        }

        @Override
        public void onLongClick(Reproduction currentReproduction, CardView cardView) {
            selectedReproduction = new Reproduction();
            selectedReproduction = currentReproduction;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.delete_menu);
        popupMenu.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.deleteMenu:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.getContext());
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setTitle(R.string.delete);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("users").document(uID)
                                .collection("pets").document(name)
                                .collection("reproduction").document(selectedReproduction.getId()).delete();
                        Toast.makeText(ReproductionFragment.this.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        reproductions.clear();
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

    private void addingReproduction() {
        Intent intent = new Intent(ReproductionFragment.this.getActivity(), ReproductionTakerActivity.class);
        intent.putExtra("petName", name);
        startActivity(intent);
    }
}
