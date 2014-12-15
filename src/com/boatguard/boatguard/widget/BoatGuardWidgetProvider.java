package com.boatguard.boatguard.widget;


import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.gson.Gson;

/**
 * Our data observer just notifies an update for all BoatGuard widgets when it detects a change.
 */
class BoatGuardDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;
        
    
    BoatGuardDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {	
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.components);
    }
}

/**
 * The BoatGuard widget's AppWidgetProvider.
 */
public class BoatGuardWidgetProvider extends AppWidgetProvider {
	public static final String TAG = "BoatGuardWidgetProvider";
	
	public static final String REFRESH_ACTION = "com.boatguard.boatguard.widget.REFRESH";
	public static final String REFRESH_OPEN_ACTION = "com.boatguard.boatguard.widget.REFRESH_OPEN";
	
    public static RemoteViews rv = null;
	private Context context;
	public static LinkedHashMap<Integer,ObuState> obuStates = new LinkedHashMap<Integer,ObuState>(){};
	//public static HashMap<Integer,RemoteViews> obuRemoteViews = new HashMap<Integer,RemoteViews>(){};
	private static Gson gson = new Gson();	
	//private boolean isAccuConnected = false;
	//private int accuStep = 0;	
	
    public BoatGuardWidgetProvider() {
    }


    @Override
    public void onEnabled(Context context) {
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG,"ACTION="+action);
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, BoatGuardWidgetProvider.class);

        if (action.equals(REFRESH_ACTION)) {
        	onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
        } 
        else if (action.equals(REFRESH_OPEN_ACTION)) {
        	onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
        	final Intent intent1 = new Intent(context, MainActivity.class);
        	intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(intent1);
        } 
        else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
        } 
        
        try {
            super.onReceive(context, intent);
        } catch (Exception e) {};
    }

    @SuppressWarnings("deprecation")
	private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
        Log.d(TAG, "buildLayout1="+largeLayout+":"+appWidgetId);
        this.context = context;
        rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        
        getObudata();
        
    	Intent intent = new Intent(context, BoatGuardWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.components, intent);
        
        if (obuStates.size() > 0) {
        	ObuState obuState = obuStates.get(((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId());
        	rv.setTextViewText(R.id.tv_last_update, Utils.formatDate(obuState.getDateState()));
        }
        
        //showObuComponents();
        //getObudata();
    	//showObuData();  
        
        
        final Intent refreshIntent = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent.setAction(BoatGuardWidgetProvider.REFRESH_ACTION);
        final PendingIntent pending = PendingIntent.getBroadcast(context, 0,  refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pending);
        //dam osvezevanje kar fiksno na 30 min
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+(30*1000), pending);
        //alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+Settings.OBU_REFRESH_TIME, pending);
        
        // accu step
        /*final Intent refreshIntent = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent.setAction(BoatGuardWidgetProvider.REFRESH_ACTION);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,  refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.lAccu, refreshPendingIntent);
        */
        
        // open app
        /*final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, BoatGuardWidgetProvider.class);
        final Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent rpIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.lWidget, rpIntent);
        mgr.updateAppWidget(mgr.getAppWidgetIds(cn), rv);
*/
        //open app and refresh
        final Intent refreshIntent1 = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent1.setAction(BoatGuardWidgetProvider.REFRESH_OPEN_ACTION);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,  refreshIntent1, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.lWidget, refreshPendingIntent);
        
        return rv;
    }

    
	public void getObudata() {
		String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getdata?obuid="+obuId;
    	System.out.println("gggurlString="+urlString);
		
    	if (Utils.isNetworkConnected(context, false)) {
  			try {
  		       HttpClient httpClient = new DefaultHttpClient();
  		       HttpContext localContext = new BasicHttpContext();
  		       HttpPost httpPost = new HttpPost(urlString);
  		       String text = null;
	    	   HttpResponse response = httpClient.execute(httpPost, localContext);
	    	   HttpEntity entity = response.getEntity();
	    	   text = getASCIIContentFromEntity(entity);

			   JSONObject jRes = (JSONObject)new JSONTokener(text).nextValue();
	    	   JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   obuStates.clear();
		   	   for (int i=0; i< jsonStates.length(); i++) {
		   		   ObuState obuState = gson.fromJson(jsonStates.get(i).toString(), ObuState.class);
		   		   obuStates.put(obuState.getId_state(), obuState);
		   	   }

	        } catch (Exception e) {
	        	e.printStackTrace();
	        	e.getLocalizedMessage();
   	   		}	        	
			
    	}
    }
	
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();

		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) {
			byte[] b = new byte[4096];
			n =  in.read(b);
	
			if (n>0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}	

	
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(context, appWidgetIds[i], false);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.components);            
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
    }

}