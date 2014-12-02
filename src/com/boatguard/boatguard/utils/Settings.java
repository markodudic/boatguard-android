package com.boatguard.boatguard.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Gravity;
import android.widget.Toast;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.objects.Alarm;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.Customer;
import com.boatguard.boatguard.objects.Friend;
import com.boatguard.boatguard.objects.ObuAlarm;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.objects.State;
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
	public static String STATE_ACCU_EMPTY = "ACCU_EMPTY";
	public static String STATE_ANCHOR = "ANCHOR";
	public static String STATE_ANCHOR_DRIFTING = "ANCHOR_DRIFTING";
	public static String STATE_PUMP_ALARM_ALWAYS = "PUMP_ALARM_ALWAYS";
	public static String STATE_PUMP_ALARM_SHORT_PERIOD = "PUMP_ALARM_SHORT_PERIOD";
	public static String STATE_PUMP_ALARM_LONG_PERIOD = "PUMP_ALARM_LONG_PERIOD";
	public static String STATE_BATTERY_CAPACITY = "BATTERY_CAPACITY";
	public static String STATE_BATTERY_ALARM_LEVEL = "BATTERY_ALARM_LEVEL";
	public static String STATE_BATTERY_ENERGY_RESET = "BATTERY_ENERGY_RESET";
		
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
	public static String APP_STATE_ALARM_REFRESH_TIME = "ALARM_REFRESH_TIME";
	public static String APP_STATE_ALARM_BATTERY_EMPTY = "BATTERY_EMPTY";
			
	public static String ALARM_GREEN = "G";
	public static String ALARM_RED = "R";
			
	public static HashMap<Integer,Alarm> alarms = new HashMap<Integer,Alarm>(){};
	public static HashMap<String,AppSetting> appSettings = new HashMap<String,AppSetting>(){};
	public static HashMap<String,State> states = new HashMap<String,State>(){};
	public static HashMap<String,Setting> settings = new HashMap<String,Setting>(){};
	public static HashMap<Integer,ObuSetting> obuSettings = new HashMap<Integer,ObuSetting>(){};
	public static LinkedHashMap<Integer,ObuComponent> obuComponents = new LinkedHashMap<Integer,ObuComponent>(){};
	//public static HashMap<Integer,ObuAlarm> obuAlarms = new HashMap<Integer,ObuAlarm>(){};
	public static List<ObuAlarm> obuAlarms = new ArrayList<ObuAlarm>();
	
	public static Customer customer = new Customer();
	public static List<Friend> friends = new ArrayList<Friend>();
	public static List<Friend> contacts = new ArrayList<Friend>();
	
	public static int OBU_REFRESH_TIME = 5;
			
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

		    	   		JSONArray jsonSettings = (JSONArray)jRes.get("settings");
		    	   		settings.clear();
		    	   		for (int i=0; i< jsonSettings.length(); i++) {
		    	   			Setting setting = gson.fromJson(jsonSettings.get(i).toString(), Setting.class);
		    	   			//System.out.println(appSetting.toString());
		    	   			settings.put(setting.getName(), setting);
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
    	   				OBU_REFRESH_TIME = Integer.parseInt(obuSetting.getValue())*60*1000;
    	   				Utils.savePrefernciesInt(context, SETTING_REFRESH_TIME, OBU_REFRESH_TIME);
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
    
    public static Integer getObuSetting(String setting) {
    	Iterator<Entry<Integer, ObuSetting>> i = obuSettings.entrySet().iterator();
    	while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuSetting obuSetting = (ObuSetting) map.getValue();
			if (obuSetting.getCode().equals(setting)) {
				return (Integer) map.getKey();
			}
    	}
    	
    	return null;
    }
    
    
    public static void setObuSettings(Context context)
    {
	    List<ObuSetting> list = new ArrayList<ObuSetting>(obuSettings.values());
	    Gson gson = new Gson();
	    String data = gson.toJson(list);
	    
	    String urlString = context.getString(R.string.server_url) + "setobusettings";
	    if (Utils.isNetworkConnected(context, true)) {
	    	AsyncTask at = new Comm().execute(urlString, "json", data); 
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
    	   			if (obuComponent.getShow() != 0) {
    	   				obuComponents.put(obuComponent.getId_component(), obuComponent);
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
    
    public static void getObuAlarms(Context context) {
    	String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getobualarms?obuid="+obuId;
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            
	            JSONArray jsonObuAlarms = (JSONArray)new JSONTokener(res).nextValue();
	            obuAlarms.clear();
    	   		for (int i=0; i< jsonObuAlarms.length(); i++) {
    	   			ObuAlarm obuAlarm = gson.fromJson(jsonObuAlarms.get(i).toString(), ObuAlarm.class);
    	   			obuAlarms.add(obuAlarm);	
    	   		}
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }     
    
    public static void setObuAlarms(Context context)
    {
	    //List<ObuAlarm> list = new ArrayList<ObuAlarm>(obuAlarms.values());
	    Gson gson = new Gson();
	    String data = gson.toJson(obuAlarms);
	    
	    String urlString = context.getString(R.string.server_url) + "setobualarms";
	    if (Utils.isNetworkConnected(context, true)) {
	    	AsyncTask at = new Comm().execute(urlString, "json", data); 
	    }
    }  
    
    public static void getCustomer(Context context) {
    	String obuId = Utils.getPrefernciesString(context, Settings.SETTING_OBU_ID);
   		
    	String urlString = context.getString(R.string.server_url) + "getcustomer?obuid="+obuId;
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            customer = gson.fromJson(res, Customer.class);
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }

    public static void setCustomer(Context context)
    {
	    Gson gson = new Gson();
	    String data = gson.toJson(customer);
	    
	    String urlString = context.getString(R.string.server_url) + "setcustomer";
	    if (Utils.isNetworkConnected(context, true)) {
	    	AsyncTask at = new Comm().execute(urlString, "json", data); 
	    }
    }     

    
    public static void getFriends(Context context) {
    	String urlString = context.getString(R.string.server_url) + "getfriends?customerid="+customer.getUid();
    	if (Utils.isNetworkConnected(context, true)) {
  			try {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();

	            JSONArray jsonFriends = (JSONArray)new JSONTokener(res).nextValue();
	            friends.clear();
    	   		for (int i=0; i< jsonFriends.length(); i++) {
    	   			Friend friend = gson.fromJson(jsonFriends.get(i).toString(), Friend.class);
    	   			friends.add(friend);	
    	   		}
	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
    }

    public static void setFriends(Context context)
    {
	    Gson gson = new Gson();
	    String data = gson.toJson(friends);
	    
	    String urlString = context.getString(R.string.server_url) + "setfriends";
	    if (Utils.isNetworkConnected(context, true)) {
	    	AsyncTask at = new Comm().execute(urlString, "json", data); 
	    }
    }      
    
    public static void readContacts(Context context){
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME + " ASC");
        contacts.clear();
        	
        if (cur.getCount() > 0) {
           while (cur.moveToNext()) {
        	   Friend contact = new Friend();
        	   contact.setId_customer(customer.getUid());
        	   
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               if (name == null) continue;
               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            	   contact.setUid(Integer.parseInt(id));
                   contact.setName(name);
                   contact.setSurname("");
                   
                   Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                          new String[]{id}, null);
                   while (pCur.moveToNext()) {
                         String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         contact.setNumber(phone!=null?phone:"");
                   }
                   pCur.close();

                  Cursor emailCur = cr.query(
                           ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                           null,
                           ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                           new String[]{id}, null);
                   while (emailCur.moveToNext()) {
                       String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                       contact.setEmail(email!=null?email:"");
                   }
                   emailCur.close();

                   contacts.add(contact);
               }
          }
      }
   }

}











