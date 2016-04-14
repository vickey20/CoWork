package com.vickey.cowork.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import com.vickey.cowork.CoWork;
import com.vickey.cowork.UserProfile;

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

public class CoworkIntentService extends IntentService {

    public static final String TAG = CoworkIntentService.class.getSimpleName();
    public static final String SEND_COWORK_TO_SERVER = "SEND_COWORK_TO_SERVER";
    public static final String COWORK = "cowork";
    public static final String RECEIVER = "receiver";
    public static final String ACTION = "ACTION";
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
            Log.d(TAG, "onHandleIntent: " + intent.getExtras().getString("vikram"));
            mReceiver = intent.getParcelableExtra("receiver");
            /* Service Started */
            mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);
            CoWork coWork = (CoWork) intent.getSerializableExtra(COWORK);
            sendCoworkToServer(coWork);
        }
    }

    private void sendCoworkToServer(CoWork coWork) {
        Gson gson = new Gson();

        String json = gson.toJson(coWork);
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://10.0.3.156:8080/SimpleWebServer/api/test");
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
            Log.i(TAG,jsonResponse);

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

        Log.d(TAG, gson.toJson(coWork));
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

    private void sendUserProfileToServer(UserProfile userProfile) {

    }
}
