package com.example.plan.Fragments;


import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plan.EditorActivity;
import com.example.plan.R;
import com.example.plan.data.PlanContract.NotesEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditNoteFragment extends DialogFragment {

    private String mTitle;
    private String mContent;
    private int mPosition;
    private long mSubjectId;

    private EditText mTitleEditText;
    private EditText mContentEditText;

    private Uri mUri;

    private boolean mNoteHasChanged;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mNoteHasChanged = true;
            return false;
        }
    };


    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mTitle = bundle.getString("Title");
            mContent = bundle.getString("Content");
            if (bundle.containsKey("URI")){
                mUri = Uri.parse(bundle.getString("URI"));
            }
            mPosition = bundle.getInt("Position");
            mSubjectId = bundle.getInt("Subject_Id");
            //Log.e("Sub", String.valueOf(mSubjectId));
        }

        mTitleEditText = view.findViewById(R.id.note_title_edit_text);
        mContentEditText = view.findViewById(R.id.note_content_edit_text);

        mTitleEditText.setOnTouchListener(mTouchListener);
        mContentEditText.setOnTouchListener(mTouchListener);

        ImageView backArrowImageView = view.findViewById(R.id.back_arrow_image);
        TextView actionSaveTextView = view.findViewById(R.id.fullscreen_dialog_save);
        TextView actionDeleteTextView = view.findViewById(R.id.fullscreen_dialog_delete);

        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mNoteHasChanged || (mTitleEditText.getText().toString().trim().isEmpty() && mContentEditText.getText().toString().trim().isEmpty())){
                    dismiss();
                } else{
                    DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    };
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
            }
        });

        actionSaveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                dismiss();
            }
        });

        actionDeleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        if (getArguments() != null) {
            mTitleEditText.setText(mTitle);
            mContentEditText.setText(mContent);
        }

        return view;
    }

    private void deleteNote() {
        int rowsDeleted = getActivity().getApplicationContext().getContentResolver().delete(mUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(getContext(), R.string.editor_delete_note_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.editor_delete_note_successful, Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    private void saveNote() {
        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();


        ContentValues values = new ContentValues();
        values.put(NotesEntry.COLUMN_TITLE, title);
        values.put(NotesEntry.COLUMN_CONTENT, content);
        values.put(NotesEntry.COLUMN_ID_OF_SUBJECT, mSubjectId);


        if (mUri==null) {

            if (title.isEmpty() && content.isEmpty()) {
                Toast.makeText(getContext(), R.string.editor_insert_empty_note, Toast.LENGTH_SHORT).show();
                return;
            }

            Uri newUri = getActivity().getApplicationContext().getContentResolver().insert(NotesEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getContext(), R.string.editor_insert_note_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.editor_insert_note_successful, Toast.LENGTH_SHORT).show();
            }
            ((EditorActivity) getActivity()).restartNotesListInsert();

        } else {

            int rowsUpdated = getActivity().getApplicationContext().getContentResolver().update(mUri, values, null, null);
            //Log.e("mSubjectID", String.valueOf(mSubjectId));
            if (rowsUpdated == 0) {
                Toast.makeText(getContext(), R.string.editor_update_note_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.editor_update_note_successful, Toast.LENGTH_SHORT).show();
            }
            ((EditorActivity) getActivity()).restartNotesListUpdate(mPosition);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.action_delete_note);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
                ((EditorActivity) getActivity()).restartNotesListRemove(mPosition);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNote();
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.discard, discardButtonClickListener);
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                if(!mNoteHasChanged || (mTitleEditText.getText().toString().trim().isEmpty() && mContentEditText.getText().toString().trim().isEmpty())){
                    super.onBackPressed();
                } else{
                    DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    };
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
            }
        };

    }
}


