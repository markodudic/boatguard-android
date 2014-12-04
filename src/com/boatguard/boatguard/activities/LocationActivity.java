package com.boatguard.boatguard.activities;

import java.util.HashMap;
import java.util.Iterator;
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
        
		/*Bundle extras = getIntent().getExtras();
		double lat = extras.getDouble("lat");
		double lon = extras.getDouble("lon");
		String date = extras.getString("date");*/

		
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
			} else if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
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
	        Marker newmarker = map.addMarker(new MarkerOptions().position(latlng).title(getResources().getString(R.string.location_title) + " " + date).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
	        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(14.0f).build();
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
	        map.moveCamera(cameraUpdate);
		}  
        
		// Instantiates a new CircleOptions object and defines the center and radius
		HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
        double geoFenceValue = Double.parseDouble(obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).getValue());
        
        if (geoFence.equals("1")) {
			CircleOptions circleOptions = new CircleOptions()
			    .center(new LatLng(lat, lon))
			    .radius(geoFenceValue); // In meters
	
			// Get back the mutable Circle
			Circle circle = map.addCircle(circleOptions);
			circle.setFillColor(getResources().getColor(R.color.alarm_red));
			circle.setStrokeColor(getResources().getColor(R.color.alarm_green));
			circle.setStrokeWidth(2.0f);
        }
		
		ImageView btnBack = (ImageView) findViewById(R.id.iv_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});          
	}  

}
