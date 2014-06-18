package si.noemus.boatguard.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;


public class Settings {
	
	public static String SETTING_LANG = "LANG";
	public static String SETTING_USERNAME = "USERNAME";
	public static String SETTING_PASSWORD = "PASSWORD";
	public static String SETTING_REMEMBER_ME = "REMEMBER_ME";
	public static String SETTING_OBU_ID = "OBU_ID";

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
