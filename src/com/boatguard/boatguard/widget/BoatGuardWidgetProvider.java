package com.boatguard.boatguard.widget;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.utils.Settings;

/**
 * Our data observer just notifies an update for all MojTelekom widgets when it detects a change.
 */
class MojTelekomDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    MojTelekomDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {
        // The data has changed, so notify the widget that the collection view needs to be updated.
        // In response, the factory's onDataSetChanged() will be called which will requery the
        // cursor for the new data.

        //mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.mojtelekom_list);
    }
}

/**
 * The MojTelekom widget's AppWidgetProvider.
 */
public class BoatGuardWidgetProvider extends AppWidgetProvider {
	public static final String TAG = "BoatGuardWidgetProvider";
	
	public static final String REFRESH_ACTION = "com.boatguard.boatguard.widget.REFRESH";




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
        else if (action.equals(AppWidgetManager.ACTION_APPWIDGET_DELETED)) {
        } 
        
        try {
            super.onReceive(context, intent);
        } catch (Exception e) {};
    }

    private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
        RemoteViews rv = null;
        Log.d(TAG, "buildLayout="+largeLayout+":"+appWidgetId);
        
        Cursor c = context.getContentResolver().query(BoatGuardDataProvider.CONTENT_URI, null, null, null, null);
        /*if (c == null) {
        	//prikazem vse disablano
        	
        	
            return null;
        }*/
        
        rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        showObuComponents(context);
        
        // refresh
        /*final Intent refreshIntent = new Intent(context, BoatGuardWidgetProvider.class);
        refreshIntent.setAction(BoatGuardWidgetProvider.REFRESH_ACTION);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,  refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);
        rv.setOnClickPendingIntent(R.id.refresh_time, refreshPendingIntent);
*/

        return rv;
    }

	private void showObuComponents(Context context){
		//TableLayout lComponents = (TableLayout)findViewById(R.id.components);
		System.out.println("showObuComponents");
try {
        HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		System.out.println("showObuComponents="+obuComponents.size());
		Set set = obuComponents.entrySet(); 
		Iterator i = set.iterator();
		View component = null;
		System.out.println("showObuComponents");
        Settings.getSettings(context);        
		
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuComponent obuComponent = (ObuComponent)map.getValue();
			
			System.out.println("WIDGET1="+obuComponent.getType());
			if (obuComponent.getShow() == 0) continue;
			
			int lc = R.layout.widget_list_component;
			if (obuComponent.getType().equals("ACCU")) { 
				lc = R.layout.widget_list_component_accu;
			}

			LayoutInflater inflater = (LayoutInflater) (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		    component = inflater.inflate(lc, null);
		    component.setId(obuComponent.getId_component());
	        
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_geofence_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
			    ((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_bilgepump);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
			    ((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_anchor_disabled); 
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ACCU)) { 
				//((TextView)component.findViewById(R.id.accu_napetost)).setText("");
			} 

		}
} catch (Exception e) {
	System.out.println("Exceptio="+e.getMessage());
	e.printStackTrace();
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
        Log.d(TAG, "onAppWidgetOptionsChanged");

    }


}