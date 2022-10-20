package com.hhh.mypetsapp.sideBar.gallery;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.hhh.mypetsapp.R;

import java.util.List;

public class ViewPager2AdapterGallery extends RecyclerView.Adapter<ViewPager2AdapterGallery.ViewHolder> {

    private List<String> images;
    private Context ctx;

    public ViewPager2AdapterGallery(Context ctx, List<String> images){
        this.ctx = ctx;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.fragment_detail_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2AdapterGallery.ViewHolder holder, int position) {
        String image = images.get(position);

        Task<Uri> storageReference = FirebaseStorage.getInstance().getReference()
                .child("images/"+image).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(holder.galleryDetailImageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView galleryDetailImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            galleryDetailImageView = itemView.findViewById(R.id.galleryDetailImageView);
        }
    }
}

