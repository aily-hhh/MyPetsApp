package com.hhh.mypetsapp.sideBar.identification;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerFragment.OnDateReceiveCallBack mListener;
    private Context context;
    int id;

    public interface OnDateReceiveCallBack {
        void onDateReceive(int dd ,int mm, int yy);
        void onDateReceive2(int dd ,int mm, int yy);
    }

    public DatePickerFragment(int id){
        this.id = id;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        try {
            mListener = (DatePickerFragment.OnDateReceiveCallBack) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDateSetListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (id == 1)
            mListener.onDateReceive(dayOfMonth,monthOfYear,year);
        if (id == 2)
            mListener.onDateReceive2(dayOfMonth,monthOfYear,year);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH+1);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(context, this, year, month, day);
    }
}
