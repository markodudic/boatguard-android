package si.noemus.boatguard;

import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
		
		TextView tvTitle = (TextView) findViewById(R.id.actionbar_text);
        tvTitle.setText(R.string.title_activity_location);

        //location
        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_location));
        
        map = mapFragment.getMap();
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        
		Bundle extras = getIntent().getExtras();
		double lat = extras.getDouble("lat");
		double lon = extras.getDouble("lon");
		String date = extras.getString("date");

		if (lat != 0 && lon != 0) {
			LatLng latlng = new LatLng(lon, lat);
	        Marker newmarker = map.addMarker(new MarkerOptions().position(latlng).title(getResources().getString(R.string.location_title) + " " + date).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
	        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(14.0f).build();
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
	        map.moveCamera(cameraUpdate);
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
