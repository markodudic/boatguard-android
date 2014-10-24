package com.boatguard.boatguard.widget;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.Comm.OnTaskCompleteListener;
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
        // The data has changed, so notify the widget that the collection view needs to be updated.
        // In response, the factory's onDataSetChanged() will be called which will requery the
        // cursor for the new data.
    	System.out.println("BoatGuardDataProviderObserver");
		
    }
}

/**
 * The BoatGuard widget's AppWidgetProvider.
 */
public class BoatGuardWidgetProvider extends AppWidgetProvider {
	public static final String TAG = "BoatGuardWidgetProvider";
	
	public static final String REFRESH_ACTION = "com.boatguard.boatguard.widget.REFRESH";
	
    private Handler handler = new Handler();
    private RemoteViews rv = null;
	private Context context;
	public static HashMap<Integer,ObuState> obuStates = new HashMap<Integer,ObuState>(){};
	public static HashMap<Integer,RemoteViews> obuRemoteViews = new HashMap<Integer,RemoteViews>(){};
	private static Gson gson = new Gson();	
	private boolean isAccuConnected = false;
	private boolean firstTime = true;
	private int accuStep = 0;	
	
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
	    handler.removeCallbacks(startRefresh);

        if (action.equals(REFRESH_ACTION)) {
    		switch (accuStep) {
    		case 0:
    			accuStep = 1;
    			break;
    		case 1:
    			accuStep = 2;
    			break;
    		case 2: 
    			accuStep = 0;
    			break;
    		}
    		onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
            //changeAccuStep();
        } 
        else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
        } 
        
        try {
            super.onReceive(context, intent);
        } catch (Exception e) {};
    }

    private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
        Log.d(TAG, "buildLayout="+largeLayout+":"+appWidgetId);
        this.context = context;
        rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        
        showObuComponents();

        
        //Cursor c = context.getContentResolver().query(MojTelekomDataProvider.CONTENT_URI, null, gsm_num_sel, null, null);

        if (firstTime) {
            getObudata();
        	firstTime = false;
        }
   		//handler.postDelayed(startRefresh, Settings.OBU_REFRESH_TIME);
   		handler.postDelayed(startRefresh, 15000);

   		//showObuData();
        changeAccuStep();
        
        // accu step
        final Intent refreshIntent = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent.setAction(BoatGuardWidgetProvider.REFRESH_ACTION);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,  refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.lIcon, refreshPendingIntent);
        
        // open app
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, BoatGuardWidgetProvider.class);
        final Intent intent = new Intent(context, MainActivity.class);
        final PendingIntent rpIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.tv_last_update, rpIntent);
        rv.setOnClickPendingIntent(R.id.logo, rpIntent);
        mgr.updateAppWidget(mgr.getAppWidgetIds(cn), rv);


        return rv;
    }

	private void showObuComponents(){
		Settings.getObuComponents(context);
		HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		Set set = obuComponents.entrySet(); 
		Iterator i = set.iterator();
		Settings.getSettings(context);        
        obuRemoteViews.clear();
        
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuComponent obuComponent = (ObuComponent)map.getValue();
			
			if (obuComponent.getShow() == 0) continue;
			
			RemoteViews rvComponent = null;
			if (!obuComponent.getType().equals("ACCU")) { 
				rvComponent = new RemoteViews(context.getPackageName(), R.layout.widget_list_component);
				
				if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_disabled);
				} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_disabled);
				} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_disabled);
				}				
			}
			else {
				rvComponent = new RemoteViews(context.getPackageName(), R.layout.widget_list_component_accu);
			}
			obuRemoteViews.put(obuComponent.getId_component(), rvComponent);
			//rv.addView(R.id.components, rvComponent);
			
		}
	}

	
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews layout = buildLayout(context, appWidgetIds[i], false);
            appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
    }

	public void getObudata() {
		String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getdata?obuid="+obuId;
    	System.out.println("urlString="+urlString);
		
    	if (Utils.isNetworkConnected(context, false)) {
			/*Comm at = new Comm();
			at.setCallbackListener(clGetObuData);
			at.execute(urlString, null); */
    		
  			try {
			
	            AsyncTask at = new Comm().execute(urlString, null);
	            String res = (String) at.get();
	            
	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	obuStates.clear();
		   		for (int i=0; i< jsonStates.length(); i++) {
		   			ObuState obuState = gson.fromJson(jsonStates.get(i).toString(), ObuState.class);
		   			obuStates.put(obuState.getId_state(), obuState);
		   		}
		   		
		        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		        final ComponentName cn = new ComponentName(context, BoatGuardWidgetProvider.class);
	
	        	showObuData();  
	        	//onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	e.getLocalizedMessage();
   	   		}	        	
			
    	}
    }	 
