package com.vickey.cowork.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vickey.cowork.R;
import com.vickey.cowork.activity.CreateActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsFragment extends Fragment {
    private String TAG = "DetailsFragment";
    private final int CHAR_COUNT = 200;
    private DetailsListener mListener;

    Spinner mSpinnerActivity;
    EditText mEditTextDescription;
    TextView mTextViewLocation, mTextViewTime, mTextViewDate, mTextViewCharCount;
    RadioGroup mRadioGroup;

    int mCharCount = 0;
    String[] mActivities;

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
        mEditTextDescription = (EditText) v.findViewById(R.id.editTextDescription);
        mTextViewLocation = (TextView) v.findViewById(R.id.textViewLocationName);
        mTextViewCharCount = (TextView) v.findViewById(R.id.textViewCharCount);
        mTextViewTime = (TextView) v.findViewById(R.id.textViewTime);
        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        mTextViewDate = (TextView) v.findViewById(R.id.textViewDate);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivities =  getResources().getStringArray(R.array.array_activities);
        mTextViewCharCount.setText(String.valueOf(CHAR_COUNT));

        mEditTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCharCount = s.length();
                mTextViewCharCount.setText(String.valueOf(CHAR_COUNT - mCharCount));
            }

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onDescriptionSet(s.toString());
            }
        });

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
                mListener.onActivitySet(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "selected radio: " + checkedId);
                mListener.onNumAttendeesSet(getNumAttendees(checkedId));
            }
        });

        ArrayAdapter<String> adapterActivity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mActivities);
        mSpinnerActivity.setAdapter(adapterActivity);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        String time = sdf.format(c.getTime());
        mTextViewTime.setText(time);

        sdf = new SimpleDateFormat("EEE, MMM dd");
        String date = sdf.format(c.getTime());
        mTextViewDate.setText(date);
    }

    public int getNumAttendees(int checkedId){

        switch (checkedId){
            case R.id.radioButton1:
                return 1;
            case R.id.radioButton2:
                return 2;
            case R.id.radioButton3:
                return 3;
            case R.id.radioButton4:
                return 4;
            case R.id.radioButton5:
                return 5;
        }

        return 1;
    }

    /**
     * Create a new instance of DetailsFragment
     */
    public static DetailsFragment newInstance() {
        DetailsFragment f = new DetailsFragment();
        return f;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            String address = CreateActivity.mCoWork.getLocationName();
            if(address != null && address != "") {
                mTextViewLocation.setText(address);
            } else {
                mTextViewLocation.setText("Couldn't get location");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DetailsListener) activity;
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

    public interface DetailsListener {
        public void onActivitySet(int activityType);
        public void onDescriptionSet(String description);
        public void onNumAttendeesSet(int numAttendees);
        public void onTimeSet(String time);
        public void onDateSet(String date);
    }

    class StartTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Use the current date as the default date in the picker
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, false);

            return dialog;

        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {

                calSet.add(Calendar.DATE, 1);
            }

            updateTimeField( calSet );
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
            updateDateField(year, monthOfYear + 1, dayOfMonth);
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
            mListener.onDateSet(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateTimeField(Calendar calendar) {

        String time = (String) DateFormat.format("hh:mm a", calendar.getTime());
        mTextViewTime.setText(time);
        mListener.onTimeSet(time);
    }
}
