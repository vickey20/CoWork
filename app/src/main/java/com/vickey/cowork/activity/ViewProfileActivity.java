package com.vickey.cowork.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.vickey.cowork.R;
import com.vickey.cowork.UserProfile;
import com.vickey.cowork.receiver.IntentServiceReceiver;
import com.vickey.cowork.service.CoworkIntentService;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.ImagePicker;
import com.vickey.cowork.utilities.RoundImageView;

import java.io.File;

public class ViewProfileActivity extends AppCompatActivity implements IntentServiceReceiver.Receiver {

    private final String TAG = ViewProfileActivity.class.getSimpleName();

    SharedPreferences mSharedPref;
    RoundImageView mImageViewPhoto;
    ImageView mImageViewPhotoEdit;
    EditText mEditTextName, mEditTextEmail, mEditTextProfession, mEditTextAge;
    RadioGroup mRadioGroup;
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mImageViewPhoto = (RoundImageView) findViewById(R.id.imageViewProfilePhoto);
        mImageViewPhotoEdit = (ImageView) findViewById(R.id.imageViewProfilePhotoEdit);
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextProfession = (EditText) findViewById(R.id.editTextProfession);
        mEditTextAge = (EditText) findViewById(R.id.editTextAge);

        mEditTextAge.setKeyListener(null);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupGender);

        mSharedPref = getSharedPreferences(getString(R.string.login_shared_pref), Context.MODE_PRIVATE);

        mImageViewPhotoEdit.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ViewProfileActivity.this);
                startActivityForResult(chooseImageIntent, 1);
            }
        });

        String name = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_NAME, "");
        if(!name.equals("")){
            mEditTextName.setText(name);
        }

        Log.d(TAG, "name: " + name);

        String email = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_EMAIL, "");
        if(!email.equals("")){
            mEditTextEmail.setText(email);
        }

        String profession = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_PROFESSION, "");
        if(!profession.equals("")){
            mEditTextProfession.setText(profession);
        }

        int age = mSharedPref.getInt(Constants.PreferenceKeys.KEY_USER_AGE, -1);
        if(age != -1){
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
    protected void onResume() {
        super.onResume();
        setProfilePhoto();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(Constants.PreferenceKeys.KEY_USER_NAME, mEditTextName.getText().toString());
        editor.putString(Constants.PreferenceKeys.KEY_USER_EMAIL, mEditTextEmail.getText().toString());
        editor.putString(Constants.PreferenceKeys.KEY_USER_PROFESSION, mEditTextProfession.getText().toString());
        String age = mEditTextAge.getText().toString();
        if(age == null || age.equals("")){
            age = "18";
        }
        editor.putInt(Constants.PreferenceKeys.KEY_USER_AGE, Integer.parseInt(age));
        editor.putString(Constants.PreferenceKeys.KEY_USER_GENDER, (mRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonMale? "male":"female"));
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                // TODO use bitmap
                mImageViewPhoto.setImageBitmap(bitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                setProfilePhoto();
                break;
        }
    }

    public void setProfilePhoto(){
        File imgFile = new  File(String.valueOf(ImagePicker.getImagePath(getApplicationContext())));
        if(imgFile.exists()){
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mImageViewPhoto.setImageBitmap(myBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ViewProfileActivity.this, "Could not get profile picture", Toast.LENGTH_LONG).show();
                mImageViewPhoto.setBackgroundResource(R.drawable.profile);
            }
        }
        else{
            mImageViewPhoto.setBackgroundResource(R.drawable.profile);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        userProfile = new UserProfile();
        userProfile.setName(mEditTextName.getText().toString());
        userProfile.setEmail(mEditTextEmail.getText().toString());
        userProfile.setUserId(mEditTextEmail.getText().toString());
        userProfile.setPassword(mSharedPref.getString(Constants.PreferenceKeys.KEY_LOGIN_PASSWORD, ""));
        userProfile.setProfession(mEditTextProfession.getText().toString());
        userProfile.setGender((mRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonMale? "male":"female"));
        userProfile.setLoginType(mSharedPref.getInt(Constants.PreferenceKeys.KEY_USER_LOGIN_TYPE, Constants.LoginType.LOGIN_TYPE_NEW_REGISTER));
        String bday = mSharedPref.getString(Constants.PreferenceKeys.KEY_USER_BIRTHDAY, "");
        userProfile.setBirthday(bday);
        sendProfileToServer(userProfile);
    }

    private void sendProfileToServer(UserProfile userProfile) {
        /* Starting Download Service */
        IntentServiceReceiver receiver = new IntentServiceReceiver(new Handler());
        receiver.setReceiver(ViewProfileActivity.this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, CoworkIntentService.class);

            /* Send optional extras to Download IntentService */
        intent.putExtra(CoworkIntentService.USER, userProfile);
        intent.putExtra(CoworkIntentService.RECEIVER, receiver);
        intent.putExtra(CoworkIntentService.REQUEST_ID, Constants.Request.USER_REQUEST);
        intent.putExtra(CoworkIntentService.REQUEST_TYPE, Constants.Request.UPDATE_USER_PROFILE);

        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "onReceiveResult:: resultCode: " + resultCode + "; resultData: " + resultData);

        switch (resultCode) {
            case CoworkIntentService.STATUS_RUNNING:

                break;

            case CoworkIntentService.STATUS_FINISHED:
                Toast.makeText(ViewProfileActivity.this, "Profile saved successfully!", Toast.LENGTH_LONG).show();
                break;

            case CoworkIntentService.STATUS_ERROR:
                Toast.makeText(ViewProfileActivity.this, "Error saving profile. Please try again...", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
