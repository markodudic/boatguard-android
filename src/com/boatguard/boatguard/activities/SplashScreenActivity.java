package com.boatguard.boatguard.activities;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.DialogFactory;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashScreenActivity extends Activity {
 
    private static int SPLASH_TIME_OUT = 200;
    private static String TAG = "SplashScreenActivity";
    
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    protected String SENDER_ID = "252096111121";
    private GoogleCloudMessaging gcm =null;
    private String regid = null;
    private Context context= null;
	public static HashMap<String,String> languages;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        if (checkPlayServices()) 
        {
               gcm = GoogleCloudMessaging.getInstance(this);
               regid = getRegistrationId(context);

               if (regid.isEmpty())
               {
                   registerInBackground();
               }
               else
               {
            	   Log.d(TAG, "Device's Registration ID is: "+regid);
               }
         }
        
        
    	languages = getLanguages(SplashScreenActivity.this);

		String lang = Utils.getPrefernciesString(SplashScreenActivity.this, Settings.SETTING_LANG);
        if (lang == null) {
        	String langDefault = Locale.getDefault().getLanguage();
        	if (langDefault.equals("sl") || (langDefault.equals("hr"))){
        		setLanguage(SplashScreenActivity.this, langDefault);
            } else {
            	setLanguage(SplashScreenActivity.this, "en"); 
            };
        } else {
        	setLanguage(SplashScreenActivity.this, lang);
        }
        

        new Thread(new Runnable() {
            public void run() {
            	 Settings.readContacts(SplashScreenActivity.this);
            }
        }).start();
        
        
        new Handler().postDelayed(new Runnable() {
 
	            @Override
	            public void run() {
	    	   		String username = Utils.getPrefernciesString(SplashScreenActivity.this, Settings.SETTING_USERNAME);
	    	   		String password = Utils.getPrefernciesString(SplashScreenActivity.this, Settings.SETTING_PASSWORD);
	    	   		String obu_id = Utils.getPrefernciesString(SplashScreenActivity.this, Settings.SETTING_OBU_ID);
	    	   		boolean remember = Utils.getPrefernciesBoolean(SplashScreenActivity.this, Settings.SETTING_REMEMBER_ME, false);
	    	   		if (!remember) {
	    	   			Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
	    	   			startActivity(i);						
	    	   			finish();
	    	   		} else {
	    	   			try {
	    	   				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		    	   		    TelephonyManager mTelephonyMgr;
			    	   	    mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
	    	   				String urlString = SplashScreenActivity.this.getString(R.string.server_url) + 
	    	   						"login?type=login" + 
	    	   						"&username=" + username + 
	    	   						"&password=" + password +
	    	   						"&obu_sn=" + obu_id +
									"&app_version=" + URLEncoder.encode(pInfo.versionName) +
									"&device_name="+URLEncoder.encode(Build.MODEL)+
									"&device_platform="+Build.VERSION.SDK_INT+
									"&device_version="+URLEncoder.encode(Build.VERSION.RELEASE)+
									"&device_uuid="+URLEncoder.encode(Build.SERIAL);

	    	   				if (mTelephonyMgr!=null && mTelephonyMgr.getLine1Number()!=null && mTelephonyMgr.getLine1Number().length() > 0) {
	    	   					urlString += "&phone_number="+URLEncoder.encode(mTelephonyMgr.getLine1Number());
	    	   				}        
	    	   	        
	    	   		        if (Utils.isNetworkConnected(SplashScreenActivity.this, true)) {
	    	   		        	AsyncTask at = new Comm().execute(urlString, null); 
		    	   	            String res = (String) at.get();
		    	   	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
		    	   	    	   	if (jRes.has("error") && !jRes.getString("error").equals("null")) {
		    	   	    	   		String msg = ((JSONObject)jRes.get("error")).getString("msg");
		    	   	    	   		String name = ((JSONObject)jRes.get("error")).getString("name");
		    	   	    	   		DialogFactory.getInstance().displayWarning(SplashScreenActivity.this, name, msg, false);
		    	   	    	   	} else {
		    	   	    	   		Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
		    	   					startActivity(i);
		    	   					finish();
		    	   	    	   	}
	    	   		        }
	    	   	        } catch (Exception e) {
	    	   	        	e.printStackTrace();
	    	   	        	Toast toast = Toast.makeText(SplashScreenActivity.this, getString(R.string.json_error), Toast.LENGTH_LONG);
	    	   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
	    	   	        	toast.show();
	    	   	   		}	    	   			
	    	   		}
	            }
        }, SPLASH_TIME_OUT);
    }

    @Override 
    protected void onResume() {
           super.onResume();
           checkPlayServices();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported - Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    
    private String getRegistrationId(Context context) 
    {
       String registrationId = Utils.getPrefernciesString(SplashScreenActivity.this, PROPERTY_REG_ID);
       if (registrationId == null) {
           Log.d(TAG, "Registration ID not found.");
           return "";
       }
       int registeredVersion = Utils.getPrefernciesInt(SplashScreenActivity.this, PROPERTY_APP_VERSION);
       int currentVersion = getAppVersion(SplashScreenActivity.this);
       if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

   private static int getAppVersion(Context context) 
    {
	   try 
	   {
		   PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		   return packageInfo.versionCode;
      } 
      catch (NameNotFoundException e) 
      {
            throw new RuntimeException("Could not get package name: " + e);
      }
    }


    @SuppressWarnings("unchecked")
	private void registerInBackground() 
    {     
    	new AsyncTask() {
    		@Override
    		protected Object doInBackground(Object... params) 
			 {
			      String msg = "";
				  try 
			      {
			           if (gcm == null) 
			           {
			                    gcm = GoogleCloudMessaging.getInstance(context);
			           }
			           regid = gcm.register(SENDER_ID);      
			           Utils.savePrefernciesString(SplashScreenActivity.this, PROPERTY_REG_ID, regid);
			           int appVersion = getAppVersion(SplashScreenActivity.this);
			           Utils.savePrefernciesInt(SplashScreenActivity.this, PROPERTY_APP_VERSION, appVersion);
			           
			           Log.d(TAG, "Current Device's Registration ID is: "+regid);     
			      } 
			      catch (IOException ex) 
			      {
			          Log.d(TAG, "Error :" + ex.getMessage());     
			      }
			      return null;
			 }     
			 
    		@Override
    		protected void onPostExecute(Object result) 
			 { 
		           Log.d(TAG, "onPostExecute");     
			 }
    	}.execute(null, null, null);
    }
    
	public static HashMap<String,String> getLanguages(final Context context) {
    	return new HashMap<String,String>() {{
		    put("en", context.getResources().getString(R.string.language_en));
		    put("sl", context.getResources().getString(R.string.language_slo));
		    put("hr", context.getResources().getString(R.string.language_hr));
		}};
	}	
	
	public static void setLanguage(Context context, String lang) {
    	Locale locale = new Locale(lang);
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    context.getResources().updateConfiguration(config, null);
	    Utils.savePrefernciesString(context, Settings.SETTING_LANG, lang);
    }	
    
    
}