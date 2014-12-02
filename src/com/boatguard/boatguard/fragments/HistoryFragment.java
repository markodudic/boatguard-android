package com.boatguard.boatguard.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.Comm.OnTaskCompleteListener;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.gson.Gson;

public class HistoryFragment  extends Fragment {
	private static Gson gson = new Gson();	
	private ListView lvHistory = null;
	private List<HashMap> history = new ArrayList<HashMap>();
	private HistoryAdapter historyAdapter;
	private LayoutInflater inflater;
    
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
		this.inflater = inflater;
        final View v = inflater.inflate(R.layout.fragment_history, null);
        lvHistory = (ListView)v.findViewById(R.id.lv_history);
        
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        historyAdapter = new HistoryAdapter();
   		lvHistory.setAdapter(historyAdapter);
   		
        getObuHistoryData();

        return v;
    }

	public void getObuHistoryData() {
    	String obuId = Utils.getPrefernciesString(getActivity(), Settings.SETTING_OBU_ID);
   		
    	String urlString = this.getString(R.string.server_url) + "gethistorydata?obuid="+obuId;
    	if (Utils.isNetworkConnected(getActivity(), false)) {
            	Comm at = new Comm();
    			at.setCallbackListener(clGetObuHistoryData);
    			at.execute(urlString, null); 
    	}
	}
	
    private OnTaskCompleteListener clGetObuHistoryData = new OnTaskCompleteListener() {

        @Override
        public void onComplete(String res) {
        	//System.out.println("RES="+res);
        	 
        	JSONObject jRes;
			try {
				jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	history.clear();
	        	for (int i=0; i< jsonStates.length(); i++) {
	    	   		JSONArray jsonState = (JSONArray)jsonStates.get(i);
    	   			//System.out.println("jsonState="+jsonState.toString());
	    	   		LinkedHashMap<Integer,ObuState> obuStates = new LinkedHashMap<Integer,ObuState>(){};
	    	   		for (int ii=0; ii< jsonState.length(); ii++) {
	    	   			ObuState obuState = gson.fromJson(jsonState.get(ii).toString(), ObuState.class);
	    	   			//System.out.println(obuState.getId_state()+":"+obuState.getDateState());
	    	   			obuStates.put(obuState.getId_state(), obuState);
	    	   		}
    	   			history.add(obuStates);
		   		}
	        	historyAdapter.notifyDataSetChanged();	        	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    public class HistoryAdapter extends BaseAdapter {
    	  
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	LinkedHashMap<Integer,ObuState> obuStates = (LinkedHashMap<Integer,ObuState>) getItem(position);
            View v = inflater.inflate(R.layout.row_history, parent, false);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, LayoutParams.MATCH_PARENT);
            
            Set set = obuStates.entrySet(); 
    		Iterator i = set.iterator();
    		while(i.hasNext()) { 
    			Map.Entry map = (Map.Entry)i.next(); 
    			ObuState obuState = (ObuState)map.getValue();
    			int idState = obuState.getId_state();			
    			
    			if (idState == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
    	            ((TextView) v.findViewById(R.id.tv_last_update)).setText(Utils.formatDate((obuStates.get(((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId())).getDateState()));
    			}	
    			else if (idState == ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()) { 
    	            ImageView img = new ImageView(getActivity());
    				String geofence = obuState.getValue();
    				
    				if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_DISABLED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_geofence_disabled);
    				} 
    				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ENABLED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_geofence_home);
    				} 
    				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ALARM)).getValue())) {
    		            img.setImageResource(R.drawable.ic_geofence_alarm_1);
    				}
    	            ((LinearLayout) v.findViewById(R.id.components)).addView(img);
    			}			
    			else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
    	            ImageView img = new ImageView(getActivity());
    				String pumpState = obuState.getValue();
    				
    				if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_OK)).getValue())) {
    		            img.setImageResource(R.drawable.ic_bilgepump);
    				}
    				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
    		            img.setImageResource(R.drawable.bilge_pumping_animation_day);
    				}
    				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_CLODGED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_bilgepump_clodged_1);
    				}
    				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_DEMAGED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_bilgepump_demaged_1);
    				}
    	            ((LinearLayout) v.findViewById(R.id.components)).addView(img);
    			}
    			else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()) { 
    	            ImageView img = new ImageView(getActivity());
    				String anchorState = obuState.getValue(); 
    				
    				if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DISABLED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_anchor_disabled);
    				}			
    				else if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_ENABLED)).getValue())) {
    		            img.setImageResource(R.drawable.ic_anchor);
    					int anchorDriftingId = ((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId();
    					String anchorDrifting = ((ObuState) obuStates.get(anchorDriftingId)).getValue();
    					if (anchorDrifting.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DRIFTING)).getValue())) {
        		            img.setImageResource(R.drawable.ic_anchor_alarm_1);
    					}			
    				}	
    	            ((LinearLayout) v.findViewById(R.id.components)).addView(img);
    			}	
    			
    			
    			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) { 
    	            View vNapetost = inflater.inflate(R.layout.row_history_text, parent, false);
    	            ((TextView) vNapetost.findViewById(R.id.tv_history_row)).setText(obuState.getValue() + "%");
    				if (Integer.parseInt(obuState.getValue()) < Integer.parseInt(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_BATTERY_ALARM_VALUE)).getValue())) {
    					((TextView) vNapetost.findViewById(R.id.tv_history_row)).setTextColor(getActivity().getResources().getColor(R.color.alarm_red));
    				}
    				else {
    					((TextView) vNapetost.findViewById(R.id.tv_history_row)).setTextColor(getActivity().getResources().getColor(R.color.text_green));
    				}
    				((LinearLayout) v.findViewById(R.id.components)).addView(vNapetost);    				
    			}			
    			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) { 
    				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
    				View vv = inflater.inflate(R.layout.row_history_text, parent, false);
    	            ((TextView) vv.findViewById(R.id.tv_history_row)).setText(f + "AH");
    				((LinearLayout) v.findViewById(R.id.components)).addView(vv);
    				
    			}	
    			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) { 
    				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
    				View vv = inflater.inflate(R.layout.row_history_text, parent, false);
    	            ((TextView) vv.findViewById(R.id.tv_history_row)).setText(f + "A");
    				((LinearLayout) v.findViewById(R.id.components)).addView(vv);
    				
    			}	
    			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId()) { 
    				ImageView img = new ImageView(getActivity());
    				
    				String accuDisconnectedState = obuState.getValue();
    				if (accuDisconnectedState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue())) {
    		            img.setImageResource(R.drawable.ic_accu_disconnected_1);
    				}
    	            ((LinearLayout) v.findViewById(R.id.components)).addView(img);
    			}	
    			
    		}
            
            
            return v;
        }

		@Override
		public int getCount() {
			return history.size();
		}

		@Override
		public Object getItem(int position) {
			return history.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
    }
}



