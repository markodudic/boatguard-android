package com.boatguard.boatguard.fragments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.ObuAlarm;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;

public class ExtFragment  extends Fragment {
	protected LayoutInflater inflater;
	String type = null;
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_ext, null);
        this.inflater = inflater;
        
        type = getArguments().getString("type");    
        
		final EditText etName = (EditText) v.findViewById(R.id.extname);
		etName.setText(getArguments().getString("name"));
		etName.addTextChangedListener(new TextWatcher(){
		    public void afterTextChanged(Editable s) {
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(((Setting)Settings.settings.get(type)).getId()).setValue(etName.getText().toString());
		    }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		    public void onTextChanged(CharSequence s, int start, int before, int count){}
		}); 
		ImageView ivName = (ImageView) v.findViewById(R.id.iv_extname);
		ivName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etName.setText("");
				etName.requestFocus();
			} 
		});

		
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
  	  
		@SuppressLint("NewApi")
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.row_obu_alarm, parent, false);
			final ObuAlarm obuAlarm = (ObuAlarm)getItem(position);
			
			TextViewFont tvAlarmMessage = (TextViewFont)v.findViewById(R.id.tv_alarm_message_short);
			tvAlarmMessage.setText(obuAlarm.getMessage_short().toUpperCase());
			tvAlarmMessage.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
			
	   		final Switch alarmEnabled = (Switch)v.findViewById(R.id.switch_alarm_enabled);
			alarmEnabled.setChecked(obuAlarm.getActive()==1);
			alarmEnabled.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	obuAlarm.setActive(alarmEnabled.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        });  

	   		final Switch sendEmail = (Switch)v.findViewById(R.id.switch_send_email);
			sendEmail.setChecked(obuAlarm.getSend_email()==1);
			sendEmail.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	obuAlarm.setSend_email(sendEmail.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			final Switch alarmFriends = (Switch)v.findViewById(R.id.switch_alarm_friends);
			alarmFriends.setChecked(obuAlarm.getSend_friends()==1);
			alarmFriends.setOnClickListener(new AdapterView.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	obuAlarm.setSend_friends(alarmFriends.isChecked()?1:0);
	            	Settings.setObuAlarms(getActivity());
	            }
	        }); 
			
			return v;
		}
		@Override
		public int getCount() {
			return 2;
		}
		@Override
		public Object getItem(int position) {
			int id = ((Setting)Settings.settings.get(type)).getId();
			List<ObuAlarm> obuAlarms = Settings.obuAlarms;
			for(int i=0; i<obuAlarms.size(); i++) { 
				ObuAlarm obuAlarm = obuAlarms.get(i);
				if ((position == 0 && obuAlarm.getId_alarm() == id) || (position == 1 && obuAlarm.getId_alarm() == (id+1))) {
					return obuAlarm;
				}
			}
			
			return null;
		}
		@Override
		public long getItemId(int position) {
			int id = ((Setting)Settings.settings.get(type)).getId();
			List<ObuAlarm> obuAlarms = Settings.obuAlarms;
			for(int i=0; i<obuAlarms.size(); i++) { 
				ObuAlarm obuAlarm = obuAlarms.get(i);
				if ((position == 0 && obuAlarm.getId_alarm() == id) || (position == 1 && obuAlarm.getId_alarm() == (id+1))) {
					return obuAlarm.getId_alarm();
				}
			}
			
			return -1;
		}		
    }	
	

    
}
