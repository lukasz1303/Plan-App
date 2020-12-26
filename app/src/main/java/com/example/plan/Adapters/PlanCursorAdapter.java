package com.example.plan.Adapters;
import com.example.plan.R;
import com.example.plan.data.PlanContract.PlanEntry;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlanCursorAdapter extends CursorAdapter {

    public PlanCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView startTimeTextView = view.findViewById(R.id.start_time_text_view);
        TextView endTimeTextView = view.findViewById(R.id.end_time_text_view);
        TextView subjectTextView = view.findViewById(R.id.subject_text_view);
        TextView lecturerTextView = view.findViewById(R.id.lecturer_text_view);
        TextView typeOfClassesTextView = view.findViewById(R.id.type_of_classes_text_view);
        TextView roomTextView = view.findViewById(R.id.room_text_view);
        LinearLayout singleSubjectLinearLayout = view.findViewById(R.id.subject_item);

        String startTime = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_START_TIME));
        String endTime = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_END_TIME));
        String subject = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_SUBJECT));
        String lecturer = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_LECTURER));
        int intTypeOfClasses = cursor.getInt(cursor.getColumnIndex(PlanEntry.COLUMN_TYPE_OF_CLASSES));
        String room = cursor.getString(cursor.getColumnIndex(PlanEntry.COLUMN_ROOM));
        int backgroundColorNumber = cursor.getInt(cursor.getColumnIndex(PlanEntry.COLUMN_COLOR));


        if (startTime.substring(0,1).equals("0")){
            startTime = startTime.substring(1);
        }

        if (endTime.substring(0,1).equals("0")){
            endTime = endTime.substring(1);
        }

        String typeOfClasses;
        switch(intTypeOfClasses){
            case PlanEntry.TYPE_EXERCISES:
                typeOfClasses = context.getString(R.string.type_of_classes_exercises);
                break;
            case PlanEntry.TYPE_LABS:
                typeOfClasses = context.getString(R.string.type_of_classes_labs);
                break;
            default:
                typeOfClasses = context.getString(R.string.type_of_classes_lecture);
                break;
        }
        int backgroundColor;

        switch (backgroundColorNumber){
            case PlanEntry.COLOR_WHITE:
                backgroundColor = R.color.white_color;
                break;
            case PlanEntry.COLOR_LIGHT_BLUE:
                backgroundColor = R.color.light_blue_color;
                break;
            case PlanEntry.COLOR_GREEN:
                backgroundColor = R.color.green_color;
                break;
            case PlanEntry.COLOR_LIGHT_GREEN:
                backgroundColor = R.color.light_green_color;
                break;
            case PlanEntry.COLOR_PURPLE:
                backgroundColor = R.color.purple_color;
                break;
            case PlanEntry.COLOR_ORANGE:
                backgroundColor = R.color.orange_color;
                break;
            case PlanEntry.COLOR_LIGHT_ORANGE:
                backgroundColor = R.color.light_orange_color;
                break;
            case PlanEntry.COLOR_RED:
                backgroundColor = R.color.red_color;
                break;
            case PlanEntry.COLOR_YELLOW:
                backgroundColor = R.color.yellow_color;
                break;
            case PlanEntry.COLOR_DARK_YELLOW:
                backgroundColor = R.color.dark_yellow_color;
                break;
            case PlanEntry.COLOR_DARK_BLUE:
                backgroundColor = R.color.dark_blue_color;
                break;
            case PlanEntry.COLOR_GREY:
                backgroundColor = R.color.grey_color;
                break;
            default:
                backgroundColor = R.color.transparent_color;
                break;
        }


        if (intTypeOfClasses==PlanEntry.TYPE_NONE){
            typeOfClassesTextView.setVisibility(View.GONE);
        } else{
            typeOfClassesTextView.setText(typeOfClasses);
        }

        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);
        subjectTextView.setText(subject);
        if (lecturer.isEmpty()){
            lecturerTextView.setVisibility(View.GONE);

        } else{
            lecturerTextView.setText(lecturer);
            lecturerTextView.setVisibility(View.VISIBLE);
        }

        GradientDrawable drawable = (GradientDrawable)singleSubjectLinearLayout.getBackground();
        drawable.setColor(ContextCompat.getColor(context,backgroundColor));
        if (backgroundColor == R.color.white_color || backgroundColor == R.color.transparent_color ){
            drawable.setStroke(convertDpToPx(2),ContextCompat.getColor(context,R.color.colorAccent));
        } else {
            drawable.setStroke(convertDpToPx(1),ContextCompat.getColor(context,backgroundColor));
        }

        roomTextView.setText(room);
        singleSubjectLinearLayout.setBackground(drawable);
        //singleSubjectLinearLayout.setBackgroundColor(ContextCompat.getColor(context,backgroundColor));

    }

    private int convertDpToPx(int dp){
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
