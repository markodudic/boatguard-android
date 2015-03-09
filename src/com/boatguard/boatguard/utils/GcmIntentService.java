package com.boatguard.boatguard.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.Alarm;

public class GcmIntentService extends IntentService {
    
	private final static String TAG = "GcmIntentService";     
	
	public GcmIntentService() {
	     super("GcmIntentService");     
	}     
	
	@Override
     protected void onHandleIntent(Intent intent) {
          Bundle extras = intent.getExtras();
          Log.d(TAG, "Notification Data :" + extras.getString("title"));
          Log.d(TAG, "Notification Data :" + extras.getString("message"));
          Log.d(TAG, "Notification Data :" + extras.getString("date"));

          Timestamp timestamp = null;
          try{
        	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        	    Date parsedDate = dateFormat.parse(extras.getString("date"));
        	    timestamp = new java.sql.Timestamp(parsedDate.getTime());
          }catch(Exception e){}
          
          int alarmId = Integer.parseInt(extras.getString("alarmid"));
          GcmBroadcastReceiver.completeWakefulIntent(intent);
          
          //if (MainActivity.activeAlarms.indexOf(alarmId) == -1) {
        	  showNotification(alarmId, extras.getString("title"), extras.getString("message"), timestamp, Integer.parseInt(extras.getString("vibrate")), Integer.parseInt(extras.getString("sound")));
        	  this.sendBroadcast(new Intent("GCMMessageReceived"));
        	//  MainActivity.activeAlarms.add(alarmId);
  		  //}
          
     }   
	
	private void showNotification(int id, String title, String message, Timestamp date, int vibrate, int sound){
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("alarmId", id);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.notification);
		if (BoatguardLifecycleHandler.isApplicationRunning()) {
			String uri = "drawable/"+ ((Alarm)Settings.alarms.get(id)).getIcon(); 
			int imageResource = getResources().getIdentifier(uri, null, getPackageName());
			bm = BitmapFactory.decodeResource(getResources(), imageResource);
		}
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_notification)
		        .setLargeIcon(bm)
		        .setColor(getResources().getColor(R.color.background_night))
		        .setContentTitle(title)
		        .setContentText(message)
		        .setAutoCancel(true)
		        .setContentIntent(pIntent)
		        .setWhen(date.getTime());
		
		NotificationManager mNotificationManager =  (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.build());
		
		beepVibrate(vibrate, sound);
	}	
	
	private void beepVibrate(int vibrate, int sound) {
		try {
	        if (sound == 1 && (Utils.getPrefernciesBoolean(this, Settings.SETTING_PLAY_SOUND, false))) {
		    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
	        }
	    } catch (Exception e) {}
	    if (vibrate == 1 && (Utils.getPrefernciesBoolean(this, Settings.SETTING_VIBRATE, false))) {
	    	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
	    }
	}	
}

