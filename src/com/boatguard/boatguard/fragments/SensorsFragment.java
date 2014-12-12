package com.boatguard.boatguard.fragments;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;

public class SensorsFragment  extends Fragment {
	protected LayoutInflater inflater;
	private String[] sensors = null;
	private String type = null;
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_sensors, null);
        this.inflater = inflater;

        type = getArguments().getString("type");    
        sensors = getArguments().getStringArray("sensors");    
        		
        ObuSensorsAdapter adapter = new ObuSensorsAdapter();
   		ListView lvObuAlarms = (ListView)v.findViewById(R.id.lv_obu_sensors);
   		lvObuAlarms.setAdapter(adapter);
   		
   		return v;
    }
    

	
    public class ObuSensorsAdapter extends BaseAdapter {
    	  
		@SuppressLint("NewApi")
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.row_sensor, parent, false);
			
			TextViewFont tvAlarmMessage = (TextViewFont)v.findViewById(R.id.tv_sensor_message_short);
			tvAlarmMessage.setText(sensors[position]);
			tvAlarmMessage.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
			
	   		final Switch sensorSwitch = (Switch)v.findViewById(R.id.switch_sensor_enabled);
	   		sensorSwitch.setChecked(((ObuSetting)Settings.obuSettings.get(((State)Settings.states.get(type)).getId())).getValue().equals("1"));
	   		sensorSwitch.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
			        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
			        obuSettings.get(((State)Settings.states.get(type)).getId()).setValue(sensorSwitch.isChecked()?"1":"0");
	            	Settings.setObuSettings(getActivity());
	            }
	        });  

			
			return v;
		}
		@Override
		public int getCount() {
			return sensors.length;
		}
		@Override
		public Object getItem(int position) {
			return sensors[position];
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}		
    }	
	
	
	
	
}
