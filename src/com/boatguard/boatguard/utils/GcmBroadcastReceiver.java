package com.boatguard.boatguard.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    
     
	protected String getGCMIntentServiceClassName(Context context)
    {
        return GcmIntentService.class.getName(); 
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		 ComponentName comp = new ComponentName(context.getPackageName(),  GcmIntentService.class.getName());        
		 startWakefulService(context, (intent.setComponent(comp)));
	     setResultCode(Activity.RESULT_OK);
	}
}