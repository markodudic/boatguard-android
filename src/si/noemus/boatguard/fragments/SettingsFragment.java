package si.noemus.boatguard.fragments;

import java.util.HashMap;

import si.noemus.boatguard.R;
import si.noemus.boatguard.activities.SplashScreenActivity;
import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    	HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String[] text = new String[settingsItems.length];
        int[] textColours = new int[settingsItems.length];
        
        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
        if (geoFence.endsWith("1")) {
        	text[0] = getResources().getString(R.string.on);
        }
        else {
        	text[0] = getResources().getString(R.string.off);
            textColours[0]= R.color.alarm_red;
        }

        text[1] = "value";
        
        String anchorDrifting = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).getValue();
        if (anchorDrifting.endsWith("1")) {
        	text[2] = getResources().getString(R.string.on);
        }
        else {
        	text[2] = getResources().getString(R.string.off);
            textColours[2]= R.color.alarm_red;
        }
        
        text[3] = "value";
        
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
        
        text[6] = Settings.customer.getName().toUpperCase()+" "+Settings.customer.getSurname().toUpperCase()+" / "+Settings.customer.getBoat_name().toUpperCase();
        text[7] = "";
        
        int refreshTime = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_REFRESH_TIME)/60/1000;
        int theme = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME);
        String lang = Utils.getPrefernciesString(getActivity(), Settings.SETTING_LANG);
        text[8] = refreshTime +
        			(theme == R.style.AppThemeDay?" / "+getResources().getString(R.string.day):" / "+getResources().getString(R.string.night)) + 
        			(SplashScreenActivity.languages.get(lang)!=null?" / "+SplashScreenActivity.languages.get(lang):"");  

        FragmentManager fragmentManager = getFragmentManager();
        for (int i=0; i<settingsItems.length; i++) {
        	SettingsItemFragment item = (SettingsItemFragment)fragmentManager.findFragmentByTag(settingsItems[i]);
        	item.setText(text[i],textColours[i]);
        }
        
    }    
 
}
