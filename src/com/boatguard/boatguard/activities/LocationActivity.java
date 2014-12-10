package com.boatguard.boatguard.activities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class LocationActivity extends Activity {

	private static MapFragment lFragment;
    GoogleMap map;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = Utils.getPrefernciesInt(this, Settings.SETTING_THEME);
		if (theme != -1) {
			setTheme(theme);			
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_back);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.actionbar_text);
		
		TextViewFont tvTitle = (TextViewFont) findViewById(R.id.actionbar_text);
        tvTitle.setText(R.string.title_activity_location);
        tvTitle.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_big));
        
        //location
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_location));
        
        map = mapFragment.getMap();
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);

				
		ImageView btnBack = (ImageView) findViewById(R.id.iv_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});          
	}  

	
	@Override
	protected void onResume() {
		super.onResume();
		
		//last position
		HashMap<Integer,ObuState> obuStates = MainActivity.obuStates;

		double lat=0, lon=0;
		String date = "";
	    Set set = obuStates.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuState obuState = (ObuState)map.getValue();
			if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_LAT)).getId()) { 
		        lat = Double.parseDouble(obuState.getValue());
			}
			else if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_LON)).getId()) { 
		        lon = Double.parseDouble(obuState.getValue());
			} 
			else if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
				date = Utils.formatDate(obuState.getDateState());
			}
		}
		if (lat != 0 && lon != 0) {
			double latF = Math.floor(lat/100);
			double latD = (lat/100 - latF)/0.6;
			lat = latF + latD;
			double lonF = Math.floor(lon/100);
			double lonD = (lon/100 - lonF)/0.6;
			lon = lonF + lonD;
	
			LatLng latlng = new LatLng(lon, lat);
	        Marker newmarker = map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latlng).title(getResources().getString(R.string.location_title) + " " + date).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
	        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(14.0f).build();
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
	        map.moveCamera(cameraUpdate);
		}  
		
		//geo fence radius
		HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
	    String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
	    double geoFenceValue = Double.parseDouble(obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).getValue());
	    
	    if (geoFence.equals("1")) {
	    	CircleOptions circleOptions = new CircleOptions()
			    .center(new LatLng(lon, lat))
			    .radius(geoFenceValue); // In meters
	
			// Get back the mutable Circle
			Circle circle = map.addCircle(circleOptions);
			circle.setFillColor(getResources().getColor(R.color.location_fill));
			circle.setStrokeColor(getResources().getColor(R.color.location_stroke));
			circle.setStrokeWidth(4.0f);
	    }

	    //history positions
		double latLast=0, lonLast=0;
		PolylineOptions rectOptions = new PolylineOptions();
		List<HashMap> obuHistory = MainActivity.history;
        for (int ii=0; ii<obuHistory.size(); ii++) {
        	LinkedHashMap<Integer,ObuState> obuHistoryStates = (LinkedHashMap<Integer,ObuState>) obuHistory.get(ii);
        	lat = 0; lon = 0;
        	ObuState os = obuHistoryStates.get(((State)Settings.states.get(Settings.STATE_LAT)).getId());
        	if (os != null){
        		lat = Double.parseDouble(os.getValue());	
        	}
        	os = obuHistoryStates.get(((State)Settings.states.get(Settings.STATE_LON)).getId());
        	if (os != null){
        		lon = Double.parseDouble(os.getValue());	
        	}
        	date = Utils.formatDate(obuHistoryStates.get(((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()).getDateState());

        	if ((Math.abs(latLast - lat) < 0.1) || (Math.abs(lonLast - lon) < 0.1)) {
        		continue;
        	}
    		latLast = lat;
    		lonLast = lon;
        	
    		if (lat != 0 && lon != 0) {
    			double latF = Math.floor(lat/100);
    			double latD = (lat/100 - latF)/0.6;
    			lat = latF + latD;
    			double lonF = Math.floor(lon/100);
    			double lonD = (lon/100 - lonF)/0.6;
    			lon = lonF + lonD;
    			LatLng latlng = new LatLng(lon, lat);
    			rectOptions.add(latlng);
    			
    			CircleOptions circleOptions = new CircleOptions()
			    .center(latlng)
			    .radius(10.0f);
    			
    			Circle circle = map.addCircle(circleOptions);
    			circle.setFillColor(getResources().getColor(R.color.location_history));
    			circle.setStrokeColor(getResources().getColor(R.color.location_history));
    		}  
        }
        
        Polyline polyline = map.addPolyline(rectOptions);
        polyline.setColor(getResources().getColor(R.color.location_history));
        polyline.setGeodesic(true);
        polyline.setWidth(3.0f);
	}
		
}
