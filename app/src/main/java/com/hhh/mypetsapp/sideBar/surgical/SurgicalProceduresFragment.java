package com.hhh.mypetsapp.sideBar.surgical;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
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
import com.hhh.mypetsapp.databinding.FragmentSurgicalProceduresBinding;

import java.util.ArrayList;
import java.util.List;

public class SurgicalProceduresFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private FragmentSurgicalProceduresBinding binding;
    private SharedPreferences defPref;
    MediaPlayer mDelete;
    RecyclerView recyclerSurgicalProcedures;
    FloatingActionButton addSurgicalProceduresButton;
    TextView notElemSurgicalProcedures;
    SurgicalProceduresListAdapter surgicalProceduresListAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<SurgicalProcedures> surgicalProcedures = new ArrayList<>();
    private ItemViewModel itemViewModel;
    private String name;
    SurgicalProcedures selectedSurgicalProcedure;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SurgicalProceduresViewModel proceduresViewModel =
                new ViewModelProvider(this).get(SurgicalProceduresViewModel.class);

        binding = FragmentSurgicalProceduresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerSurgicalProcedures = binding.recyclerSurgicalProcedures;
        addSurgicalProceduresButton = binding.addSurgicalProceduresButton;
        notElemSurgicalProcedures = binding.notElemSurgicalProcedures;

        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = itemViewModel.namePet;

        addSurgicalProceduresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSurgicalProcedure();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        surgicalProcedures.clear();
        infoFromDataBase();

        defPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            this.getView().setBackgroundResource(R.drawable.side_nav_bar_dark);
        }
        else {
            //light
            this.getView().setBackgroundResource(R.drawable.side_nav_bar);
        }

        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mDelete = MediaPlayer.create(this.getContext(), R.raw.delete);
        }
        else {
            mDelete = null;
        }
    }

    private void infoFromDataBase() {
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("surgical")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                SurgicalProcedures newSurgical = document.toObject(SurgicalProcedures.class);
                                surgicalProcedures.add(newSurgical);
                                updateRecycler();
                            }
                            if (surgicalProcedures.isEmpty())
                                notElemSurgicalProcedures.setText(R.string.notElem);
                            else
                                notElemSurgicalProcedures.setText("");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addSurgicalProcedure() {
        Intent intent = new Intent(SurgicalProceduresFragment.this.getActivity(), SurgicalProceduresTakerActivity.class);
        intent.putExtra("petName", name.toString());
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
    }

    private void updateRecycler(){
        recyclerSurgicalProcedures.setHasFixedSize(true);
        recyclerSurgicalProcedures.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        surgicalProceduresListAdapter = new SurgicalProceduresListAdapter(SurgicalProceduresFragment.this, surgicalProcedures, surgicalProceduresClickListener);
        recyclerSurgicalProcedures.setAdapter(surgicalProceduresListAdapter);
    }

    private final SurgicalProceduresClickListener surgicalProceduresClickListener = new SurgicalProceduresClickListener() {
        @Override
        public void onClick(SurgicalProcedures currentSurgicalProcedure) {
            Intent intent = new Intent(SurgicalProceduresFragment.this.getActivity(), SurgicalProceduresTakerActivity.class);
            intent.putExtra("oldSurgical", currentSurgicalProcedure.id);
            intent.putExtra("petName", name);
            intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
            startActivity(intent);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onLongClick(SurgicalProcedures currentSurgicalProcedure, CardView cardView) {
            selectedSurgicalProcedure = new SurgicalProcedures();
            selectedSurgicalProcedure = currentSurgicalProcedure;
            showPopUp(cardView);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPopUp(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.delete_menu);
        popupMenu.setForceShowIcon(true);
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
                alertDialog.setTitle(R.string.deleteQuestion);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mDelete != null)
                            mDelete.start();
                        db.collection("users").document(uID)
                                .collection("pets").document(name)
                                .collection("surgical").document(selectedSurgicalProcedure.getId()).delete();
                        Toast.makeText(SurgicalProceduresFragment.this.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        surgicalProcedures.clear();
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