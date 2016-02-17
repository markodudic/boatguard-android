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

public class AnchorDriftingFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
    	FlurryAgent.logEvent("Anchor Settings");
        final View v = inflater.inflate(R.layout.fragment_anchor_drifting, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String anchorDrifting = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).getValue();
        String anchorDriftingValue = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId()).getValue();
        
        final TextView tvanchorDrifting = (TextView)v.findViewById(R.id.tv_anchor_drifting);
        tvanchorDrifting.setText(anchorDriftingValue + "m");
		final Switch switchanchorDrifting = (Switch) v.findViewById(R.id.switch_anchor_drifting);
		switchanchorDrifting.setChecked(anchorDrifting.equals("1"));
		switchanchorDrifting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
		    	FlurryAgent.logEvent("Anchor On/Off");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).setValue(switchanchorDrifting.isChecked()?"1":"0");
			}
		});
        
		SeekBar seekbaranchorDrifting = (SeekBar) v.findViewById(R.id.seekbar_anchor_drifting);
		seekbaranchorDrifting.setProgress(Integer.parseInt(anchorDriftingValue));
		seekbaranchorDrifting.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				tvanchorDrifting.setText(progressChanged + "m");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId()).setValue(progressChanged+"");
			}
		});
		
		
		TextView tvDefine = (TextView) v.findViewById(R.id.button_anchor_drifting_define);
		tvDefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
		    	FlurryAgent.logEvent("Anchor Define");
				Settings.obuSettings.get(((State)Settings.states.get(Settings.STATE_LAT)).getId()).setValue("SET");
		        new Settings(getActivity()).setObuSettings();
		        getActivity().finish();
			}
		});	

		
        return v;
    }
 
}
