package com.hhh.mypetsapp.ui.identification;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhh.mypetsapp.ItemViewModel;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.FragmentIdentificationBinding;

public class IdentificationFragment extends Fragment {

    private FragmentIdentificationBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ItemViewModel viewModel;
    private String name;
    private boolean isOld = true;

    static int DIALOG_DATE_MICRO = 1;
    static int DIALOG_DATE_TATTOO = 2;

    ImageView saveIdentification;
    EditText microchipNumber;
    static EditText dateOfMicrochipping;
    EditText microchipLocation;
    EditText tattooNumber;
    static EditText dateOfTattooing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentIdentificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        saveIdentification = binding.saveIdentification;
        microchipNumber = binding.microchipNumber;
        dateOfMicrochipping = binding.dateOfMicrochipping;
        microchipLocation = binding.microchipLocation;
        tattooNumber = binding.tattooNumber;
        dateOfTattooing = binding.dateOfTattooing;

        saveIdentification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatingToDataBase();
            }
        });

        dateOfMicrochipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view, DIALOG_DATE_MICRO);
            }
        });

        dateOfTattooing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view, DIALOG_DATE_TATTOO);
            }
        });

        infoFromDataBase();

        return root;
    }

    public static void onClickMicro(int year, int monthOfYear, int dayOfMonth) {
        dateOfMicrochipping.setText(dayOfMonth + "." + monthOfYear + "." + year);
    }

    public static void onClickTattoo(int year, int monthOfYear, int dayOfMonth) {
        dateOfTattooing.setText(dayOfMonth + "." + monthOfYear + "." + year);
    }

    public void showDatePickerDialog(View v, int id)
    {
        DialogFragment newFragment = new DatePickerFragment(id);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void infoFromDataBase(){
        final DocumentReference docRef = db.collection("users").document(uID)
                .collection("pets").document(name)
                .collection("identification").document(name);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@com.google.firebase.database.annotations.Nullable DocumentSnapshot snapshot,
                                @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if(snapshot.get("microchipNumber") != null)
                        microchipNumber.setText(snapshot.get("microchipNumber").toString().trim());
                    if(snapshot.get("dateOfMicrochipping") != null)
                        dateOfMicrochipping.setText(snapshot.get("dateOfMicrochipping").toString().trim());
                    if(snapshot.get("microchipLocation") != null)
                        microchipLocation.setText(snapshot.get("microchipLocation").toString().trim());
                    if(snapshot.get("tattooNumber") != null)
                        tattooNumber.setText(snapshot.get("tattooNumber").toString().trim());
                    if(snapshot.get("dateOfTattooing") != null)
                        dateOfTattooing.setText(snapshot.get("dateOfTattooing").toString().trim());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void updatingToDataBase() {
        Toast.makeText(this.getContext(), R.string.saved, Toast.LENGTH_SHORT).show();

        db.collection("users").document(uID)
                .collection("pets").document(name)
                .collection("identification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                    }
                    if (count > 0)
                        isOld = true;
                    else
                        isOld = false;
                }
            }
        });

        if (isOld){
            DocumentReference documentReference = db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("identification").document(name);
            documentReference.update("microchipNumber", microchipNumber.getText().toString().trim());
            documentReference.update("microchipLocation", microchipLocation.getText().toString().trim());
            documentReference.update("dateOfMicrochipping", dateOfMicrochipping.getText().toString().trim());
            documentReference.update("tattooNumber", tattooNumber.getText().toString().trim());
            documentReference.update("dateOfTattooing", dateOfTattooing.getText().toString().trim());
        }
        else {
            Identification identification = new Identification();
            identification.setMicrochipNumber(microchipNumber.getText().toString().trim());
            identification.setDateOfMicrochipping(dateOfMicrochipping.getText().toString().trim());
            identification.setMicrochipLocation(microchipLocation.getText().toString().trim());
            identification.setTattooNumber(tattooNumber.getText().toString().trim());
            identification.setDateOfTattooing(dateOfTattooing.getText().toString().trim());

            db.collection("users").document(uID)
                    .collection("pets").document(name)
                    .collection("identification").document(name).set(identification);
        }
    }
}
