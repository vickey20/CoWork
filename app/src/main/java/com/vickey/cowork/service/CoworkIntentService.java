package com.vickey.cowork.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.LocationClass;
import com.vickey.cowork.LocationClass;
import com.vickey.cowork.UserProfile;
import com.vickey.cowork.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class CoworkIntentService extends IntentService {

    public static final String TAG = CoworkIntentService.class.getSimpleName();
    public static final String COWORK = "cowork";
    public static final String USER = "USER";
    public static final String LOCATION = "LOCATION";
    public static final String RECEIVER = "receiver";
    public static final String REQUEST_ID = "requestID";
    public static final String REQUEST_TYPE = "requestType";
    public static final String ACTION = "ACTION";
    public static final String RESULT = "RESULT";
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_ERROR = -1;

    private ResultReceiver mReceiver;

    public CoworkIntentService() {
        super("CoworkIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: " + intent);
        if (intent != null) {
            mReceiver = intent.getParcelableExtra(RECEIVER);
            /* Service Started */
            mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);

            int requestId = intent.getIntExtra(REQUEST_ID, 0);
            int requestType = intent.getIntExtra(REQUEST_TYPE, 0);

            if(requestId == Constants.Request.COWORK_REQUEST) {
                CoWork coWork = (CoWork) intent.getSerializableExtra(COWORK);
                switch (requestType) {
                    case Constants.Request.SEND_COWORK_TO_SERVER:
                        sendCoworkToServer(coWork);
                        break;

                    case Constants.Request.GET_NEARBY_COWORKS_FROM_SERVER:
                        LocationClass location = (LocationClass) intent.getSerializableExtra(LOCATION);
                        getNearbyCoworks(location);
                        break;
                }
            } else if(requestId == Constants.Request.USER_REQUEST) {
                UserProfile userProfile = (UserProfile) intent.getSerializableExtra(USER);
                handleUserRequest(userProfile, requestType);
            }
        }
    }

    private void sendCoworkToServer(CoWork coWork) {
        Gson gson = new Gson();

        String json = gson.toJson(coWork);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://192.168.1.252:8080/CoWork/api/insertcowork");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //set headers and method
            Writer out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            out.write(json);
            out.close();

            InputStream inputStream = urlConnection.getInputStream();

            //input stream
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            inputLine = reader.readLine();

            //response data
            Log.i(TAG,"JSON response: " + inputLine);
            Bundle bundle = new Bundle();
            /* Status Finished */
            bundle.putInt(RESULT, Integer.parseInt(inputLine));
            bundle.putInt(REQUEST_TYPE, Constants.Request.SEND_COWORK_TO_SERVER);
            mReceiver.send(STATUS_FINISHED, bundle);

        } catch (IOException e) {
            e.printStackTrace();

            Bundle bundle = new Bundle();
            /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, "Error message here..");
            mReceiver.send(STATUS_ERROR, bundle);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(TAG, "sendCoworkToServer json: " + gson.toJson(coWork));
    }

    private void getNearbyCoworks(LocationClass location) {
        Gson gson = new Gson();

        String json = gson.toJson(location);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://192.168.1.252:8080/CoWork/api/getnearbycoworks");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //set headers and method
            Writer out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            out.write(json);
            out.close();

            InputStream inputStream = urlConnection.getInputStream();

            //input stream
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;

            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return;
            }

            jsonResponse = buffer.toString();

            Gson gson1 = new Gson();
            CoWork[] coWorks = gson1.fromJson(jsonResponse, CoWork[].class);

            //response data
            Log.i(TAG,"coworks[]: " + coWorks[0].getCoworkID());

            ArrayList<CoWork> coWorkArrayList = new ArrayList<CoWork>(Arrays.asList(coWorks));
            Bundle bundle = new Bundle();
            /* Status Finished */
            bundle.putSerializable(RESULT, coWorkArrayList);
            bundle.putInt(REQUEST_TYPE, Constants.Request.GET_NEARBY_COWORKS_FROM_SERVER);
            mReceiver.send(STATUS_FINISHED, bundle);


        } catch (Exception e) {
            e.printStackTrace();

            Bundle bundle = new Bundle();
            /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, "Error message here..");
            mReceiver.send(STATUS_ERROR, bundle);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    private void handleUserRequest(UserProfile userProfile, int requestType) {
        Log.d(TAG, "handleUserRequest: " + requestType);

        Gson gson = new Gson();

        String json = gson.toJson(userProfile);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = null;
            if(requestType == Constants.Request.SEND_USER_PROFILE_TO_SERVER) {
                url = new URL("http://192.168.1.252:8080/CoWork/api/insertuser");
            } else if(requestType == Constants.Request.UPDATE_USER_PROFILE) {
                url = new URL("http://192.168.1.252:8080/CoWork/api/updateuser");
            }
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //set headers and method
            Writer out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            out.write(json);
            out.close();

            InputStream inputStream = urlConnection.getInputStream();

            //input stream
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return;
            }

            jsonResponse = buffer.toString();

            //response data
            Log.i(TAG,"JSON response: " + jsonResponse);

            Bundle bundle = new Bundle();
            /* Status Finished */
            bundle.putString("result", jsonResponse);
            mReceiver.send(STATUS_FINISHED, bundle);

        } catch (IOException e) {
            e.printStackTrace();

            Bundle bundle = new Bundle();
            /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, "Error message here..");
            mReceiver.send(STATUS_ERROR, bundle);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(TAG, "sendUserProfileToServer json: " + gson.toJson(userProfile));
    }


    private ArrayList<CoWork> getCoworkListFromServer(int userId) {

        return null;
    }

    private UserProfile getUserProfileFromServer(int userId) {

        return null;
    }

    private CoWork getCoworkFromServer(int coworkId) {

        return null;
    }

}
