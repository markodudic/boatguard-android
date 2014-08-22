package si.noemus.boatguard.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import si.noemus.boatguard.R;
import si.noemus.boatguard.R.id;
import si.noemus.boatguard.R.layout;
import si.noemus.boatguard.R.string;
import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Intent;
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

public class AlarmTypeFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_alarm_type, null);

        final Switch switchPlaySound = (Switch) v.findViewById(R.id.switch_play_sound);
		switchPlaySound.setChecked(Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_PLAY_SOUND, false));
		switchPlaySound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				Utils.savePrefernciesBoolean(getActivity(), Settings.SETTING_PLAY_SOUND, switchPlaySound.isChecked());
			}
		});	

		final Switch switchVibrate = (Switch) v.findViewById(R.id.switch_vibrate);
        switchVibrate.setChecked(Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_VIBRATE, false));
        switchVibrate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				Utils.savePrefernciesBoolean(getActivity(), Settings.SETTING_VIBRATE, switchVibrate.isChecked());
			}
		});	

		final Switch switchPopUp = (Switch) v.findViewById(R.id.switch_pop_up);
		switchPopUp.setChecked(Utils.getPrefernciesBoolean(getActivity(), Settings.SETTING_POP_UP, false));
		switchPopUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				Utils.savePrefernciesBoolean(getActivity(), Settings.SETTING_POP_UP, switchPopUp.isChecked());
			}
		});	
		
        return v;
    }
 
}
