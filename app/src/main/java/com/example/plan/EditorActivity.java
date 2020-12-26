package com.example.plan;

import com.example.plan.Adapters.ColorsSpinnerAdapter;
import com.example.plan.Adapters.CursorRecyclerViewAdapter;
import com.example.plan.Fragments.EditNoteFragment;
import com.example.plan.Fragments.TimePickerFragment;
import com.example.plan.data.PlanContract.PlanEntry;
import com.example.plan.data.PlanContract.NotesEntry;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Spinner mTypeOfClassesSpinner;
    private Spinner mColorSpinner;

    private EditText mSubjectEditText;
    private EditText mLecturerEditText;
    private EditText mRoomEditText;
    private TextView mStartTimeTextView;
    private TextView mEndTimeTextView;
    private TextView mNumberOfNotesTextView;
    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayoutHeader;
    private LinearLayout mMainNotesLinearLayout;
    private RecyclerView mRecyclerView;
    private ImageView mListArrow;

    private static final int EDITOR_SUBJECT_LOADER = 1;
    public static final int NOTES_LOADER = 2;

    private Uri mUri;
    private int mSubjectId = -1;

    private int mTypeOfClasses = 0;
    private int mColor = 0;
    public static boolean mStartTimePicker;

    private boolean mSubjectHasChanged;
    private boolean mInsert;
    private boolean mUpdate;
    private boolean mRemove;
    private int mItemPositionToRemove;
    private int mItemPositionToUpdate;

    private CursorRecyclerViewAdapter mAdapter;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mSubjectHasChanged = true;
            return false;
        }
    };

    private int[] mColors = {
            R.drawable.circle_transparent,
            R.drawable.circle_light_blue,
            R.drawable.circle_dark_blue,
            R.drawable.circle_green,
            R.drawable.circle_light_green,
            R.drawable.circle_purple,
            R.drawable.circle_orange,
            R.drawable.circle_light_orange,
            R.drawable.circle_red,
            R.drawable.circle_yellow,
            R.drawable.circle_dark_yellow,
            R.drawable.circle_grey,
            R.drawable.circle_white};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_image));

        setupViews();

        GradientDrawable drawable = (GradientDrawable)mMainNotesLinearLayout.getBackground();
        drawable.setColor(ContextCompat.getColor(this,R.color.backgroundNotesColor));
        drawable.setStroke(convertDpToPx(2),ContextCompat.getColor(this,R.color.colorAccent));

        mUri = getIntent().getData();
        if (mUri == null) {
            setTitle(R.string.add_a_subject);
            invalidateOptionsMenu();
            mMainNotesLinearLayout.setVisibility(View.GONE);
        } else {
            setTitle(R.string.edit_subject);
            getSupportLoaderManager().initLoader(EDITOR_SUBJECT_LOADER, null, this);
            getSupportLoaderManager().initLoader(NOTES_LOADER, null, this);
        }

        setupSpinner();

        mTypeOfClassesSpinner.setOnTouchListener(mTouchListener);
        mColorSpinner.setOnTouchListener(mTouchListener);
        mSubjectEditText.setOnTouchListener(mTouchListener);
        mLecturerEditText.setOnTouchListener(mTouchListener);
        mRoomEditText.setOnTouchListener(mTouchListener);
        mStartTimeTextView.setOnTouchListener(mTouchListener);
        mEndTimeTextView.setOnTouchListener(mTouchListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mLinearLayout.setVisibility(View.GONE);
        ImageView addNote = findViewById(R.id.add_note);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNoteFragment editNoteFragment = new EditNoteFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("Subject_Id",mSubjectId);
                editNoteFragment.setArguments(bundle);
                editNoteFragment.show(transaction, "Tag");
            }
        });

        mLinearLayoutHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount()!=0){
                    if (mLinearLayout.getVisibility()==View.GONE){
                        expand();
                    }else{
                        collapse();
                    }
                }
            }
        });
    }


    private void setupViews(){
        mTypeOfClassesSpinner = findViewById(R.id.type_of_classes_spinner);
        mColorSpinner = findViewById(R.id.color_spinner);
        mSubjectEditText = findViewById(R.id.edit_subject);
        mLecturerEditText = findViewById(R.id.edit_lecturer);
        mRoomEditText = findViewById(R.id.edit_classroom);
        mStartTimeTextView = findViewById(R.id.start_time_picker);
        mEndTimeTextView = findViewById(R.id.end_time_picker);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLinearLayout = findViewById(R.id.expandable);
        mLinearLayoutHeader = findViewById(R.id.header);
        mListArrow = findViewById(R.id.list_arrow);
        mNumberOfNotesTextView = findViewById(R.id.number_of_notes);
        mMainNotesLinearLayout = findViewById(R.id.main_notes_linear_layout);
    }


    private void expand() {
        mLinearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);

        int maxHeight = (mLinearLayout.getMeasuredHeight()/mAdapter.getItemCount()*5)+1;
        ValueAnimator mAnimator;

        if (mLinearLayout.getMeasuredHeight()>maxHeight){
            mAnimator = slideAnimator(0, maxHeight);
        }
        else {
            mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
        }

        mAnimator.start();
    }


    public void expandAfterInsertItem() {
        mLinearLayout.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mLinearLayout.measure(widthSpec, heightSpec);
        if (mAdapter.getItemCount()==0){
            ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
            mAnimator.start();
        } else{
            int maxHeight = (mLinearLayout.getMeasuredHeight()/mAdapter.getItemCount()*5)+1;
            if (mLinearLayout.getMeasuredHeight()<=maxHeight){
                ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
                mAnimator.start();
            }
        }
    }


    private void collapse() {
        int finalHeight = mLinearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
                mListArrow.setImageResource(R.mipmap.list_down_arrow);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }


    public void collapseAfterDeleteItem() {
        int finalHeight = mLinearLayout.getHeight();

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mLinearLayout.measure(widthSpec, heightSpec);

        if (mAdapter.getItemCount()==0){
            ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mAnimator.start();
        }else {
            int maxHeight = (mLinearLayout.getMeasuredHeight()/mAdapter.getItemCount()*5)+1;
            if (mLinearLayout.getMeasuredHeight()<=maxHeight) {
                ValueAnimator mAnimator = slideAnimator(finalHeight, mLinearLayout.getMeasuredHeight());
                mAnimator.start();
            }
        }
    }


    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);

            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mListArrow.setImageResource(R.mipmap.list_up_arrow);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animator;
    }


    public void showTimePickerDialog(View v) {
        mStartTimePicker = (v.getId() == R.id.start_time_picker);

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    private void setupSpinner() {

        ArrayAdapter typeOfClassesSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_type_of_classes_options, android.R.layout.simple_spinner_item);

        typeOfClassesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTypeOfClassesSpinner.setAdapter(typeOfClassesSpinnerAdapter);

        mTypeOfClassesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_of_classes_lecture))) {
                        mTypeOfClasses = PlanEntry.TYPE_LECTURE;
                    } else if (selection.equals(getString(R.string.type_of_classes_exercises))) {
                        mTypeOfClasses = PlanEntry.TYPE_EXERCISES;
                    } else if (selection.equals(getString(R.string.type_of_classes_labs))){
                        mTypeOfClasses = PlanEntry.TYPE_LABS;
                    } else{
                        mTypeOfClasses = PlanEntry.TYPE_NONE;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTypeOfClasses = PlanEntry.TYPE_NONE;
            }
        });


        ColorsSpinnerAdapter colorSpinnerAdapter = new ColorsSpinnerAdapter(getApplicationContext(), mColors);
        mColorSpinner.setAdapter(colorSpinnerAdapter);

        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer selection = (Integer) parent.getItemAtPosition(position);

                switch (selection) {
                    case R.drawable.circle_white:
                        mColor = PlanEntry.COLOR_WHITE;
                        break;
                    case R.drawable.circle_light_blue:
                        mColor = PlanEntry.COLOR_LIGHT_BLUE;
                        break;
                    case R.drawable.circle_dark_blue:
                        mColor = PlanEntry.COLOR_DARK_BLUE;
                        break;
                    case R.drawable.circle_green:
                        mColor = PlanEntry.COLOR_GREEN;
                        break;
                    case R.drawable.circle_light_green:
                        mColor = PlanEntry.COLOR_LIGHT_GREEN;
                        break;
                    case R.drawable.circle_purple:
                        mColor = PlanEntry.COLOR_PURPLE;
                        break;
                    case R.drawable.circle_orange:
                        mColor = PlanEntry.COLOR_ORANGE;
                        break;
                    case R.drawable.circle_light_orange:
                        mColor = PlanEntry.COLOR_LIGHT_ORANGE;
                        break;
                    case R.drawable.circle_red:
                        mColor = PlanEntry.COLOR_RED;
                        break;
                    case R.drawable.circle_yellow:
                        mColor = PlanEntry.COLOR_YELLOW;
                        break;
                    case R.drawable.circle_dark_yellow:
                        mColor = PlanEntry.COLOR_DARK_YELLOW;
                        break;
                    case R.drawable.circle_grey:
                        mColor = PlanEntry.COLOR_GREY;
                        break;
                    default:
                        mColor = PlanEntry.COLOR_TRANSPARENT;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mColor = PlanEntry.COLOR_TRANSPARENT;
            }
        });
    }


    private void save_subject(){

        String subject = mSubjectEditText.getText().toString().trim();
        String lecturer = mLecturerEditText.getText().toString().trim();
        String classroom = mRoomEditText.getText().toString().trim();
        String startTime = mStartTimeTextView.getText().toString().trim();
        String endTime = mEndTimeTextView.getText().toString().trim();
        int day = getIntent().getIntExtra("day",0);

        String startTimeEdited;
        String endTimeEdited;
        if (startTime.length() ==4){
            startTimeEdited = "0" + startTime;
        } else{
            startTimeEdited = startTime;
        }
        if (endTime.length() == 4){
            endTimeEdited = "0" + endTime;
        } else {
            endTimeEdited = endTime;
        }

        ContentValues values = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME, startTimeEdited);
        values.put(PlanEntry.COLUMN_END_TIME, endTimeEdited);
        values.put(PlanEntry.COLUMN_SUBJECT, subject);
        values.put(PlanEntry.COLUMN_LECTURER, lecturer);
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, mTypeOfClasses);
        values.put(PlanEntry.COLUMN_ROOM, classroom);
        values.put(PlanEntry.COLUMN_COLOR, mColor);
        values.put(PlanEntry.COLUMN_DAY,day);

        if(mUri != null){

            int rowUpdated = getContentResolver().update(mUri,values,null,null);

            if (rowUpdated == 0){
                Toast.makeText(this, R.string.editor_update_subject_failed, Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, R.string.editor_update_subject_successful, Toast.LENGTH_SHORT).show();
            }
        } else {

            Uri newUri = getContentResolver().insert(PlanEntry.CONTENT_URI,values);

            if(newUri==null){
                Toast.makeText(this, R.string.editor_insert_subject_failed, Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, R.string.editor_insert_subject_successful, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private void showSavingIncompleteSubjectDialog(){

        String subject = mSubjectEditText.getText().toString().trim();
        String classroom = mRoomEditText.getText().toString().trim();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (subject.isEmpty() && classroom.isEmpty()){
            builder.setMessage(R.string.alert_no_subject_and_room);
        } else if (subject.isEmpty()){
            builder.setMessage(R.string.alert_no_subject);
        } else if (classroom.isEmpty()){
            builder.setMessage(R.string.alert_no_room);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        if(!mSubjectHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mUri==null){
            MenuItem menuItem = menu.findItem(R.id.action_delete_subject);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_subject:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_save_subject:

                String subject = mSubjectEditText.getText().toString().trim();
                String classroom = mRoomEditText.getText().toString().trim();
                String startTime  = mStartTimeTextView.getText().toString().trim();
                String endTime = mEndTimeTextView.getText().toString().trim();

                if (startTime.length()>endTime.length()){
                    endTime = "0" + endTime;
                } else if (endTime.length()>startTime.length()){
                    startTime = "0" + startTime;
                }

                if (subject.isEmpty() || classroom.isEmpty()){
                    showSavingIncompleteSubjectDialog();
                } else if(endTime.compareTo(startTime)<0){
                    showTimeSubjectDialog();
                } else{
                    save_subject();
                    finish();
                }
                return true;

            case android.R.id.home:
                if (!mSubjectHasChanged) {
                    finish();
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void deleteSubject(){
        int rowsDeleted = getContentResolver().delete(mUri,null,null);
        if (rowsDeleted==0){
            Toast.makeText(this,R.string.editor_delete_subject_failed,Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this,R.string.editor_delete_subject_successful,Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.action_delete_subject);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSubject();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subject = mSubjectEditText.getText().toString().trim();
                String classroom = mRoomEditText.getText().toString().trim();
                String startTime  = mStartTimeTextView.getText().toString().trim();
                String endTime = mEndTimeTextView.getText().toString().trim();

                if (startTime.length()>endTime.length()){
                    endTime = "0" + endTime;
                } else if (endTime.length()>startTime.length()){
                    startTime = "0" + startTime;
                }

                if (subject.isEmpty() || classroom.isEmpty()){
                    showSavingIncompleteSubjectDialog();
                } else if(endTime.compareTo(startTime)<0){
                    showTimeSubjectDialog();
                } else{
                    save_subject();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.discard, discardButtonClickListener);
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showTimeSubjectDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.end_time_later_than_start_time);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save_subject();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void restartNotesListInsert(){
        mInsert = true;
        getSupportLoaderManager().restartLoader(NOTES_LOADER, null, this);
    }

    public void restartNotesListUpdate(int position){
        mUpdate = true;
        mItemPositionToUpdate = position;
        getSupportLoaderManager().restartLoader(NOTES_LOADER, null, this);
    }

    public void restartNotesListRemove(int position){
        mRemove = true;
        mItemPositionToRemove = position;
        getSupportLoaderManager().restartLoader(NOTES_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {

        switch (id){
            case EDITOR_SUBJECT_LOADER:
                String[] projection = {
                        PlanEntry._ID,
                        PlanEntry.COLUMN_START_TIME,
                        PlanEntry.COLUMN_END_TIME,
                        PlanEntry.COLUMN_SUBJECT,
                        PlanEntry.COLUMN_LECTURER,
                        PlanEntry.COLUMN_TYPE_OF_CLASSES,
                        PlanEntry.COLUMN_ROOM,
                        PlanEntry.COLUMN_COLOR
                };

                if(ContentUris.parseId(mUri) != -1){
                    return new CursorLoader(this,mUri,projection,null,null,null);
                } else {
                    return null;
                }
            case NOTES_LOADER:
                String[] notesProjection = {
                        NotesEntry._ID,
                        NotesEntry.COLUMN_TITLE,
                        NotesEntry.COLUMN_CONTENT,
                };
                String selection = NotesEntry.COLUMN_ID_OF_SUBJECT + "=?";
                String[] selectionArgs = {String.valueOf(ContentUris.parseId(mUri))};
                return new CursorLoader(this, NotesEntry.CONTENT_URI,notesProjection, selection, selectionArgs, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()){
            case EDITOR_SUBJECT_LOADER:
                if (cursor.moveToFirst()){
                    String startTime = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_START_TIME));
                    if (startTime.charAt(0) == "0".charAt(0)){
                        startTime = startTime.substring(1);
                    }

                    String endTime = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_END_TIME));
                    if (endTime.charAt(0) == "0".charAt(0)){
                        endTime = endTime.substring(1);
                    }

                    mStartTimeTextView.setText(startTime);
                    mEndTimeTextView.setText(endTime);
                    mSubjectEditText.setText(cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_SUBJECT)));
                    mLecturerEditText.setText(cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_LECTURER)));
                    mTypeOfClassesSpinner.setSelection(cursor.getInt(cursor.getColumnIndex(PlanEntry.COLUMN_TYPE_OF_CLASSES)));
                    mRoomEditText.setText(cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_ROOM)));
                    mColorSpinner.setSelection(cursor.getInt(cursor.getColumnIndex(PlanEntry.COLUMN_COLOR)));
                    mSubjectId = cursor.getInt((cursor.getColumnIndex(PlanEntry._ID)));
                    if (mAdapter!=null){
                        //Log.e(EditorActivity.class.getSimpleName(), "EDIT LOADER: " + String.valueOf(mSubjectId));
                        mAdapter.setSubjectId(mSubjectId);
                    }
                    break;
                }
            case NOTES_LOADER:
                if (mInsert){
                    if (mSubjectId != -1){
                        //Log.e(EditorActivity.class.getSimpleName(), "Note LOADER: " + String.valueOf(mSubjectId));
                        mAdapter.setSubjectId(mSubjectId);
                    }

                    mAdapter.setCursor(cursor,this);
                    //Scroll screen to last position
                    //Log.e("Position", String.valueOf(mAdapter.getItemCount()-1));
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);
                    mInsert = false;
                } else if (mRemove){
                    if (mSubjectId != -1){
                        //Log.e(EditorActivity.class.getSimpleName(), "Note LOADER: " + String.valueOf(mSubjectId));
                        mAdapter.setSubjectId(mSubjectId);
                    }
                    mAdapter.setCursor(cursor, mItemPositionToRemove, this);
                    mRemove = false;
                } else if(mUpdate){
                    if (mSubjectId != -1){
                        //Log.e(EditorActivity.class.getSimpleName(), "Note LOADER: " + String.valueOf(mSubjectId));
                        mAdapter.setSubjectId(mSubjectId);
                    }
                    mAdapter.setCursor(cursor,this);
                    //Log.e("Position up", String.valueOf(mItemPositionToUpdate));
                    mRecyclerView.smoothScrollToPosition(mItemPositionToUpdate);
                    mUpdate = false;
                } else {
                    mAdapter = new CursorRecyclerViewAdapter(this, null);
                    mRecyclerView.setAdapter(mAdapter);
                    if (mSubjectId != -1){
                        //Log.e(EditorActivity.class.getSimpleName(), "Note LOADER: " + String.valueOf(mSubjectId));
                        mAdapter.setSubjectId(mSubjectId);
                    }
                    mAdapter.setCursor(cursor);
                }

                if (mAdapter.getItemCount()==0){
                    mNumberOfNotesTextView.setText(getResources().getString(R.string.no_notes));
                    mListArrow.setVisibility(View.GONE);
                }else {
                    mListArrow.setVisibility(View.VISIBLE);
                    mNumberOfNotesTextView.setText(String.valueOf(mAdapter.getItemCount()) + " " + getResources().getString(R.string.notes_to_number_of_notes));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()){
            case EDITOR_SUBJECT_LOADER:
                mStartTimeTextView.setText(null);
                mEndTimeTextView.setText(null);
                mSubjectEditText.setText(null);
                mLecturerEditText.setText(null);
                mTypeOfClassesSpinner.setSelection(0);
                mRoomEditText.setText(null);
                mColorSpinner.setSelection(0);
                break;
            case NOTES_LOADER:
                mAdapter.setCursor(null);
        }
    }

    private int convertDpToPx(int dp){
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
