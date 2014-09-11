package si.noemus.boatguard.fragments;

import si.noemus.boatguard.R;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.utils.Settings;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

public class AlarmContactsFragment  extends Fragment {
	protected LayoutInflater inflater;
    
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_alarm_contacts, null);
        this.inflater = inflater;

	   	final AlarmContactsAdapter adapter = new AlarmContactsAdapter();
   		ListView lvObuAlarms = (ListView)v.findViewById(R.id.lv_alarm_contacts);
   		lvObuAlarms.setAdapter(adapter);
   		
   		TextViewFont btnAddContact = (TextViewFont) v.findViewById(R.id.button_add_contact);
        btnAddContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {

			}
		});	

		return v;
    }
    

	
    public class AlarmContactsAdapter extends BaseAdapter {
    	  
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.row_alarm_contact, parent, false);
			
			TextViewFont tvContact = (TextViewFont)v.findViewById(R.id.tv_contact);
			tvContact.setText(Settings.friends.get(position).getName().toUpperCase() + " " + Settings.friends.get(position).getSurname().toUpperCase());
			TextViewFont tvNumberEmail = (TextViewFont)v.findViewById(R.id.tv_number_email);
			tvNumberEmail.setText(Settings.friends.get(position).getNumber() + " / " + Settings.friends.get(position).getEmail());

			ImageView ivContact = (ImageView) v.findViewById(R.id.iv_contact);
			ivContact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View vv) {
					Settings.friends.remove(position);
					notifyDataSetChanged();
					Settings.setFriends(getActivity());
				} 
			});
			
			return v;
		}
		@Override
		public int getCount() {
			return Settings.friends.size();
		}
		@Override
		public Object getItem(int position) {
			return Settings.friends.get(position);
		}
		@Override
		public long getItemId(int position) {
			return Settings.friends.get(position).getUid();
		}
    }	
	
	
	
	
}
