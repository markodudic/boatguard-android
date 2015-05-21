package com.boatguard.boatguard.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.SplashScreenActivity;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.flurry.android.FlurryAgent;

public class SettingsFragment  extends Fragment {
	
	private String[] settingsItems;
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_settings, null);

        settingsItems = getResources().getStringArray(R.array.settings_items);
 
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i=0; i<settingsItems.length; i++) {
        	SettingsItemFragment item = new SettingsItemFragment();
        	Bundle args = new Bundle();
            args.putInt("id", i);
            args.putString("title", settingsItems[i]);
            //args.putString("text", values[i]);
            item.setArguments(args);
        	fragmentTransaction.add(R.id.layout_settings, item, settingsItems[i]);
        }
        fragmentTransaction.commit();        
        
        
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume(); 
		FlurryAgent.logEvent("Settings");

    	HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String[] text = new String[settingsItems.length];
        int[] textColours = new int[settingsItems.length];
        
        if (Settings.states!=null && (State)Settings.states.get(Settings.STATE_GEO_FENCE) != null) {
	        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
	        if (geoFence.endsWith("1")) {
	        	text[0] = getResources().getString(R.string.on);
	            textColours[0]= R.color.text_green;
	        }
	        else {
	        	text[0] = getResources().getString(R.string.off);
	            textColours[0]= R.color.alarm_red;
	        }
        }

        String pumpAlarmAlways = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_ALWAYS)).getId()).getValue();
        String pumpAlarmShortPeriod = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_SHORT_PERIOD)).getId()).getValue();
        String pumpAlarmLongPeriod = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_PUMP_ALARM_LONG_PERIOD)).getId()).getValue();
        if (pumpAlarmAlways.endsWith("1")) {
        	text[1] = getResources().getString(R.string.on) + " / " + pumpAlarmShortPeriod + " / " + pumpAlarmLongPeriod;
            textColours[1]= R.color.text_green;
        }
        else {
        	text[1] = getResources().getString(R.string.off) + " / " + pumpAlarmShortPeriod + " / " + pumpAlarmLongPeriod;
            textColours[1]= R.color.alarm_red;
        }
        
        String anchorDrifting = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).getValue();
        if (anchorDrifting.endsWith("1")) {
        	text[2] = getResources().getString(R.string.on);
            textColours[2]= R.color.text_green;
        }
        else {
        	text[2] = getResources().getString(R.string.off);
            textColours[2]= R.color.alarm_red;
        }
        
        String batteryCapacity = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_CAPACITY)).getId()).getValue();
        String batteryAlarmLevel = obuSettings.get(((Setting)Settings.settings.get(Settings.STATE_BATTERY_ALARM_LEVEL)).getId()).getValue();
        text[3] = batteryCapacity + "Ah / " + batteryAlarmLevel + "%";
        
        String contacts = "";
        for (int i=0; i<Settings.friends.size(); i++) {
        	contacts += (contacts.length()>0?" / ":"") + Settings.friends.get(i).getName().toUpperCase() + " " + Settings.friends.get(i).getSurname().toUpperCase();
        }
        text[4] = contacts;
        
        boolean playSound = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_PLAY_SOUND, false);
        boolean vibrate = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_VIBRATE, false);
        boolean popUp = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_POP_UP, false);
        text[5] = (playSound?getResources().getString(R.string.play_sound):"") + (playSound&&vibrate?" / ":"") + 
					(vibrate?getResources().getString(R.string.vibrate):"") + (((playSound&&!vibrate)||vibrate)&&popUp?" / ":"") + 
					(popUp?getResources().getString(R.string.pop_up):"");
        
        text[6] = (Settings.customer.getName()!=null?Settings.customer.getName().toUpperCase():"")+" "+
        		(Settings.customer.getSurname()!=null?Settings.customer.getSurname().toUpperCase():"")+" / "+
        		(Settings.customer.getBoat_name()!=null?Settings.customer.getBoat_name().toUpperCase():"");
        text[7] = "";
        
        int refreshTime = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_REFRESH_TIME)/60/1000;
        int theme = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME);
        String lang = Utils.getPrefernciesString(getActivity(), Settings.SETTING_LANG);
        text[8] = refreshTime +
        			(theme == R.style.AppThemeDay?" / "+getResources().getString(R.string.day):" / "+getResources().getString(R.string.night)) + 
        			(SplashScreenActivity.languageCodes!=null&&SplashScreenActivity.languageCodes.indexOf(lang)!=-1?" / "+SplashScreenActivity.languages.get(SplashScreenActivity.languageCodes.indexOf(lang)):"");  

        FragmentManager fragmentManager = getFragmentManager();
        for (int i=0; i<settingsItems.length; i++) {
        	SettingsItemFragment item = (SettingsItemFragment)fragmentManager.findFragmentByTag(settingsItems[i]);
        	item.setText(text[i],textColours[i]);
        }
        
    }    
 
}
