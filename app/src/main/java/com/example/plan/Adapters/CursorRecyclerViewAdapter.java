package com.example.plan.Adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.plan.EditorActivity;
import com.example.plan.Fragments.EditNoteFragment;
import com.example.plan.R;
import com.example.plan.data.PlanContract;
import com.example.plan.data.PlanContract.NotesEntry;


public class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.ViewHolder> {

    // Because RecyclerView.Adapter in its current form doesn't natively
    // support cursors, we wrap a CursorAdapter that will do all the job
    // for us.
    private CursorAdapter mCursorAdapter;

    public TextView mTitleTextView;
    private TextView mContentTextView;

    private Context mContext;
    private Cursor mCursor;
    private int mSubjectId;
    private int mPositionOfNote;

    public CursorRecyclerViewAdapter(Context context, Cursor c) {

        mContext = context;
        mCursor = c;

        mCursorAdapter = new CursorAdapter(mContext, null, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.card_view_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, final Cursor cursor) {

                final String title = cursor.getString(cursor.getColumnIndex(NotesEntry.COLUMN_TITLE));
                final String content = cursor.getString(cursor.getColumnIndex(NotesEntry.COLUMN_CONTENT));
                int id = cursor.getInt(cursor.getColumnIndex(NotesEntry._ID));
                final Uri uri = ContentUris.withAppendedId(PlanContract.NotesEntry.CONTENT_URI,id);
                //Log.e("URIII", String.valueOf(uri));

                final int position = mPositionOfNote;

                mTitleTextView = view.findViewById(R.id.note_title);
                mContentTextView = view.findViewById(R.id.note_content);

                mTitleTextView.setText(title);
                mContentTextView.setText(content);

                //Log.e(CursorRecyclerViewAdapter.class.getSimpleName(), "Current item position is " + String.valueOf(cursor.getPosition()));
                //Log.e(CursorRecyclerViewAdapter.class.getSimpleName(), "Current item Uri is " + ContentUris.parseId(uri));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditNoteFragment editNoteFragment = new EditNoteFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Title", title);
                        bundle.putString("Content", content);
                        bundle.putInt("Position", position);
                        bundle.putString("URI", uri.toString());
                        //Log.e("SubjectID", String.valueOf(mSubjectId));
                        bundle.putInt("Subject_Id",mSubjectId);
                        editNoteFragment.setArguments(bundle);
                        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        editNoteFragment.show(transaction, "Tag");
                    }
                });
            }
        };
        mCursorAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursor.moveToPosition(position);
        mPositionOfNote = position;
        mCursorAdapter.bindView(holder.itemView, mContext, mCursor);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View view = mCursorAdapter.newView(mContext,null, parent);
        return new ViewHolder(view);
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }


    public void setCursor(Cursor cursor, Activity activity){
        mCursor = cursor;
        notifyItemInserted(mCursor.getCount()-1);
        notifyItemRangeChanged(mCursor.getCount()-1, 1);
        notifyDataSetChanged();

        if (activity instanceof EditorActivity){
            ((EditorActivity) activity).expandAfterInsertItem();
        }
    }


    public void setCursor(Cursor cursor, int position, Activity activity){
        mCursor = cursor;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mCursor.getCount()-position+1);
        notifyDataSetChanged();

        if (activity instanceof EditorActivity){
            ((EditorActivity) activity).collapseAfterDeleteItem();
        }
    }

    public void setSubjectId(int id){
        mSubjectId = id;
    }
}

