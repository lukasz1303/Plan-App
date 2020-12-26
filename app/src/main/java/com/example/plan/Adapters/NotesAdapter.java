package com.example.plan.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.plan.Fragments.EditNoteFragment;
import com.example.plan.Note;
import com.example.plan.R;
import com.example.plan.data.PlanContract;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    List<Note> mNotes;
    Context mContext;

    public NotesAdapter(Context context, List<Note> notes) {
        mContext = context;
        mNotes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_item, viewGroup, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(view);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {

        final String title = mNotes.get(i).getTitle();
        final String content = mNotes.get(i).getNote();

        noteViewHolder.mTitleTextView.setText(title);
        noteViewHolder.mContentTextView.setText(content);
        final Uri uri = ContentUris.withAppendedId(PlanContract.NotesEntry.CONTENT_URI,i);

        noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNoteFragment editNoteFragment = new EditNoteFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Title", title);
                bundle.putString("Content", content);
                bundle.putString("URI", uri.toString());
                editNoteFragment.setArguments(bundle);
                FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                editNoteFragment.show(transaction, "Tag");
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTitleTextView;
        TextView mContentTextView;

        public NoteViewHolder(View view) {
            super(view);
            mCardView = view.findViewById(R.id.cardView);
            mTitleTextView = view.findViewById(R.id.note_title);
            mContentTextView = view.findViewById(R.id.note_content);
        }
    }
}
