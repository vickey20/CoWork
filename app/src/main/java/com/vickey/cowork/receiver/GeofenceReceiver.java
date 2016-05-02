package com.vickey.cowork.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.vickey.cowork.CoWork;
import com.vickey.cowork.R;
import com.vickey.cowork.activity.HomeActivity;
import com.vickey.cowork.service.CoworkIntentService;
import com.vickey.cowork.utilities.GeofenceErrorMessages;
import com.vickey.cowork.utilities.HelperClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikramgupta on 5/1/16.
 */
public class GeofenceReceiver extends BroadcastReceiver {
    public static final String TAG = GeofenceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        handleGeofenceEvent(intent, context);
    }

    private void handleGeofenceEvent(Intent intent, Context context) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.e(TAG, "geofenceTransition: " + geofenceTransition);

        if (geofenceTransition == -1) {
            String error = context.getString(R.string.geofence_transition_invalid_type,
                    geofenceTransition);
            HelperClass.showNotification(context, "Auto Check-in", "Error: " + error, R.drawable.discover_icon, (int) System.currentTimeMillis(), HomeActivity.class);
            // Log the error.
            Log.e(TAG, error);
        } else {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    context,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                HelperClass.showNotification(context, "Auto Check-in", geofenceTransitionDetails, R.drawable.discover_icon, (int) System.currentTimeMillis(), HomeActivity.class);
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                HelperClass.showNotification(context, "Auto Check-in", geofenceTransitionDetails, R.drawable.discover_icon, (int) System.currentTimeMillis(), HomeActivity.class);
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                HelperClass.showNotification(context, "Checked-in", geofenceTransitionDetails, R.drawable.discover_icon, (int) System.currentTimeMillis(), HomeActivity.class);
            }
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context             The app context.
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition, context);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType, Context context) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.unknown_geofence_transition);
        }
    }
}
