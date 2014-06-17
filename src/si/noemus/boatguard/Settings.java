package si.noemus.boatguard;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.text.Html;
import android.widget.Toast;


public class Settings {
	
	public static String SETTING_LANG = "LANG";

	public static HashMap<Integer,String> languages = new HashMap<Integer,String>() {{
																					    put(0, "en");
																					    put(1, "ru");
																					    put(2, "hr");
																					    put(3, "it");
																					}};
    public static void setLanguage(Context context, String lang) {
    	System.out.println("LANG="+lang);
        Locale locale = new Locale(lang);
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    context.getResources().updateConfiguration(config, null);
	    
	    for (Entry<Integer, String> entry : languages.entrySet()) {
	        if (entry.getValue().equals(lang)) {
	        	Utils.savePrefernciesInt(context, Settings.SETTING_LANG, entry.getKey());
	        }
    	}

    }	
}
