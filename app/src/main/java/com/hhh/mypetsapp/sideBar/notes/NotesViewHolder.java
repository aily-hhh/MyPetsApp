package com.hhh.mypetsapp.sideBar.notes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView notesContainer;
    TextView titleNotes, descriptionNotes, dateNotes;
    ImageView pinnedNotes;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notesContainer = itemView.findViewById(R.id.notesContainer);
        titleNotes = itemView.findViewById(R.id.titleNotes);
        descriptionNotes = itemView.findViewById(R.id.descriptionNotes);
        dateNotes = itemView.findViewById(R.id.dateNotes);
        pinnedNotes = itemView.findViewById(R.id.pinnedNotes);
    }
}
