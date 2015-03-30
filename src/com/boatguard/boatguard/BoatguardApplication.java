package com.boatguard.boatguard;

import android.app.Application;

import com.boatguard.boatguard.utils.BoatguardLifecycleHandler;
import com.flurry.android.FlurryAgent;

public class BoatguardApplication extends Application
{
    @Override
    public void onCreate()
    {
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, getResources().getString(R.string.MY_FLURRY_APIKEY));

        registerActivityLifecycleCallbacks(new BoatguardLifecycleHandler());
    	
    	// begin add
        try {
            Class.forName("com.boatguard.boatguard.activities.LoginActivity");
        } catch(Throwable ignore) {
        	System.out.println("**************************");
        }
        // end add

        super.onCreate();
    }
}