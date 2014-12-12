package com.boatguard.boatguard.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.utils.Settings;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class BoatGuardWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
    	System.out.println("*****************RemoteViewsqqqqqqqqq**************");
    	return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
    }

    public void onDestroy() {
    }

    public int getCount() {
        return Settings.obuComponents.size();
    }

    public RemoteViews getViewAt(int position) {
    	System.out.println("*****************RemoteViews**************");
    	
    	List keys = new ArrayList(Settings.obuComponents.keySet());
    	ObuComponent obuComponent = (ObuComponent)keys.get(position);
    	
    	RemoteViews rvComponent = null;
		
    	System.out.println(obuComponent.getName());
    	
    	//if (obuComponent.getShow() == 0) return null;
		
		if (!obuComponent.getType().equals("ACCU")) { 
			rvComponent = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_component);
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_geofence_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_bilgepump_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_anchor_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_LIGHT)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_light_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_FAN)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_fan_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_DOOR)) { 
				rvComponent.setImageViewResource(R.id.logo, R.drawable.ic_door_disabled);
			}				
		}
		else {
			rvComponent = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_component_accu);
		}
		
    	/*
        // Get the data for this position from the content provider
        String day = "Unknown Day";
        int temp = 0;
        if (mCursor.moveToPosition(position)) {
            final int dayColIndex = mCursor.getColumnIndex(WeatherDataProvider.Columns.DAY);
            final int tempColIndex = mCursor.getColumnIndex(
                    WeatherDataProvider.Columns.TEMPERATURE);
            day = mCursor.getString(dayColIndex);
            temp = mCursor.getInt(tempColIndex);
        }

        // Return a proper item with the proper day and temperature
        final String formatStr = mContext.getResources().getString(R.string.item_format_string);
        final int itemId = R.layout.widget_item;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp, day));

        // Set the click intent so that we can handle it and show a toast message
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putString(WeatherWidgetProvider.EXTRA_DAY_ID, day);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
*/
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

}
