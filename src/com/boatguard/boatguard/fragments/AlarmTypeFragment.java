package com.boatguard.boatguard.fragments;

import com.boatguard.boatguard.R;

import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.ObuAlarm;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;

public class AlarmTypeFragment  extends Fragment {
	protected LayoutInflater inflater;
    
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_alarm_type, null);
        this.inflater = inflater;

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

		ObuAlarmAdapter adapter = new ObuAlarmAdapter();
   		ListView lvObuAlarms = (ListView)v.findViewById(R.id.lv_obu_alarms);
   		lvObuAlarms.setAdapter(adapter);
   		
   		return v;
    }
    

	
    public class ObuAlarmAdapter extends BaseAdapter {
    	  
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.row_obu_alarm, parent, false);
			
			TextViewFont tvAlarmMessage = (TextViewFont)v.findViewById(R.id.tv_alarm_message_short);
			tvAlarmMessage.setText(((ObuAlarm)getItem(position)).getMessage_short().toUpperCase());
			tvAlarmMessage.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
			
	   		final Switch alarmEnabled = (Switch)v.findViewById(R.id.switch_alarm_enabled);
			alarmEnabled.setChecked(Settings.obuAlarms.get(position).getActive()==1);
			alarmEnabled.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	((ObuAlarm)getItem(position)).setActive(alarmEnabled.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        });  

	   		final Switch sendEmail = (Switch)v.findViewById(R.id.switch_send_email);
			sendEmail.setChecked(Settings.obuAlarms.get(position).getSend_email()==1);
			sendEmail.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	((ObuAlarm)getItem(position)).setSend_email(sendEmail.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			final Switch alarmFriends = (Switch)v.findViewById(R.id.switch_alarm_friends);
			alarmFriends.setChecked(Settings.obuAlarms.get(position).getSend_friends()==1);
			alarmFriends.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	((ObuAlarm)getItem(position)).setSend_friends(alarmFriends.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			return v;
		}
		@Override
		public int getCount() {
			return Settings.obuAlarms.size();
		}
		@Override
		public Object getItem(int position) {
			return Settings.obuAlarms.get(position);
		}
		@Override
		public long getItemId(int position) {
			return Settings.obuAlarms.get(position).getId_alarm();
		}		
    }	
	
	
	
	
}
