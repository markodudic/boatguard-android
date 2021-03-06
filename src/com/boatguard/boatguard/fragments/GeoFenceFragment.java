package com.boatguard.boatguard.fragments;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.flurry.android.FlurryAgent;

public class GeoFenceFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
    	FlurryAgent.logEvent("GeoFence");

    	final View v = inflater.inflate(R.layout.fragment_geo_fence, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
        String geoFenceValue = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).getValue();
        
        final TextView tvGeoFence = (TextView)v.findViewById(R.id.tv_geo_fence);
        tvGeoFence.setText(geoFenceValue + "m");
		final Switch switchGeoFence = (Switch) v.findViewById(R.id.switch_geo_fence);
		switchGeoFence.setChecked(geoFence.equals("1"));
		switchGeoFence.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
		    	FlurryAgent.logEvent("GeoFence On/Off");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).setValue(switchGeoFence.isChecked()?"1":"0");
			}
		});
		
		SeekBar seekbarGeoFence = (SeekBar) v.findViewById(R.id.seekbar_geo_fence);
		seekbarGeoFence.setProgress(Integer.parseInt(geoFenceValue));
		seekbarGeoFence.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				tvGeoFence.setText(progressChanged + "m");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).setValue(progressChanged+"");
			}
		});
		
		
		TextView tvDefine = (TextView) v.findViewById(R.id.button_geo_fence_define);
		tvDefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
		    	FlurryAgent.logEvent("GeoFence Define");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_LAT)).getId()).setValue("SET");
				new Settings(getActivity()).setObuSettings();
	    		getActivity().finish();
			}
		});	

		
        return v;
    }
 
}
