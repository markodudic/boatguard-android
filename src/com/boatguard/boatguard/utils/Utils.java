package com.boatguard.boatguard.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.boatguard.boatguard.R;

public class Utils {
    public static final String PREFS_NAME = "com.boatguard.boatguard.PREFS_FILE";

    public static void savePrefernciesString(Context context, String paramString1, String paramString2)
    {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        localEditor.putString(paramString1, paramString2);
        localEditor.commit();
    }

    public static String getPrefernciesString(Context context, String paramString)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(paramString, null);
    }

    public static void savePrefernciesInt(Context context, String paramString1, int paramString2)
    {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        localEditor.putInt(paramString1, paramString2);
        localEditor.commit();
    }

    public static int getPrefernciesInt(Context context, String paramString)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getInt(paramString, -1);
    }

    public static void savePrefernciesLong(Context context, String paramString1, long paramString2)
    {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        localEditor.putLong(paramString1, paramString2);
        localEditor.commit();
    }

    public static long getPrefernciesLong(Context context, String paramString)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getLong(paramString, -1);
    }
    
    public static void savePrefernciesBoolean(Context context, String paramString1, boolean paramString2)
    {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        localEditor.putBoolean(paramString1, paramString2);
        localEditor.commit();
    }
 
    public static boolean getPrefernciesBoolean(Context context, String paramString, boolean def)
    {
        return context.getSharedPreferences(PREFS_NAME, 0).getBoolean(paramString, def);
    }

	
    public static boolean isNetworkConnected(Context context, boolean showDialog) {
	  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	  NetworkInfo ni = cm.getActiveNetworkInfo();
	  if (ni == null) {
	   	  if (showDialog) {
	   		  DialogFactory.getInstance().displayWarning(context, context.getResources().getString(R.string.no_active_connection_title), context.getResources().getString(R.string.no_active_connection_msg), false);
	   	  }
   		  return false;
	  } else
		  return true;
	}
    
    public static int dpToPx(Context context, int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }    
    
    
    public static String formatDate(String dateToConvert)
    {
    	Date d = new Date(dateToConvert);
    	DateFormat df = new SimpleDateFormat("MMM/dd/yyyy HH:mm");
		return df.format(d).toUpperCase();
    }
 
    public static String formatDateShort(String dateToConvert)
    {
    	Date d = new Date(dateToConvert);
    	DateFormat df = new SimpleDateFormat("MM/dd");
		return df.format(d).toUpperCase();
    }    

    public static int getDateDif(String dateFrom, String dateTo)
    {
    	Date d1 = new Date(dateFrom);
    	Date d2 = new Date(dateTo);
    	long diffInMs = (d2.getTime() - d1.getTime());
        long diffInMins = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
    	DateFormat df = new SimpleDateFormat("dd");
    	if (!df.format(d1).equals(df.format(d2))) {
			diffInMins = 0;
		}
        return (int)diffInMins;
    }     
}



