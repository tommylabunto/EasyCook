package com.example.easycook.Home;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends AppCompatDialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private final String LOG_TAG = "DatePickerFragment";

    /**
     * Creates the date picker dialog with the current date from Calendar.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
    }

    /**
     * Grabs the date and passes it back.
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Log.d(LOG_TAG, "onDateSet");
        // send date back to the target fragment
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("selectedDate",
                        (day + "/" + (month + 1) + "/" + year)
                ));
    }
}

