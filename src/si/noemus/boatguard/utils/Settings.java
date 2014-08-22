package si.noemus.boatguard.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.R;
import si.noemus.boatguard.objects.Alarm;
import si.noemus.boatguard.objects.AppSetting;
import si.noemus.boatguard.objects.ObuComponent;
import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;


public class Settings {
	
	public static String SETTING_LANG = "LANG";
	public static String SETTING_USERNAME = "USERNAME";
	public static String SETTING_PASSWORD = "PASSWORD";
	public static String SETTING_REMEMBER_ME = "REMEMBER_ME";
	public static String SETTING_OBU_ID = "OBU_ID";
	public static String SETTING_REFRESH_TIME = "REFRESH_TIME";
	public static String SETTING_SERVER_NUM = "SERVER_NUM";
	public static String SETTING_THEME = "THEME";
	public static String SETTING_PLAY_SOUND = "PLAY_SOUND";
	public static String SETTING_VIBRATE = "VIBRATE";
	public static String SETTING_POP_UP = "POP_UP";

	public static String COMPONENT_TYPE_ACCU = "ACCU";
	public static String COMPONENT_TYPE_PUMP = "PUMP";
	public static String COMPONENT_TYPE_GEO = "GEO";
	public static String COMPONENT_TYPE_ANCHOR = "ANCHOR";

	public static String STATE_ROW_STATE = "ROW_STATE";
	public static String STATE_GEO_FENCE = "GEO_FENCE";
	public static String STATE_GEO_DISTANCE = "GEO_DISTANCE";
	public static String STATE_LAT = "LAT";
	public static String STATE_LON = "LON";
	public static String STATE_PUMP_STATE = "PUMP_STATE";
	public static String STATE_ACCU_NAPETOST = "ACCU_NAPETOST";
	public static String STATE_ACCU_AH = "ACCU_AH";
	public static String STATE_ACCU_TOK = "ACCU_TOK";
	public static String STATE_ACCU_DISCONNECT = "ACCU_DISCONNECT";
	public static String STATE_ANCHOR = "ANCHOR";
	public static String STATE_ANCHOR_DRIFTING = "ANCHOR_DRIFTING";
	
	public static String APP_STATE_GEO_FENCE_DISABLED = "GEO_FENCE_DISABLED";
	public static String APP_STATE_GEO_FENCE_ENABLED = "GEO_FENCE_ENABLED";
	public static String APP_STATE_GEO_FENCE_ALARM = "GEO_FENCE_ALARM";
	public static String APP_STATE_PUMP_OK = "PUMP_OK";
	public static String APP_STATE_PUMP_PUMPING = "PUMP_PUMPING";
	public static String APP_STATE_PUMP_CLODGED = "PUMP_CLODGED";
	public static String APP_STATE_PUMP_DEMAGED = "PUMP_DEMAGED";
	public static String APP_STATE_ANCHOR_DISABLED = "ANCHOR_DISABLED";
	public static String APP_STATE_ANCHOR_ENABLED = "ANCHOR_ENABLED";
	public static String APP_STATE_ANCHOR_DRIFTING = "ANCHOR_DRIFTING";
	public static String APP_STATE_BATTERY_ALARM_VALUE = "BATTERY_ALARM_VALUE";
	public static String APP_STATE_ACCU_DISCONNECT = "ACCU_DISCONNECT";
			
	public static String ALARM_GREEN = "G";
	public static String ALARM_RED = "R";
			
	public static HashMap<Integer,Alarm> alarms = new HashMap<Integer,Alarm>(){};
	public static HashMap<String,AppSetting> appSettings = new HashMap<String,AppSetting>(){};
	public static HashMap<String,State> states = new HashMap<String,State>(){};
	public static HashMap<Integer,ObuSetting> obuSettings = new HashMap<Integer,ObuSetting>(){};
	public static HashMap<Integer,ObuComponent> obuComponents = new HashMap<Integer,ObuComponent>(){};
			
	private static Gson gson = new Gson();																					
    
    public static void getSettings(final Context context) {
		String urlString = context.getString(R.string.server_url) + "getsettings";
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
		        	AsyncTask at = new Comm().execute(urlString, null); 
		            String res = (String) at.get();
		            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
		    	   	if (jRes.has("error") && !jRes.getString("error").equals("null")) {
		    	   	} else {
		    	   		JSONArray jsonAlarms = (JSONArray)jRes.get("alarms");
		    	   		alarms.clear();
		    	   		for (int i=0; i< jsonAlarms.length(); i++) {
		    	   			Alarm alarm = gson.fromJson(jsonAlarms.get(i).toString(), Alarm.class);
		    	   			//System.out.println(alarm.toString());
		    	   			alarms.put(alarm.getId(), alarm);
		    	   		}
		    	   		
		    	   		JSONArray jsonAppSettings = (JSONArray)jRes.get("app_settings");
		    	   		appSettings.clear();
		    	   		for (int i=0; i< jsonAppSettings.length(); i++) {
		    	   			AppSetting appSetting = gson.fromJson(jsonAppSettings.get(i).toString(), AppSetting.class);
		    	   			//System.out.println(appSetting.toString());
		    	   			appSettings.put(appSetting.getName(), appSetting);
		    	   			if (appSetting.getName().equals(SETTING_SERVER_NUM)) {
		    	   				Utils.savePrefernciesString(context, SETTING_SERVER_NUM, appSetting.getValue());
		    	   			}

		    	   		}

		    	   		JSONArray jsonStates = (JSONArray)jRes.get("states");
		    	   		states.clear();
		    	   		for (int i=0; i< jsonStates.length(); i++) {
		    	   			State state = gson.fromJson(jsonStates.get(i).toString(), State.class);
		    	   			//System.out.println(state.toString());
		    	   			states.put(state.getCode(), state);
		    	   		}
		    	   		
		    	   	}
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }
    
    public static void getObuSettings(Context context) {
    	String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getobusettings?obuid="+obuId;
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            JSONArray jsonObuSettings = (JSONArray)new JSONTokener(res).nextValue();
    	   		obuSettings.clear();
    	   		for (int i=0; i< jsonObuSettings.length(); i++) {
    	   			ObuSetting obuSetting = gson.fromJson(jsonObuSettings.get(i).toString(), ObuSetting.class);
    	   			//System.out.println(obuSetting.toString());
    	   			obuSettings.put(obuSetting.getId_setting(), obuSetting);
    	   			if (obuSetting.getCode().equals(SETTING_REFRESH_TIME)) {
    	   				Utils.savePrefernciesInt(context, SETTING_REFRESH_TIME, Integer.parseInt(obuSetting.getValue())*60*1000);
    	   			}
    	   				
    	   		}  	        
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }
    
    
    public static void getObuComponents(Context context) {
    	String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getobucomponents?obuid="+obuId;
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            JSONArray jsonObuComponents = (JSONArray)new JSONTokener(res).nextValue();
	            obuComponents.clear();
    	   		for (int i=0; i< jsonObuComponents.length(); i++) {
    	   			ObuComponent obuComponent = gson.fromJson(jsonObuComponents.get(i).toString(), ObuComponent.class);
    	   			//System.out.println(obuComponent.toString());
    	   			obuComponents.put(obuComponent.getId_component(), obuComponent);	
    	   		}
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }    
}











