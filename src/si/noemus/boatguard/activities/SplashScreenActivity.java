package si.noemus.boatguard.activities;

import java.net.URLEncoder;
import java.util.Locale;

import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.R;
import si.noemus.boatguard.R.layout;
import si.noemus.boatguard.R.string;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.DialogFactory;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {
 
    private static int SPLASH_TIME_OUT = 200;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        int lang = Utils.getPrefernciesInt(SplashScreenActivity.this, Settings.SETTING_LANG);
        if (lang == -1) {
        	String langDefault = Locale.getDefault().getLanguage();
        	if (langDefault.equals("ru") || (langDefault.equals("hr")) || (langDefault.equals("it"))){
        		Settings.setLanguage(SplashScreenActivity.this, langDefault);
            } else {
            	Settings.setLanguage(SplashScreenActivity.this, "en"); 
            };
        } else {
        	Settings.setLanguage(SplashScreenActivity.this, Settings.languages.get(lang));
        }
        

        
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

	    	   				if (mTelephonyMgr.getLine1Number().length() > 0) {
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
 
}