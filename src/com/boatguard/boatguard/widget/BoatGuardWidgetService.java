package com.boatguard.boatguard.widget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class BoatGuardWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId;
	//public static LinkedHashMap<Integer,ObuState> obuStates = new LinkedHashMap<Integer,ObuState>(){};
	//private static Gson gson = new Gson();	
	private boolean isAccuConnected = false;
	
    public StackRemoteViewsFactory(Context context, Intent intent) {
    	mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
    	Settings.getSettings(mContext);
    	Settings.getObuComponents(mContext);
    }

    public void onDestroy() {
    }

    public int getCount() {
    	return Settings.obuComponents.size();
    }

    public RemoteViews getViewAt(int position) {
    	List keys = new ArrayList(Settings.obuComponents.keySet());
    	ObuComponent obuComponent = (ObuComponent)Settings.obuComponents.get(keys.get(position));
    	
    	RemoteViews rvComponent = null;
		
    	if (obuComponent.getShow() == 0) return null;
		if (!obuComponent.getType().equals("ACCU")) { 
			rvComponent = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_component);
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId());
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId());
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_ANCHOR)).getId());
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_LIGHT)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_LIGHT)).getId());
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_FAN)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_FAN)).getId());
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_DOOR)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_disabled);
				rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_DOOR)).getId());
			}				
		}
		else {
			rvComponent = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_component_accu);
			rvComponent = showObuData(rvComponent, ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId());
		}
		
		Intent i = new Intent();
		rvComponent.setOnClickFillInIntent(R.id.logo, i);
		
		
        return rvComponent;
    }
    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
    	return null;
    }

    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light background views)
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

	@Override
	public void onDataSetChanged() {
    	//getObudata();
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}


    private RemoteViews showObuData(RemoteViews rvComponent, Integer idState){
        if (BoatGuardWidgetProvider.obuStates.size() == 0) return rvComponent;
		String accuDisconnected = ((ObuState)BoatGuardWidgetProvider.obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId())).getValue();
		if (accuDisconnected != null) {
			isAccuConnected = accuDisconnected.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue());
		}
        
		ObuState obuState = BoatGuardWidgetProvider.obuStates.get(idState);
		if (obuState == null) return rvComponent;
		if (idState == ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()) { 
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
		}			
		else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
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
			else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_SERVIS)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_servis_1);
			}
		}
		else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()) { 
			String anchorState = obuState.getValue(); 
			
			if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DISABLED)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_disabled);
			}			
			else if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_ENABLED)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor);
				int anchorDriftingId = ((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId();
				String anchorDrifting = ((ObuState)BoatGuardWidgetProvider.obuStates.get(anchorDriftingId)).getValue();
				if (anchorDrifting.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DRIFTING)).getValue())) {
					rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_alarm_1);
				}			
			}	
		}			
		else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) && (!isAccuConnected)) { 
			rvComponent.setTextViewText(R.id.accu_napetost, obuState.getValue() + "%");
			rvComponent.setViewVisibility(R.id.accu_napetost, View.VISIBLE);
			String accuEmpty = ((ObuState)BoatGuardWidgetProvider.obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_EMPTY)).getId())).getValue();
			if (accuEmpty.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ALARM_BATTERY_EMPTY)).getValue())) {
				rvComponent.setTextColor(R.id.accu_napetost, mContext.getResources().getColor(R.color.alarm_red));
			}
			else {
				rvComponent.setTextColor(R.id.accu_napetost, mContext.getResources().getColor(R.color.text_green));
			}
		}			
		else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) { 
			if (isAccuConnected) {
				rvComponent.setImageViewResource(R.id.accu_disconnected, R.drawable.ic_accu_disconnected_1);
				rvComponent.setViewVisibility(R.id.accu_disconnected, View.VISIBLE);
			}
			else {
				rvComponent.setViewVisibility(R.id.accu_disconnected, View.GONE);
			}
		}	
		else if (idState == ((State)Settings.states.get(Settings.STATE_LIGHT)).getId()) { 
			String light = obuState.getValue();
			
			if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_disabled);
			} 
			else if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light);
			} 
			else {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_disabled);
			}
		}			
		else if (idState == ((State)Settings.states.get(Settings.STATE_FAN)).getId()) { 
			String fan = obuState.getValue();
			
			if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_disabled);
			} 
			else if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan);
			} 
			else {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_disabled);
			}
		}			
		else if (idState == ((State)Settings.states.get(Settings.STATE_DOOR)).getId()) { 
			String door = obuState.getValue();
			
			if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_OK)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door);
			} 
			else if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_ALARM)).getValue())) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_alarm_1);
			} 
			else {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_disabled);
			}
		}			
		
		return rvComponent;
	}

	
	
}
