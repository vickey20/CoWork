package com.vickey.cowork;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsFragment extends Fragment {
    private String TAG = "DetailsFragment";

    private OnFragmentInteractionListener mListener;

    Spinner mSpinnerActivity, mSpinnerPeople;
    EditText mEditTextDescription;
    TextView mTextViewTime, mTextViewDate;

    String[] mActivities = {"Reading", "Writing", "Coding", "Writing assignments", "Painting"}; //for now, will update later
    String[] mPeople = {"1", "2", "3", "4", "5"};

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_details, container, false);

        mSpinnerActivity = (Spinner) v.findViewById(R.id.spinnerActivity);
        mSpinnerPeople = (Spinner) v.findViewById(R.id.spinnerPeople);
        mEditTextDescription = (EditText) v.findViewById(R.id.editTextDescription);
        mTextViewTime = (TextView) v.findViewById(R.id.textViewTime);
        mTextViewDate = (TextView) v.findViewById(R.id.textViewDate);

        mTextViewTime.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new StartTimePicker();
                timeFragment.show(getActivity().getFragmentManager(), "start_time_picker");
            }
        });
        mTextViewDate.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new StartDatePicker();
                dialogFragment.show(getActivity().getFragmentManager(), "start_date_picker");
            }
        });

        mSpinnerActivity.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected activity: " + mActivities[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerPeople.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected num people: " + mPeople[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapterActivity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mActivities);
        mSpinnerActivity.setAdapter(adapterActivity);

        ArrayAdapter<String> adapterPeople = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mPeople);
        mSpinnerPeople.setAdapter(adapterPeople);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTextViewTime.setText(sdf.format(c.getTime()));
        sdf = new SimpleDateFormat("EEE, MMM dd");
        mTextViewDate.setText(sdf.format(c.getTime()));

    }

    /**
     * Create a new instance of DetailsFragment
     */
    static DetailsFragment newInstance() {
        DetailsFragment f = new DetailsFragment();
        return f;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    class StartTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Use the current date as the default date in the picker
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, true);

            return dialog;

        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            updateTimeField( hourOfDay, minute );

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {

                calSet.add(Calendar.DATE, 1);
            }
        }
    }

    class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub

            Calendar c = Calendar.getInstance();
            int startYear = c.get(Calendar.YEAR);
            int startMonth = c.get(Calendar.MONTH);
            int startDay = c.get(Calendar.DAY_OF_MONTH);

            // Use the current date as the default date in the picker
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, startYear, startMonth, startDay);

            return dialog;

        }
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            updateDateField(year, monthOfYear, dayOfMonth);
        }
    }

    public void updateDateField(int startYear, int startMonth, int startDay) {

        try {
            SimpleDateFormat initial = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            Date date = initial.parse("" + startMonth + "-" + startDay + "-" + startYear +"");

            SimpleDateFormat finalFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

            String dateStr = finalFormat.format(date);

            Log.d("MainActivity", "date: " + dateStr);
            mTextViewDate.setText(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateTimeField(int hour, int minute) {

        mTextViewTime.setText("" + hour + ":" + minute);
    }
}
