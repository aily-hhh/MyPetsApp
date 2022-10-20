package com.hhh.mypetsapp.sideBar.gallery;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.hhh.mypetsapp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;
    protected PhotoListener photoListener;

    public GalleryAdapter(Context context, ArrayList<String> images, PhotoListener photoListener) {
        this.context = context;
        this.images = images;
        this.photoListener = photoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item, parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String image = images.get(position);

        Task<Uri> storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/"+image).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(holder.galleryImage);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoListener.onPhotoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView galleryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryImage = itemView.findViewById(R.id.galleryImage);

        }
    }

    public interface PhotoListener{
        void onPhotoClick(int pos);
    }
}
