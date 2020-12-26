package com.example.plan.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.plan.R;
import com.example.plan.Subject;

public class SubjectAdapter extends ArrayAdapter<Subject> {

    public SubjectAdapter(Activity context, ArrayList<Subject> subjects){
        super(context,0,subjects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item,parent, false);
        }

        Subject currentSubject = getItem(position);

        TextView startTimeTextView = listItemView.findViewById(R.id.start_time_text_view);
        startTimeTextView.setText(currentSubject.getStartTime());

        TextView endTimeTextView = listItemView.findViewById(R.id.end_time_text_view);
        endTimeTextView.setText(currentSubject.getEndTime());

        TextView nameTextView = listItemView.findViewById(R.id.subject_text_view);
        nameTextView.setText(currentSubject.getName());

        TextView lecturerTextView = listItemView.findViewById(R.id.lecturer_text_view);
        lecturerTextView.setText(currentSubject.getLecturer());

        TextView roomTextView = listItemView.findViewById(R.id.room_text_view);
        roomTextView.setText(currentSubject.getRoom());

        View textContainer = listItemView.findViewById(R.id.subject_item);
        if(currentSubject.hasColor()){
            textContainer.setBackgroundColor(ContextCompat.getColor(getContext(),currentSubject.getColorResourceId()));
        }

        return listItemView;
    }
}
