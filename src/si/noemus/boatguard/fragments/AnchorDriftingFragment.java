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

public class AnchorDriftingFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_anchor_drifting, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String anchorDrifting = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).getValue();
        String anchorDriftingValue = obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId()).getValue();
        
        final TextView tvanchorDrifting = (TextView)v.findViewById(R.id.tv_anchor_drifting);
        tvanchorDrifting.setText(anchorDriftingValue + "m");
		Switch switchanchorDrifting = (Switch) v.findViewById(R.id.switch_anchor_drifting);
		switchanchorDrifting.setChecked(anchorDrifting.equals("1"));
        
		SeekBar seekbaranchorDrifting = (SeekBar) v.findViewById(R.id.seekbar_anchor_drifting);
		seekbaranchorDrifting.setProgress(Integer.parseInt(anchorDriftingValue));
		seekbaranchorDrifting.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				tvanchorDrifting.setText(progressChanged + "m");

			}
		});
		
		
		TextView tvDefine = (TextView) v.findViewById(R.id.button_anchor_drifting_define);
		tvDefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				Switch switchanchorDrifting = (Switch) v.findViewById(R.id.switch_anchor_drifting);
				SeekBar seekbaranchorDrifting = (SeekBar) v.findViewById(R.id.seekbar_anchor_drifting);
				
				//set settings
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()).setValue(switchanchorDrifting.isChecked()?"1":"0");
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId()).setValue(seekbaranchorDrifting.getProgress()+"");
		        
		        Settings.setObuSettings(getActivity());
		        
		        getActivity().finish();
			}
		});	

		
        return v;
    }
 
}
