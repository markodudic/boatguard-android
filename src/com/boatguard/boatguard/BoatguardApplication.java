package com.boatguard.boatguard;

import android.app.Application;

public class BoatguardApplication extends Application
{
    @Override
    public void onCreate()
    {
        // begin add
        try {
            Class.forName("com.boatguard.boatguard.activities.LocationActivity");
        } catch(Throwable ignore) {
        	System.out.println("**************************");
        }
        // end add

        super.onCreate();
    }
}