package com.example.plan.Fragments;
import com.example.plan.EditorActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.plan.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time;
        String stringMinute = String.valueOf(minute);
        if (minute < 10){
            stringMinute = "0" + String.valueOf(minute) ;
        }
        if (EditorActivity.mStartTimePicker){
            TextView textView = getActivity().findViewById(R.id.start_time_picker);
            time = hourOfDay + ":" + stringMinute;
            textView.setText(String.valueOf(time));
        } else {
            TextView textView = getActivity().findViewById(R.id.end_time_picker);
            time = hourOfDay + ":" + stringMinute;
            textView.setText(String.valueOf(time));
        }
    }
}
