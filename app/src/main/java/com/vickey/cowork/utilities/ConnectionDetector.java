package com.vickey.cowork.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
public class ConnectionDetector {
 
    public Context _context;
 
    public ConnectionDetector(Context context){
        this._context = context;
    }
 
    /**
     * Checking for all possible internet providers
     * **/
    public static boolean isConnectedToInternet( Context context ){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public static boolean isWifiConnected(Context context) {
    	 ConnectivityManager connectivityManager = 
    		 				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	 
    	 NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	 
    	 if ( wifiInfo.isConnected() ) {
    		 return true;
    	 }
    	 return false;
	}
    
    public static boolean isMobileDataConnected(Context context) {
   	 ConnectivityManager connectivityManager = 
   		 				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
   	 
   	 NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
   	 
   	 if ( mobileInfo.isConnected() ) {
   		 return true;
   	 }
   	 return false;
	}
    
    public static boolean isRoaming(Context context) {
      	 ConnectivityManager connectivityManager = 
      		 				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      	 
      	 NetworkInfo roaming = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      	 
      	 if ( roaming.isRoaming() ) {
      		 return true;
      	 }
      	 return false;
   	}
}
