package com.boatguard.boatguard;

import android.app.Application;

import com.boatguard.boatguard.utils.BoatguardLifecycleHandler;

public class BoatguardApplication extends Application
{
    @Override
    public void onCreate()
    {
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