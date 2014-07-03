package si.noemus.boatguard;

import java.util.HashMap;

import si.noemus.boatguard.objects.ObuSetting;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

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
		switchGeoFence.setChecked(Integer.parseInt(geoFence) == 1);
        
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
				boolean checked = switchGeoFence.isChecked();
				SeekBar seekbarGeoFence = (SeekBar) v.findViewById(R.id.seekbar_geo_fence);
				int progress = seekbarGeoFence.getProgress();
				System.out.println(checked+":"+progress);
				
				//set settings
				
				Settings.getObuSettings(getActivity());
			}
		});	

		
        return v;
    }
 
}
