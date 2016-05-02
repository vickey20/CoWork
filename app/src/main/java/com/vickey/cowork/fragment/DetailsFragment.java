package com.vickey.cowork.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vickey.cowork.R;
import com.vickey.cowork.activity.CreateActivity;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.HelperClass;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsFragment extends Fragment {
    private String TAG = "DetailsFragment";
    private final int CHAR_COUNT = 200;
    private DetailsListener mListener;

    ImageView mLocationImage;
    Spinner mSpinnerActivity, mSpinnerDuration;
    EditText mEditTextDescription;
    TextView mTextViewLocation, mTextViewTime, mTextViewDate, mTextViewCharCount;
    RadioGroup mRadioGroup;
    ScrollView mScrollView;

    int mCharCount = 0;
    String[] mActivities, mDurations;

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

        mScrollView = (ScrollView) v.findViewById(R.id.scrollView);
        mLocationImage = (ImageView) v.findViewById(R.id.imageViewMap);
        mSpinnerActivity = (Spinner) v.findViewById(R.id.spinnerActivity);
        mEditTextDescription = (EditText) v.findViewById(R.id.editTextDescription);
        mTextViewLocation = (TextView) v.findViewById(R.id.textViewLocationName);
        mTextViewCharCount = (TextView) v.findViewById(R.id.textViewCharCount);
        mTextViewTime = (TextView) v.findViewById(R.id.textViewTime);
        mRadioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        mTextViewDate = (TextView) v.findViewById(R.id.textViewDate);
        mSpinnerDuration = (Spinner) v.findViewById(R.id.spinnerDuration);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Bitmap bitmap = HelperClass.decodeSampledBitmapFromResource(getResources(), R.drawable.maps_shot, (int) HelperClass.convertDpToPixel(300), (int) HelperClass.convertDpToPixel(200));
        //mLocationImage.setImageBitmap(bitmap);

        mActivities =  getResources().getStringArray(R.array.array_activities);
        mDurations =  getResources().getStringArray(R.array.array_duration);

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

        mSpinnerDuration.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected duration: " + mDurations[position]);
                mListener.onDurationSet(getDurationFromPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mListener.onDurationSet(getDurationFromPosition(0));
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "selected radio: " + checkedId);
                mListener.onNumAttendeesSet(getNumAttendees(checkedId));
            }
        });

        mRadioGroup.check(R.id.radioButton1);

        ArrayAdapter<String> adapterActivity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mActivities);
        mSpinnerActivity.setAdapter(adapterActivity);

        ArrayAdapter<String> adapterDuration = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mDurations);
        mSpinnerDuration.setAdapter(adapterDuration);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TimeAndDate.TIME_FORMAT);
        String time = sdf.format(c.getTime());
        mTextViewTime.setText(time);
        mListener.onTimeSet(time);

        sdf = new SimpleDateFormat(Constants.TimeAndDate.DATE_FORMAT_FOR_DISPLAY);
        String date = sdf.format(c.getTime());
        mTextViewDate.setText(date);

        sdf = new SimpleDateFormat(Constants.TimeAndDate.DATE_FORMAT);
        mListener.onDateSet(sdf.format(c.getTime()));

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private long getDurationFromPosition(int position) {

        switch (position) {
            case 0:
                // 1 hour
                return 1 * 60 * 60 * 1000;
            case 1:
                // 2 hours
                return 2 * 60 * 60 * 1000;
            case 2:
                // 3 hours
                return 3 * 60 * 60 * 1000;
            case 3:
                // 5 hours
                return 5 * 60 * 60 * 1000;
            case 4:
                // 8 hours
                return 8 * 60 * 60 * 1000;

            default:
                // 1 hour
                return 1 * 60 * 60 * 1000;
        }
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

    public void setNumAttendees(int numAttendees){

        switch (numAttendees){
            case 1:
                mRadioGroup.check(R.id.radioButton1);
                break;
            case 2:
                mRadioGroup.check(R.id.radioButton2);
                break;
            case 3:
                mRadioGroup.check(R.id.radioButton3);
                break;
            case 4:
                mRadioGroup.check(R.id.radioButton4);
                break;
            case 5:
                mRadioGroup.check(R.id.radioButton5);
                break;
        }
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

            int activity = CreateActivity.mCoWork.getActivityType();
            String description = CreateActivity.mCoWork.getDescription();
            int numAttendees = CreateActivity.mCoWork.getNumAttendees();
            String time = CreateActivity.mCoWork.getTime();
            String date = CreateActivity.mCoWork.getDate();

            mSpinnerActivity.setSelection(activity);
            mEditTextDescription.setText(description);
            setNumAttendees(numAttendees);
            mTextViewTime.setText(time);
            mTextViewDate.setText(updateDateField(date));

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
        void onActivitySet(int activityType);
        void onDescriptionSet(String description);
        void onNumAttendeesSet(int numAttendees);
        void onTimeSet(String time);
        void onDateSet(String date);
        void onDurationSet(long duration);
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
            String dateStr = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "";
            updateDateField(dateStr);
            mListener.onDateSet(dateStr);
        }
    }

    public String updateDateField(String dateStr) {

        try {
            SimpleDateFormat initial = new SimpleDateFormat(Constants.TimeAndDate.DATE_FORMAT, Locale.US);
            Date date = initial.parse(dateStr);
            SimpleDateFormat finalFormat = new SimpleDateFormat(Constants.TimeAndDate.DATE_FORMAT_FOR_DISPLAY, Locale.US);

            String finalDate = finalFormat.format(date);

            Log.d("MainActivity", "date: " + finalDate);
            mTextViewDate.setText(finalDate);
            return finalDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return dateStr;
        }
    }

    public void updateTimeField(Calendar calendar) {

        String time = (String) DateFormat.format(Constants.TimeAndDate.TIME_FORMAT, calendar.getTime());
        mTextViewTime.setText(time);
        mListener.onTimeSet(time);
    }
}
