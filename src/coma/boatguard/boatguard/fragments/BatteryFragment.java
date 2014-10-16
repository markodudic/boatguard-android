package com.boatguard.boatguard.fragments;

import java.util.HashMap;

import com.boatguard.boatguard.R;

import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.utils.Settings;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class BatteryFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_battery, null);

        final HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        final String batteryCapacity = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_CAPACITY)).getId()).getValue();
        String batteryAlarmLevel = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_ALARM_LEVEL)).getId()).getValue();

        final EditText etBatteryCapacity = (EditText) v.findViewById(R.id.battery_capacity);
        etBatteryCapacity.setText(batteryCapacity + "Ah");
        etBatteryCapacity.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
		        obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_CAPACITY)).getId()).setValue(etBatteryCapacity.getText().toString().replaceAll("Ah", ""));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
        ImageView ivBatteryCapacity = (ImageView) v.findViewById(R.id.iv_battery_capacity);
		ivBatteryCapacity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBatteryCapacity.setText("");
			} 
		});

        final TextView tvBatteryAlarmLevel = (TextView)v.findViewById(R.id.tv_battery_alarm_level);
        tvBatteryAlarmLevel.setText(batteryAlarmLevel + "%");
		SeekBar seekbarBatteryAlarmLevel = (SeekBar) v.findViewById(R.id.seekbar_battery_alarm_level);
		seekbarBatteryAlarmLevel.setProgress(Integer.parseInt(batteryAlarmLevel));
		seekbarBatteryAlarmLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				tvBatteryAlarmLevel.setText(progressChanged + "%");
		        obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_ALARM_LEVEL)).getId()).setValue(progressChanged+"");
			}
		});
		
		
   		return v;
    }
     	
}
