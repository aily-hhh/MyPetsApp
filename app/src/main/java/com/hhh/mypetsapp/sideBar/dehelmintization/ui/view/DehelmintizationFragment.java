package com.hhh.mypetsapp.sideBar.dehelmintization.ui.view;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import androidx.annotation.Nullable;
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
import com.hhh.mypetsapp.databinding.FragmentDehelmintizationBinding;
import com.hhh.mypetsapp.sideBar.dehelmintization.ui.adapter.DehelmintizationClickListener;
import com.hhh.mypetsapp.sideBar.dehelmintization.ui.adapter.DehelmintizationListAdapter;
import com.hhh.mypetsapp.sideBar.dehelmintization.ui.viewModel.DehelmintizationViewModel;
import com.hhh.mypetsapp.sideBar.dehelmintization.model.Dehelmintization;

import java.util.ArrayList;
import java.util.List;

public class DehelmintizationFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private FragmentDehelmintizationBinding binding;
    private SharedPreferences defPref;
    RecyclerView recyclerDehelmintization;
    FloatingActionButton addDehelmintization;
    TextView notElemDehelmintization;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DehelmintizationListAdapter dehelmintizationListAdapter;
    List<Dehelmintization> dehelmintizations = new ArrayList<>();
    private ItemViewModel viewModel;
    private String name;
    Dehelmintization selectedDehelmintization;
    MediaPlayer mClick;
    MediaPlayer mDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DehelmintizationViewModel ViewModel =
                new ViewModelProvider(this).get(DehelmintizationViewModel.class);

        binding = FragmentDehelmintizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerDehelmintization = binding.recyclerDehelmintization;
        addDehelmintization = binding.addDehelmintization;
        notElemDehelmintization = binding.notElemDehelmintization;

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        addDehelmintization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingDehelmintization();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        dehelmintizations.clear();
        infoFromDataBase();

        defPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean key = defPref.getBoolean("theme", false);
        if (key){
            //dark
            this.getView().setBackgroundResource(R.drawable.side_nav_bar_dark);
        }
        else {
            //light
            this.getView().setBackgroundResource(R.drawable.background_notes);
        }

        boolean keySound = defPref.getBoolean("sound", false);;
        if (!keySound){
            //enable
            mClick = MediaPlayer.create(this.getContext(), R.raw.click);
            mDelete = MediaPlayer.create(this.getContext(), R.raw.delete);
        }
        else {
            mClick = null;
            mDelete = null;
        }
    }

    private void infoFromDataBase() {
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("dehelmintization")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Dehelmintization newDehelmintization = document.toObject(Dehelmintization.class);
                                dehelmintizations.add(newDehelmintization);
                                updateRecycler();
                            }
                            if (dehelmintizations.isEmpty())
                                notElemDehelmintization.setText(R.string.notElem);
                            else
                                notElemDehelmintization.setText("");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addingDehelmintization() {
        Intent intent = new Intent(DehelmintizationFragment.this.getActivity(), DehelmintizationTakerActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        intent.putExtra("petName", name);
        startActivity(intent);
        if (mClick != null)
            mClick.start();
    }

    private void updateRecycler(){
        recyclerDehelmintization.setHasFixedSize(true);
        recyclerDehelmintization.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        dehelmintizationListAdapter = new DehelmintizationListAdapter(DehelmintizationFragment.this,
                dehelmintizations, dehelmintizationClickListener);
        recyclerDehelmintization.setAdapter(dehelmintizationListAdapter);
    }

    private final DehelmintizationClickListener dehelmintizationClickListener = new DehelmintizationClickListener() {
        @Override
        public void onClick(Dehelmintization currentDehelmintization) {
            Intent intent = new Intent(DehelmintizationFragment.this.getActivity(), DehelmintizationTakerActivity.class);
            intent.putExtra("oldDehelmintization", currentDehelmintization.getId());
            intent.putExtra("petName", name);
            intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
            startActivity(intent);
            if (mClick != null)
                mClick.start();
        }

        @Override
        public void onLongClick(Dehelmintization currentDehelmintization, CardView cardView) {
            selectedDehelmintization = new Dehelmintization();
            selectedDehelmintization = currentDehelmintization;
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.deleteMenu:
                if (mClick != null)
                    mClick.start();
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
                                .collection("dehelmintization").document(selectedDehelmintization.getId()).delete();
                        Toast.makeText(DehelmintizationFragment.this.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        dehelmintizations.clear();
                        infoFromDataBase();
                        updateRecycler();
                    }
                });
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (mClick != null)
                            mClick.start();
                    }
                });
                alertDialog.show();
                return true;

            default: return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
