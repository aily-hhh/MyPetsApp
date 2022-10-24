package com.hhh.mypetsapp.sideBar.gallery;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hhh.mypetsapp.ItemViewModel;
import com.hhh.mypetsapp.R;
import com.hhh.mypetsapp.databinding.FragmentGalleryBinding;
import com.hhh.mypetsapp.sideBar.notes.NotesFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class GalleryFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private FragmentGalleryBinding binding;
    RecyclerView recyclerGallery;
    FloatingActionButton addGalleryButton;
    TextView notElemGallery;
    GalleryAdapter galleryAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ArrayList<String> images = new ArrayList<>();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ItemViewModel viewModel;
    private String name;
    private SharedPreferences defPref;
    MediaPlayer mDelete;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy ',' HH:mm");
    private Gallery selectedImage;


    private static final int MY_READ_PERMISSION_CODE = 101;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerGallery = binding.recyclerGallery;
        addGalleryButton = binding.addGalleryButton;
        notElemGallery = binding.notElemGallery;

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        name = viewModel.namePet;

        if (ContextCompat.checkSelfPermission(GalleryFragment.this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        else {
            loadImages();
        }

        addGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });


        return root;
    }

    private void addImage() {
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    setImage(uri);
                }
            });

    private void setImage(Uri uri)
    {
        String photoStr = UUID.randomUUID().toString();
        StorageReference ref
                = storageReference.child("images/" + photoStr);
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(
                    UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(GalleryFragment.this.getContext(), "Image Uploaded!!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(GalleryFragment.this.getContext(), "Failed " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        Gallery newPhoto = new Gallery();
        newPhoto.setId(UUID.randomUUID().toString());
        newPhoto.setImageName(photoStr);
        String date = dateFormat.format(calendar.getTime());
        newPhoto.setDate(date);
        db.collection("users").document(uID)
                .collection("pets").document(name)
                .collection("gallery").document(newPhoto.getId())
                .set(newPhoto);
    }

    private void loadImages() {
        recyclerGallery.setHasFixedSize(true);
        recyclerGallery.setLayoutManager(new GridLayoutManager(this.getContext(), 4));
        galleryAdapter = new GalleryAdapter(this.getContext(), images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(int pos) {
                //do something with photo
                Intent intent  = new Intent (GalleryFragment.this.getContext(), DetailActivity.class);
                intent.putExtra("imagesGallery", images);
                intent.putExtra("positionImage", pos);
                startActivity (intent);
            }

            @Override
            public void onLongClick(Gallery gallery, CardView cardView) {
                selectedImage = new Gallery();
                selectedImage = gallery;
                showPopUp(cardView);
            }
        });

        recyclerGallery.setAdapter(galleryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        images.clear();
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
            mDelete = MediaPlayer.create(this.getContext(), R.raw.delete);
        }
        else {
            mDelete = null;
        }
    }

    private void infoFromDataBase() {
        db.collection("users").document(uID)
                .collection("pets").document(name).collection("gallery")
                .orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Gallery newImage = document.toObject(Gallery.class);
                                images.add(newImage.imageName);
                            }
                            loadImages();
                            if (images.isEmpty())
                                notElemGallery.setText(R.string.notElem);
                            else
                                notElemGallery.setText("");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

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
                                .collection("gallery").document(selectedImage.getId()).delete();
                        Toast.makeText(GalleryFragment.this.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        images.clear();
                        infoFromDataBase();
                        loadImages();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}