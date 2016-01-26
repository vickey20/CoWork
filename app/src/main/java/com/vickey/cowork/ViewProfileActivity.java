package com.vickey.cowork;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.vickey.cowork.utilities.Constants;

public class ViewProfileActivity extends AppCompatActivity {

    SharedPreferences mSharedPref;

    ImageView mImageViewPhotoEdit;
    EditText mEditTextName, mEditTextEmail, mEditTextProfession, mEditTextAge;
    RadioGroup mRadioGroup;
    int test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mImageViewPhotoEdit = (ImageView) findViewById(R.id.imageViewProfilePhotoEdit);
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextProfession = (EditText) findViewById(R.id.editTextProfession);
        mEditTextAge = (EditText) findViewById(R.id.editTextAge);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupGender);

        mSharedPref = getSharedPreferences(getString(R.string.login_shared_pref), Context.MODE_PRIVATE);

        mImageViewPhotoEdit.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String name = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_NAME, "");
        if(!name.equals("")){
            mEditTextName.setText(name);
        }

        String email = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_EMAIL, "");
        if(!email.equals("")){
            mEditTextEmail.setText(email);
        }

        String profession = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_PROFESSION, "");
        if(!profession.equals("")){
            mEditTextProfession.setText(profession);
        }

        int age = mSharedPref.getInt(Constants.PreferenceKeys.KEY_USER_AGE, -1);
        if(age != -1 ){
            mEditTextAge.setText(String.valueOf(age));
        }
        else{
            mEditTextAge.setHint(String.valueOf(18));
        }

        String gender = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_GENDER, "");
        if(gender.equals("male")){
            mRadioGroup.check(R.id.radioButtonMale);
        }
        else if(gender.equals("female")){
            mRadioGroup.check(R.id.radioButtonFemale);
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonMale) {
                } else if (checkedId == R.id.radioButtonFemale) {
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(Constants.PreferenceKeys.KEY_USER_NAME, mEditTextName.getText().toString());
        editor.putString(Constants.PreferenceKeys.KEY_USER_EMAIL, mEditTextEmail.getText().toString());
        editor.putString(Constants.PreferenceKeys.KEY_USER_PROFESSION, mEditTextProfession.getText().toString());
        editor.putInt(Constants.PreferenceKeys.KEY_USER_AGE, Integer.parseInt(mEditTextAge.getText().toString()));
        editor.putString(Constants.PreferenceKeys.KEY_USER_GENDER, (mRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonMale? "male":"female"));
        editor.commit();
    }
}
