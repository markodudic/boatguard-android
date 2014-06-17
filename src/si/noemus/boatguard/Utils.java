package si.noemus.boatguard;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public static final String PREFS_NAME = "si.noemus.boatguard.PREFS_FILE";

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


}
