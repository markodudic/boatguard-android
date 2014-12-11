package com.boatguard.boatguard.widget;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Comm;
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
    }
}

/**
 * The BoatGuard widget's AppWidgetProvider.
 */
public class BoatGuardWidgetProvider extends AppWidgetProvider {
	public static final String TAG = "BoatGuardWidgetProvider";
	
	public static final String REFRESH_ACTION = "com.boatguard.boatguard.widget.REFRESH";
	public static final String REFRESH_OPEN_ACTION = "com.boatguard.boatguard.widget.REFRESH_OPEN";
	
    private RemoteViews rv = null;
	private Context context;
	public static LinkedHashMap<Integer,ObuState> obuStates = new LinkedHashMap<Integer,ObuState>(){};
	public static HashMap<Integer,RemoteViews> obuRemoteViews = new HashMap<Integer,RemoteViews>(){};
	private static Gson gson = new Gson();	
	private boolean isAccuConnected = false;
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
	    //handler.removeCallbacks(startRefresh);

        if (action.equals(REFRESH_ACTION)) {
        	onUpdate(context, AppWidgetManager.getInstance(context), mgr.getAppWidgetIds(cn));
            
        	/*switch (accuStep) {
    		case 0:
    			accuStep = 1;
    			break;
    		case 1:
    			accuStep = 2;
    			break;
    		case 2: 
    			accuStep = 0;
    			break;
    		}*/
    		//changeAccuStep();
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

    private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
        Log.d(TAG, "buildLayout="+largeLayout+":"+appWidgetId);
        this.context = context;
        rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        
        showObuComponents();
        getObudata();
    	showObuData();  
        
        
        final Intent refreshIntent = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent.setAction(BoatGuardWidgetProvider.REFRESH_ACTION);
        final PendingIntent pending = PendingIntent.getBroadcast(context, 0,  refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pending);
        //dam osvezevanje kar fiksno na 30 min
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+(30*60*1000), pending);
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
	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	e.getLocalizedMessage();
   	   		}	        	
			
    	}
    }	 

	private void showObuData(){
        if (obuStates.size() == 0) return;
		String accuDisconnected = ((ObuState)obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId())).getValue();
		if (accuDisconnected != null) {
			isAccuConnected = accuDisconnected.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue());
		}
        
		rv.removeAllViews(R.id.components);
		
        Set set = obuStates.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) {
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
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_pumping_step_9_day);
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
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) && (!isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getIdComponent());
				rvComponent.setTextViewText(R.id.accu_napetost, obuState.getValue() + "%");
				rvComponent.setViewVisibility(R.id.accu_napetost, View.VISIBLE);
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
			/*else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) && (!isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getIdComponent());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				rvComponent.setTextViewText(R.id.accu_ah, f + "AH");
				rv.setViewVisibility(R.id.accu_ah, View.GONE);
				//rv.addView(R.id.components, rvComponent);
			}	
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) && (!isAccuConnected)) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getIdComponent());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				rvComponent.setTextViewText(R.id.accu_tok, f + "A");
				rv.setViewVisibility(R.id.accu_tok, View.GONE);
				//rv.addView(R.id.components, rvComponent);
			}	*/
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getIdComponent());
				
				if (isAccuConnected) {
					rvComponent.setImageViewResource(R.id.accu_disconnected, R.drawable.ic_accu_disconnected_1);
					rvComponent.setViewVisibility(R.id.accu_disconnected, View.VISIBLE);

					rv.addView(R.id.components, rvComponent);
				}
				else {
					//changeAccuStep();
					rvComponent.setViewVisibility(R.id.accu_disconnected, View.GONE);
					//rvComponent.setViewVisibility(R.id.lIcon, View.VISIBLE);
				}
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_LIGHT)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_LIGHT)).getIdComponent());
				String light = obuState.getValue();
				
				if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_off);
				} 
				else if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_off);
				} 
				else {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_disabled);
				}
				rv.addView(R.id.components, rvComponent);
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_FAN)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_LIGHT)).getIdComponent());
				String fan = obuState.getValue();
				
				if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_off);
				} 
				else if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_on);
				} 
				else {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_disabled);
				}
				rv.addView(R.id.components, rvComponent);
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_DOOR)).getId()) { 
				RemoteViews rvComponent = obuRemoteViews.get(((State)Settings.states.get(Settings.STATE_DOOR)).getIdComponent());
				String door = obuState.getValue();
				
				if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_OK)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_ok);
				} 
				else if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_ALARM)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_alarm_1);
				} 
				else {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_disabled);
				}
				rv.addView(R.id.components, rvComponent);
			}			
		}
	}
/*
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
*/
}