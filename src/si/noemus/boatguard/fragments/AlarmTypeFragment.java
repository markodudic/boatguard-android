package si.noemus.boatguard.fragments;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import si.noemus.boatguard.R;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.objects.ObuAlarm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

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

		
		int[] ids = new int[Settings.obuAlarms.size()];
		String[] titles = new String[Settings.obuAlarms.size()];
		boolean[] active = new boolean[Settings.obuAlarms.size()];
		boolean[] email = new boolean[Settings.obuAlarms.size()];
		boolean[] friends = new boolean[Settings.obuAlarms.size()];
		Set set = Settings.obuAlarms.entrySet(); 
		Iterator i = set.iterator();
		int ii = 0;
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuAlarm obuAlarm = (ObuAlarm)map.getValue();
			ids[ii] = obuAlarm.getId_alarm();
			titles[ii] = obuAlarm.getMessage_short().toUpperCase();
			active[ii] = obuAlarm.getActive()==1?true:false;
			email[ii] = obuAlarm.getSend_email()==1?true:false;
			friends[ii] = obuAlarm.getSend_friends()==1?true:false;
			ii++;
		}
	   	final ObuAlarmAdapter adapter = new ObuAlarmAdapter(getActivity(), titles, active, email, friends, ids);
   		ListView lvObuAlarms = (ListView)v.findViewById(R.id.lv_obu_alarms);
   		lvObuAlarms.setAdapter(adapter);
   		
   		return v;
    }
    

	
    public class ObuAlarmAdapter extends ArrayAdapter<String> {
    	private final Context context;
    	private final String[] titles;
    	private final boolean[] active;
    	private final boolean[] email;
    	private final boolean[] friends;
    	private final int[] ids;
    	  
		public ObuAlarmAdapter(Context context, String[] titles, boolean[] active, boolean[] email, boolean[] friends, int[] ids) {
		    super(context, R.layout.row_obu_alarm, titles);
		    this.context = context;
		    this.titles = titles;
		    this.active = active;
		    this.email = email;
		    this.friends = friends;
		    this.ids = ids;
		}
    	  
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.row_obu_alarm, parent, false);
			
			TextViewFont tvAlarmMessage = (TextViewFont)v.findViewById(R.id.tv_alarm_message_short);
			tvAlarmMessage.setText(titles[position]);

	   		final Switch alarmEnabled = (Switch)v.findViewById(R.id.switch_alarm_enabled);
			alarmEnabled.setChecked(active[position]);
			alarmEnabled.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	active[position] = alarmEnabled.isChecked();
	            	ObuAlarm obuAlarm = Settings.obuAlarms.get(ids[position]);
	            	obuAlarm.setActive(alarmEnabled.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        });  

	   		final Switch sendEmail = (Switch)v.findViewById(R.id.switch_send_email);
			sendEmail.setChecked(email[position]);
			sendEmail.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	email[position] = sendEmail.isChecked();
	            	ObuAlarm obuAlarm = Settings.obuAlarms.get(ids[position]);
	            	obuAlarm.setSend_email(sendEmail.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			final Switch alarmFriends = (Switch)v.findViewById(R.id.switch_alarm_friends);
			alarmFriends.setChecked(friends[position]);
			alarmFriends.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	friends[position] = alarmFriends.isChecked();
	            	ObuAlarm obuAlarm = Settings.obuAlarms.get(ids[position]);
	            	obuAlarm.setSend_friends(alarmFriends.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			return v;
		}
    }	
	
	
	
	
}
