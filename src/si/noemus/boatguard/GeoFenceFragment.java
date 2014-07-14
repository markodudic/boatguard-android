package si.noemus.boatguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class GeoFenceFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_geofence, null);

        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
        String geoFence = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).getValue();
        String geoFenceValue = obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).getValue();
        
        final TextView tvGeoFence = (TextView)v.findViewById(R.id.tv_geo_fence);
        tvGeoFence.setText(geoFenceValue + "m");
		Switch switchGeoFence = (Switch) v.findViewById(R.id.switch_geo_fence);
		switchGeoFence.setChecked(geoFence.equals("1"));
        
		SeekBar seekbarGeoFence = (SeekBar) v.findViewById(R.id.seekbar_geo_fence);
		seekbarGeoFence.setProgress(Integer.parseInt(geoFenceValue));
		seekbarGeoFence.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				tvGeoFence.setText(progressChanged + "m");

			}
		});
		
		
		TextView tvDefine = (TextView) v.findViewById(R.id.button_geo_fence_define);
		tvDefine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				Switch switchGeoFence = (Switch) v.findViewById(R.id.switch_geo_fence);
				SeekBar seekbarGeoFence = (SeekBar) v.findViewById(R.id.seekbar_geo_fence);
				
				//set settings
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()).setValue(switchGeoFence.isChecked()?"1":"0");
		        obuSettings.get(((State)Settings.states.get(Settings.STATE_GEO_DISTANCE)).getId()).setValue(seekbarGeoFence.getProgress()+"");
		        
		        List<ObuSetting> list = new ArrayList<ObuSetting>(obuSettings.values());
		        Gson gson = new Gson();
		        String data = gson.toJson(list);
		        
		        String obuId = Utils.getPrefernciesString(getActivity(), Settings.SETTING_OBU_ID);
		        
		       	String urlString = getActivity().getString(R.string.server_url) + "setobusettings?obuid="+obuId;
		        if (Utils.isNetworkConnected(getActivity(), true)) {
		        	AsyncTask at = new Comm().execute(urlString, "json", data); 
		        }
		        
		        getActivity().finish();
			}
		});	

		
        return v;
    }
 
}
