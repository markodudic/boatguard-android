package si.noemus.boatguard.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import si.noemus.boatguard.R;
import si.noemus.boatguard.R.array;
import si.noemus.boatguard.R.id;
import si.noemus.boatguard.R.layout;
import si.noemus.boatguard.R.string;
import si.noemus.boatguard.activities.MainActivity;
import si.noemus.boatguard.activities.SplashScreenActivity;
import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

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
        String[] values = new String[settingsItems.length];
        
        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
        values[0] = geoFence.endsWith("1")?getResources().getString(R.string.on):getResources().getString(R.string.off);

        values[1] = "value";
        
        String anchorDrifting = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).getValue();
        values[2] = anchorDrifting.endsWith("1")?getResources().getString(R.string.on):getResources().getString(R.string.off);
        
        values[3] = "value";
        values[4] = "value";
        
        boolean playSound = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_PLAY_SOUND, false);
        boolean vibrate = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_VIBRATE, false);
        boolean popUp = Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_POP_UP, false);
		values[5] = (playSound?getResources().getString(R.string.play_sound):"") + " / " + 
					(vibrate?getResources().getString(R.string.vibrate):"") + " / " + 
					(popUp?getResources().getString(R.string.pop_up):"");
        
        values[6] = "value";
        values[7] = "";
        
        int refreshTime = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_REFRESH_TIME)/60/1000;
        int theme = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME);
        String lang = Utils.getPrefernciesString(getActivity(), Settings.SETTING_LANG);
        values[8] = refreshTime +
        			(theme == R.style.AppThemeDay?" / "+getResources().getString(R.string.day):" / "+getResources().getString(R.string.night)) + 
        			(SplashScreenActivity.languages.get(lang)!=null?" / "+SplashScreenActivity.languages.get(lang):"");  

        FragmentManager fragmentManager = getFragmentManager();
        for (int i=0; i<settingsItems.length; i++) {
        	SettingsItemFragment item = (SettingsItemFragment)fragmentManager.findFragmentByTag(settingsItems[i]);
        	item.setText(values[i]);
        }
        
    }    
 
}
