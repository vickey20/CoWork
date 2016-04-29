package com.vickey.cowork.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;

import com.vickey.cowork.AddUserClass;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.LocationClass;
import com.vickey.cowork.R;
import com.vickey.cowork.UserProfile;
import com.vickey.cowork.UserProfileList;
import com.vickey.cowork.activity.DiscoverActivity;
import com.vickey.cowork.activity.HomeActivity;
import com.vickey.cowork.utilities.Constants;
import com.vickey.cowork.utilities.GeofenceErrorMessages;
import com.vickey.cowork.utilities.HelperClass;

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
import java.util.List;

public class CoworkIntentService extends IntentService {

    public static final String TAG = CoworkIntentService.class.getSimpleName();
    public static final String URL = "http://10.0.3.156:8080/CoWork/api/";
    public static final String COWORK = "cowork";
    public static final String USER = "user";
    public static final String GEOFENCE = "geofence";
    public static final String LOCATION = "location";
    public static final String COWORK_ID = "coworkID";
    public static final String USER_ID = "userID";
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

            if (mReceiver != null) {
            /* Service Started */
                mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);
            }
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

                    case Constants.Request.ADD_USER_AS_ATTENDEE:
                        int coworkID = intent.getIntExtra(COWORK_ID, -1);
                        String userID = intent.getStringExtra(USER_ID);
                        addUserAsAttendee(coworkID, userID);
                        break;
                }
            } else if(requestId == Constants.Request.USER_REQUEST) {

                switch (requestType) {
                    case Constants.Request.SEND_USER_PROFILE_TO_SERVER:
                    case Constants.Request.UPDATE_USER_PROFILE:
                        UserProfile userProfile = (UserProfile) intent.getSerializableExtra(USER);
                        sendUserProfileToServer(userProfile, requestType);
                        break;

                    case Constants.Request.GET_CORRESPONDING_USER_PROFILE_LIST:
                        ArrayList<CoWork> coWorkArrayList = (ArrayList<CoWork>) intent.getSerializableExtra(COWORK);
                        requestCorrespondingUserProfileList(coWorkArrayList);
                        break;
                }
            } else if (requestId == Constants.Request.GEOFENCE_REQUEST) {
                handleGeofenceEvent(intent);
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
            URL url = new URL(URL + "insertcowork");
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
            URL url = new URL(URL + "getnearbycoworks");
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

    private void addUserAsAttendee(int coworkID, String userID) {
        Gson gson = new Gson();

        AddUserClass addUserClass = new AddUserClass();
        addUserClass.setCoworkID(coworkID);
        addUserClass.setUserID(userID);

        String json = gson.toJson(addUserClass);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(URL + "adduserasattendee");
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

            Log.i(TAG,"response: " + jsonResponse);

            CoWork coWork = gson.fromJson(jsonResponse, CoWork.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable(RESULT, coWork);
            /* Status Finished */
            bundle.putInt(REQUEST_TYPE, Constants.Request.ADD_USER_AS_ATTENDEE);
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

    private void sendUserProfileToServer(UserProfile userProfile, int requestType) {
        Log.d(TAG, "sendUserProfileToServer: " + requestType);

        Gson gson = new Gson();

        String json = gson.toJson(userProfile);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = null;
            if(requestType == Constants.Request.SEND_USER_PROFILE_TO_SERVER) {
                url = new URL(URL + "insertuser");
            } else if(requestType == Constants.Request.UPDATE_USER_PROFILE) {
                url = new URL(URL + "updateuser");
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

    private void requestCorrespondingUserProfileList(ArrayList<CoWork> coWorks) {
        Gson gson = new Gson();

        ArrayList<UserProfileList> userProfileListArrayList = new ArrayList<>();

        for (CoWork coWork: coWorks) {
            UserProfileList userProfileList = new UserProfileList();
            userProfileList.setCoworkID(coWork.getCoworkID());
            userProfileList.setUserID(coWork.getCreatorID());

            userProfileListArrayList.add(userProfileList);
        }

        String json = gson.toJson(userProfileListArrayList);

        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(URL + "getcorrespondinguserprofilelist");
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
            Log.d(TAG, "requestCorrespondingUserProfileList: jsonResponse: " + jsonResponse);

            Gson gson1 = new Gson();
            UserProfile[] userProfiles = gson1.fromJson(jsonResponse, UserProfile[].class);

            //response data
            Log.i(TAG,"userProfiles[]: " + userProfiles[0].getUserId());

            ArrayList<UserProfile> userProfileArrayList = new ArrayList<UserProfile>(Arrays.asList(userProfiles));
            Bundle bundle = new Bundle();

            bundle.putSerializable(RESULT, userProfileArrayList);
            bundle.putInt(REQUEST_TYPE, Constants.Request.GET_CORRESPONDING_USER_PROFILE_LIST);
            mReceiver.send(STATUS_FINISHED, bundle);


        } catch (Exception e) {
            e.printStackTrace();

            Bundle bundle = new Bundle();
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

    private void handleGeofenceEvent(Intent intent) {

        CoWork coWork = (CoWork) intent.getSerializableExtra(COWORK);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            HelperClass.showNotification(getApplicationContext(), "Auto Check-in", geofenceTransitionDetails, R.drawable.discover_icon, (int) System.currentTimeMillis(), HomeActivity.class);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                    geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
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
