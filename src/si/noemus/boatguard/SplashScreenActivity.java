package si.noemus.boatguard;

import java.util.Locale;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
					Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
					startActivity(i);						
					finish();
	            }
        }, SPLASH_TIME_OUT);
    }
 
}