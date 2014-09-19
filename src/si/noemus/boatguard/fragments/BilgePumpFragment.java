package si.noemus.boatguard.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import si.noemus.boatguard.R;
import si.noemus.boatguard.R.id;
import si.noemus.boatguard.R.layout;
import si.noemus.boatguard.R.string;
import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

public class BilgePumpFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_bilge_pump, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String pumpAlarmAlways = obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_ALWAYS)).getId()).getValue();
        
		final Switch switchPumpAlarmAlways = (Switch) v.findViewById(R.id.switch_pump_alarm_always);
		switchPumpAlarmAlways.setChecked(pumpAlarmAlways.equals("1"));
		switchPumpAlarmAlways.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				//set settings
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_ALWAYS)).getId()).setValue(switchPumpAlarmAlways.isChecked()?"1":"0");
		        Settings.setObuSettings(getActivity());
			}
		});	

		
		String pumpAlarmShortPeriod = obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_SHORT_PERIOD)).getId()).getValue();
        final Spinner spinnerAlarmShortPeriod = (Spinner) v.findViewById(R.id.spinner_pump_short_period);
        spinnerAlarmShortPeriod.setSelection(getIndex(spinnerAlarmShortPeriod, pumpAlarmShortPeriod), false);
        spinnerAlarmShortPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
 			@Override
 			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 				String val = (String)spinnerAlarmShortPeriod.getSelectedItem();
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_SHORT_PERIOD)).getId()).setValue(val);
		        Settings.setObuSettings(getActivity());
			}


			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});	
        
        String pumpAlarmLongPeriod = obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).getValue();
        final Spinner spinnerAlarmLongPeriod = (Spinner) v.findViewById(R.id.spinner_pump_long_period);
        
        spinnerAlarmLongPeriod.setSelection(getIndex(spinnerAlarmLongPeriod, pumpAlarmLongPeriod), false);
        spinnerAlarmLongPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {
 			@Override
 			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 				String val = (String)spinnerAlarmLongPeriod.getSelectedItem();
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).setValue(val);
		        Settings.setObuSettings(getActivity());
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
