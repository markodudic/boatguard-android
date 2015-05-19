package com.boatguard.boatguard.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

public class BilgePumpFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
    	FlurryAgent.logEvent("Bilge Pump Settings");
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
        MySpinnerAdapter adapterSP = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.short_period))
        );
        spinnerAlarmShortPeriod.setAdapter(adapterSP);
        spinnerAlarmShortPeriod.setSelection(Utils.getIndex(spinnerAlarmShortPeriod, pumpAlarmShortPeriod), false);
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
        MySpinnerAdapter adapterLP = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.long_period))
        );
        spinnerAlarmLongPeriod.setAdapter(adapterLP);
        spinnerAlarmLongPeriod.setSelection(Utils.getIndex(spinnerAlarmLongPeriod, pumpAlarmLongPeriod), false);
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
        
        final String pumpPeriod = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).getValue();
        
        
        LineChart chart = (LineChart) v.findViewById(R.id.chart);
		chart.setDrawLegend(false);
		chart.setDrawYValues(false);
		chart.setDescription("");
		  
	    ArrayList<Entry> valsComp = new ArrayList<Entry>();
	    ArrayList<String> xVals = new ArrayList<String>();
	    List<HashMap> history = MainActivity.history;
	    int historySize = history.size();
	    String lastDate = null;
	    
	    LinkedHashMap<String,Float> pumpStates = new LinkedHashMap<String,Float>(){};
	    for (int i=historySize-1; i>-1; i--) 
	    {
	    	LinkedHashMap<Integer,ObuState> obuStates = (LinkedHashMap<Integer, ObuState>) history.get(i);
	    	ObuState state = obuStates.get(((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId());
	    	if (state==null) continue;
	    	Float value = pumpStates.get(Utils.formatDateShort(state.getDateState()));
			if (value == null) {
				value = 12.0f;
				pumpStates.put(Utils.formatDateShort(state.getDateState()), value);
			}
			
	    	if (state.getValue().equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
	    		if (lastDate != null) {
	    			value += Utils.getDateDif(lastDate, state.getDateState());
					pumpStates.put(Utils.formatDateShort(state.getDateState()), value);
	    		}
	    	}
			lastDate = state.getDateState();
	    }	
	    
	    int[] colors = new int[pumpStates.size()];
	    int i = 0;
        Set set = pumpStates.entrySet(); 
		Iterator ii = set.iterator();
		while(ii.hasNext()) { 
			Map.Entry map = (Map.Entry)ii.next(); 
			Entry c1e = new Entry((Float)map.getValue(), i);
			valsComp.add(c1e);
			xVals.add((String)map.getKey());

			if ((Float)map.getValue() < Float.parseFloat(pumpPeriod)) {
		    	colors[i]=getResources().getColor(R.color.text_green);
		    }
		    else {
		    	colors[i]=getResources().getColor(R.color.alarm_red);
		    }
			i++;
		}

	    
	    LineDataSet setComp = new LineDataSet(valsComp, "pump");
	    //setComp.setColor(getResources().getColor(R.color.text_green));
	    setComp.setCircleColors(colors);
	    setComp.setColors(colors);
	    setComp.setCircleSize(5f);
	    setComp.setLineWidth(2f);
	    
	    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
	    dataSets.add(setComp);
	    
	    LineData data = new LineData(xVals, dataSets);
	    LimitLine ll = new LimitLine(Float.parseFloat(pumpPeriod));
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
    
    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Dosis-Regular.otf");

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }    
}
