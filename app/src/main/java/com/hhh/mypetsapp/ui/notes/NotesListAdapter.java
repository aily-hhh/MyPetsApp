package com.hhh.mypetsapp.ui.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hhh.mypetsapp.R;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    List<Notes> list;
    NotesClickListener listener;

    public NotesListAdapter(NotesFragment context, List<Notes> list, NotesClickListener listener) {
        this.context = context.getContext();
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.titleNotes.setText(list.get(position).getTitle());
        holder.titleNotes.setSelected(true);
        holder.descriptionNotes.setText(list.get(position).getDescription());
        holder.dateNotes.setText(list.get(position).getDate());
        holder.dateNotes.setSelected(true);

        if(list.get(position).isPinned()){
            holder.pinnedNotes.setImageResource(R.drawable.blue_right_pushpin);
        } else {
            holder.pinnedNotes.setImageResource(0);
        }

        holder.notesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(holder.getAdapterPosition()));
            }
        });

        holder.notesContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.notesContainer);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