/*
    private OnTaskCompleteListener clGetObuData = new OnTaskCompleteListener() {

        @Override
        public void onComplete(String res) {
  			try {
  				System.out.println("onComplete");
  		        handler.removeCallbacks(startRefresh);
  				
	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	obuStates.clear();
		   		for (int i=0; i< jsonStates.length(); i++) {
		   			ObuState obuState = gson.fromJson(jsonStates.get(i).toString(), ObuState.class);
		   			obuStates.put(obuState.getId_state(), obuState);
		   		}
		   		
		        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		        final ComponentName cn = new ComponentName(context, BoatGuardWidgetProvider.class);

	        	onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
	        	//showObuData();

	        } catch (Exception e) {
	        	e.printStackTrace();
	        	e.getLocalizedMessage();
   	   		}
        }
    };
    */
	private void showObuData(){
        System.out.println("showObuData1");
		if (obuStates.size() == 0) return;
		String accuDisconnected = ((ObuState)obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId())).getValue();
		if (accuDisconnected != null) {
			isAccuConnected = !accuDisconnected.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue());
		}
        
		//rv.removeAllViews(R.id.components);
		
        Set set = obuStates.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
	        System.out.println("showObuData3");
			Map.Entry map = (Map.Entry)i.next(); 
			ObuState obuState = (ObuState)map.getValue();
			int idState = obuState.getId_state();			
			
			if (idState == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
				rv.setTextViewText(R.id.tv_last_update, context.getResources().getString(R.string.last_update) + " " + Utils.formatDate(obuState.getDateState()));
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getIdComponent());
				String geofence = obuState.getValue();
				
				if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_DISABLED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_disabled);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ENABLED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_home);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ALARM)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_alarm_1);
				}
				rv.addView(R.id.components, rvComponent);
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getIdComponent());
				String pumpState = obuState.getValue();
				
				if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_OK)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.bilge_pumping_animation_day);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_CLODGED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_clodged_1);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_DEMAGED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_demaged_1);
				}
				rv.addView(R.id.components, rvComponent);
			}
			else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getIdComponent());
				String anchorState = obuState.getValue(); 
				
				if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DISABLED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_disabled);
				}			
				else if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_ENABLED)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor);
					int anchorDriftingId = ((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId();
					String anchorDrifting = ((ObuState) obuStates.get(anchorDriftingId)).getValue();
					if (anchorDrifting.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DRIFTING)).getValue())) {
						rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_alarm_1);
					}			
				}	
				rv.addView(R.id.components, rvComponent);
			}			
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) && (isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getIdComponent());
				rvComponent.setTextViewText(R.id.accu_napetost, obuState.getValue() + "%");
				String accuEmpty = ((ObuState)obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_EMPTY)).getId())).getValue();
				if (accuEmpty.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ALARM_BATTERY_EMPTY)).getValue())) {
				//if (Integer.parseInt(obuState.getValue()) < Integer.parseInt(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_BATTERY_ALARM_VALUE)).getValue())) {
					rvComponent.setTextColor(R.id.accu_napetost, context.getResources().getColor(R.color.alarm_red));
				}
				else {
					rvComponent.setTextColor(R.id.accu_napetost, context.getResources().getColor(R.color.text_green));
				}
				rv.addView(R.id.components, rvComponent);
			}			
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) && (isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getIdComponent());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				rvComponent.setTextViewText(R.id.accu_ah, f + "AH");
				rv.setViewVisibility(R.id.accu_ah, View.GONE);
				//rv.addView(R.id.components, rvComponent);
			}	
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) && (isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getIdComponent());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				rvComponent.setTextViewText(R.id.accu_tok, f + "A");
				rv.setViewVisibility(R.id.accu_tok, View.GONE);
				//rv.addView(R.id.components, rvComponent);
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getIdComponent());
				
				String accuDisconnectedState = obuState.getValue();
				if (accuDisconnectedState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue())) {
					rvComponent.setImageViewResource(R.id.accu_disconnected, R.drawable.ic_accu_disconnected_1);
					rvComponent.setViewVisibility(R.id.accu_disconnected, View.VISIBLE);

					rv.addView(R.id.components, rvComponent);
				}
				else {
					changeAccuStep();
					rvComponent.setViewVisibility(R.id.accu_disconnected, View.GONE);
					rvComponent.setViewVisibility(R.id.lIcon, View.VISIBLE);
				}
			}	
		}
	}

	public void changeAccuStep () {
		if (!isAccuConnected) return;
		RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getIdComponent());
		
		switch (accuStep) {
		case 0:
			rvComponent.setViewVisibility(R.id.accu_napetost, View.VISIBLE);
			rvComponent.setViewVisibility(R.id.accu_ah, View.GONE);
			rvComponent.setViewVisibility(R.id.accu_tok, View.GONE);
			rvComponent.setImageViewResource(R.id.step, R.drawable.ic_battery_step_1);
			break;
		case 1:
			rvComponent.setViewVisibility(R.id.accu_napetost, View.GONE);
			rvComponent.setViewVisibility(R.id.accu_ah, View.VISIBLE);
			rvComponent.setViewVisibility(R.id.accu_tok, View.GONE);
			rvComponent.setImageViewResource(R.id.step, R.drawable.ic_battery_step_2);
			break;
		case 2: 
			rvComponent.setViewVisibility(R.id.accu_napetost, View.GONE);
			rvComponent.setViewVisibility(R.id.accu_ah, View.GONE);
			rvComponent.setViewVisibility(R.id.accu_tok, View.VISIBLE);
			rvComponent.setImageViewResource(R.id.step, R.drawable.ic_battery_step_3);
			break;
		}
	}
	
	private Runnable startRefresh = new Runnable() {
	   @Override
	   public void run() {
		   getObudata();
		   //handler.postDelayed(startRefresh, Settings.OBU_REFRESH_TIME);
		   handler.postDelayed(startRefresh, 15000);
	   }
	};

}