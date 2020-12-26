package com.example.plan.Fragments.Days;

import com.example.plan.EditorActivity;
import com.example.plan.Adapters.PlanCursorAdapter;
import com.example.plan.R;
import com.example.plan.data.PlanContract.PlanEntry;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MondayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MondayFragment() {
        // Required empty public constructor
    }

    private static final int PLAN_LOADER = 0;
    private PlanCursorAdapter mAdapter;
    private boolean mIsScrollingUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.plan_list, container, false);

        getLoaderManager().initLoader(PLAN_LOADER, null, this);

        ListView listView = rootView.findViewById(R.id.list);

        mAdapter = new PlanCursorAdapter(getContext(), null);

        listView.setEmptyView(rootView.findViewById(R.id.empty_view));
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(PlanEntry.CONTENT_URI, id);
                intent.setData(uri);
                intent.putExtra("day", PlanEntry.DAY_MONDAY);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_day, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inert_dummy_data:
                insertSubject();
                return true;
            case R.id.action_add_new_subject:
                Intent intent = new Intent(getActivity(), EditorActivity.class);
                intent.putExtra("day", PlanEntry.DAY_MONDAY);
                startActivity(intent);
                return true;
            case R.id.action_delete_all_subjects:
                showDeleteConfirmationDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllSubject() {

        String selection = PlanEntry.COLUMN_DAY + "=?";
        String[] selectionArgs = {String.valueOf(PlanEntry.DAY_MONDAY)};

        int rowsDeleted = getContext().getContentResolver().delete(PlanEntry.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted == 0) {
            Toast.makeText(getContext(), R.string.delete_all_subjects_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.delete_all_subjects_successful, Toast.LENGTH_SHORT).show();
            getLoaderManager().restartLoader(PLAN_LOADER, null, this);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.dialog_delete_all_subjects);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllSubject();
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

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
        String selection = PlanEntry.COLUMN_DAY + "=?";
        String[] selectionArgs = {String.valueOf(PlanEntry.DAY_MONDAY)};
        return new CursorLoader(getActivity().getApplicationContext(), PlanEntry.CONTENT_URI, projection, selection, selectionArgs, PlanEntry.COLUMN_START_TIME + " ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void insertSubject() {


        ContentValues values = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME, "8:00");
        values.put(PlanEntry.COLUMN_END_TIME, "9:30");
        values.put(PlanEntry.COLUMN_SUBJECT, "Programowanie niskopoziomowe");
        values.put(PlanEntry.COLUMN_LECTURER, "dr inż. Marcin Radom");
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, PlanEntry.TYPE_LECTURE);
        values.put(PlanEntry.COLUMN_ROOM, "L053 BT");
        values.put(PlanEntry.COLUMN_COLOR, PlanEntry.COLOR_LIGHT_ORANGE);
        values.put(PlanEntry.COLUMN_DAY, PlanEntry.DAY_MONDAY);

        Uri newUri = getActivity().getApplicationContext().getContentResolver().insert(PlanEntry.CONTENT_URI, values);

        /*ContentValues values1 = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME,"8:00");
        values.put(PlanEntry.COLUMN_END_TIME,"9:30");
        values.put(PlanEntry.COLUMN_SUBJECT,"Programowanie deklaratywne");
        values.put(PlanEntry.COLUMN_LECTURER,"dr inż. Artur Michalski");
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, PlanEntry.TYPE_LECTURE);
        values.put(PlanEntry.COLUMN_ROOM,"L053 BT");
        values.put(PlanEntry.COLUMN_COLOR, PlanEntry.COLOR_ORANGE);
        values.put(PlanEntry.COLUMN_DAY, PlanEntry.DAY_MONDAY);

        //Uri newUri1 = getActivity().getApplicationContext().getContentResolver().insert(PlanEntry.CONTENT_URI,values1);

        ContentValues values2 = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME,"9:45");
        values.put(PlanEntry.COLUMN_END_TIME,"11:15");
        values.put(PlanEntry.COLUMN_SUBJECT,"Algorytmy i struktury danych");
        values.put(PlanEntry.COLUMN_LECTURER,"prof. dr hab. inż. Jacek Błażewicz");
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, PlanEntry.TYPE_LECTURE);
        values.put(PlanEntry.COLUMN_ROOM,"L053 BT");
        values.put(PlanEntry.COLUMN_COLOR, PlanEntry.COLOR_LIGHT_BLUE);
        values.put(PlanEntry.COLUMN_DAY, PlanEntry.DAY_MONDAY);

        //Uri newUri2 = getActivity().getApplicationContext().getContentResolver().insert(PlanEntry.CONTENT_URI,values2);

        ContentValues values3 = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME,"11:45");
        values.put(PlanEntry.COLUMN_END_TIME,"13:15");
        values.put(PlanEntry.COLUMN_SUBJECT,"Programowanie deklaratywne");
        values.put(PlanEntry.COLUMN_LECTURER,"dr inż. Artur Michalski");
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, PlanEntry.TYPE_LABS);
        values.put(PlanEntry.COLUMN_ROOM,"lab. 43");
        values.put(PlanEntry.COLUMN_COLOR, PlanEntry.COLOR_ORANGE);
        values.put(PlanEntry.COLUMN_DAY, PlanEntry.DAY_MONDAY);

        //Uri newUri3 = getActivity().getApplicationContext().getContentResolver().insert(PlanEntry.CONTENT_URI,values3);

        ContentValues values4 = new ContentValues();
        values.put(PlanEntry.COLUMN_START_TIME,"13:30");
        values.put(PlanEntry.COLUMN_END_TIME,"15:00");
        values.put(PlanEntry.COLUMN_SUBJECT,"Programowanie niskopoziomowe");
        values.put(PlanEntry.COLUMN_LECTURER,"dr inż. Michał Melosik");
        values.put(PlanEntry.COLUMN_TYPE_OF_CLASSES, PlanEntry.TYPE_LABS);
        values.put(PlanEntry.COLUMN_ROOM,"lab. 507");
        values.put(PlanEntry.COLUMN_COLOR, PlanEntry.COLOR_LIGHT_GREEN);
        values.put(PlanEntry.COLUMN_DAY, PlanEntry.DAY_MONDAY);

        //Uri newUri4 = getActivity().getApplicationContext().getContentResolver().insert(PlanEntry.CONTENT_URI,values4);

        ContentValues[] arrayOfValues = {values, values1, values2, values3 ,values4};

        int newUris = getActivity().getApplicationContext().getContentResolver().bulkInsert(PlanEntry.CONTENT_URI,arrayOfValues);*/

    }
}
