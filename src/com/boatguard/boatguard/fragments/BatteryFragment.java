package com.boatguard.boatguard.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

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
		
		TextView tvDefine = (TextView) v.findViewById(R.id.button_battery_energy_reset);
		tvDefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				//set settings
		        Settings.obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_ENERGY_RESET)).getId()).setValue("1");
			}
		});	
		
		LineChart chart = (LineChart) v.findViewById(R.id.chart);
		chart.setDrawLegend(false);
		chart.setDrawYValues(false);
		chart.setDescription("");
		  
	    ArrayList<Entry> valsComp = new ArrayList<Entry>();
	    ArrayList<String> xVals = new ArrayList<String>();
	    List<HashMap> history = MainActivity.history;
	    int historySize = history.size();
	    int[] colors = new int[history.size()];
	    
	    for (int i=historySize-1; i>-1; i--) 
	    {
	    	@SuppressWarnings("unchecked")
			LinkedHashMap<Integer,ObuState> obuStates = (LinkedHashMap<Integer, ObuState>) history.get(i);
	    	ObuState state = obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId());
	    	if (state==null) continue;
	    	Entry c1e = new Entry(Float.parseFloat(state.getValue()), historySize-1-i);
		    valsComp.add(c1e);
		    xVals.add(Utils.formatDateShort(state.getDateState()));
		    if (Float.parseFloat(state.getValue()) > Float.parseFloat(batteryAlarmLevel)) {
		    	colors[historySize-1-i]=getResources().getColor(R.color.text_green);
		    }
		    else {
		    	colors[historySize-1-i]=getResources().getColor(R.color.alarm_red);
		    }
	    }
	    
	    LineDataSet setComp = new LineDataSet(valsComp, "battery");
	    //setComp.setColor(getResources().getColor(R.color.text_green));
	    setComp.setCircleColors(colors);
	    setComp.setColors(colors);
	    setComp.setCircleSize(5f);
	    setComp.setLineWidth(2f);
	    
	    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
	    dataSets.add(setComp);
	    
	    LineData data = new LineData(xVals, dataSets);
	    LimitLine ll = new LimitLine(Float.parseFloat(batteryAlarmLevel));
	    ll.setLineColor(Color.RED);
	    ll.setLineWidth(2f);
	    data.addLimitLine(ll);

	    chart.setData(data);
	    
	    XLabels x = chart.getXLabels();
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Dosis-Regular.otf");  
	    x.setTypeface(font); 
	    x.setTextSize(getResources().getDimension(R.dimen.chart_text_size)); 

	    YLabels y = chart.getYLabels();
	    y.setTypeface(font); 
	    y.setTextSize(getResources().getDimension(R.dimen.chart_text_size)); 
		    
		    
   		return v;
    }
     	
}
