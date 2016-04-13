package com.vickey.cowork.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.vickey.cowork.R;
import com.vickey.cowork.activity.HomeActivity;
import com.vickey.cowork.service.CoworkService;

import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * Created by vikramgupta on 3/27/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    public static final String COWORK_ID = "COWORK_ID";
    public Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        PowerManager pm = (PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        //Acquire the lock
        wl.acquire();

        Bundle extras = intent.getExtras();
        int coworkId = extras.getInt(COWORK_ID, -1);
        Toast.makeText(mContext, "Alarm fired!!! Cowork: " + coworkId, Toast.LENGTH_LONG).show();

        Log.d(TAG, "AlarmReceiver.onReceive(): coworkID:: " + coworkId);

        Intent serviceIntent = new Intent(mContext, CoworkService.class);
        mContext.startService(serviceIntent);

        HelperClass.showNotification(context, "Auto Checkin", "New feature in the making!", R.drawable.discover_icon, 1, HomeActivity.class);

        wl.release();
    }

}
