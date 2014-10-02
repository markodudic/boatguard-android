package com.boatguard.boatguard.fragments;

import java.util.HashMap;

import com.boatguard.boatguard.R;

import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.utils.Settings;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Switch;

public class BilgePumpFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_bilge_pump, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String pumpAlarmAlways = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_ALWAYS)).getId()).getValue();
        
		final Switch switchPumpAlarmAlways = (Switch) v.findViewById(R.id.switch_pump_alarm_always);
		switchPumpAlarmAlways.setChecked(pumpAlarmAlways.equals("1"));
		switchPumpAlarmAlways.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				//set settings
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_ALWAYS)).getId()).setValue(switchPumpAlarmAlways.isChecked()?"1":"0");
			}
		});	

		
		String pumpAlarmShortPeriod = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_SHORT_PERIOD)).getId()).getValue();
        final Spinner spinnerAlarmShortPeriod = (Spinner) v.findViewById(R.id.spinner_pump_short_period);
        spinnerAlarmShortPeriod.setSelection(getIndex(spinnerAlarmShortPeriod, pumpAlarmShortPeriod), false);
        spinnerAlarmShortPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
 			@Override
 			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 				String val = (String)spinnerAlarmShortPeriod.getSelectedItem();
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_SHORT_PERIOD)).getId()).setValue(val);
			}


			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});	
        
        String pumpAlarmLongPeriod = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).getValue();
        final Spinner spinnerAlarmLongPeriod = (Spinner) v.findViewById(R.id.spinner_pump_long_period);
        
        spinnerAlarmLongPeriod.setSelection(getIndex(spinnerAlarmLongPeriod, pumpAlarmLongPeriod), false);
        spinnerAlarmLongPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
 			@Override
 			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 				String val = (String)spinnerAlarmLongPeriod.getSelectedItem();
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).setValue(val);
			}


			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});	        
        return v;
    }
 
    private int getIndex(Spinner spinner, String myString)
    {
     int index = 0;

     for (int i=0;i<spinner.getCount();i++){
      if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
       index = i;
       i=spinner.getCount();
      }
     }
     return index;
    }     
}
