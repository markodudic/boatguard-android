package com.boatguard.boatguard.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.SettingsActivity;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.Friend;
import com.boatguard.boatguard.utils.Settings;

public class ContactsFragment extends Fragment {
	protected LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_contacts, null);
        this.inflater = inflater;

	   	final ContactsAdapter adapter = new ContactsAdapter();
   		ListView lvContacts = (ListView)v.findViewById(R.id.lv_contacts);
   		lvContacts.setAdapter(adapter);

   		lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

   		  @Override
   		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
   			  Friend contact = (Friend) adapter.getItem(position);
   			  contact.setId_customer(Settings.customer.getUid());
   			  Settings.friends.add(contact);
   			  Settings.setFriends(getActivity());
  			  Intent i = new Intent(getActivity(), SettingsActivity.class);
   			  i.putExtra("id", 4);
   			  i.putExtra("title", getResources().getString(R.string.alarm_contacts));
   			  startActivity(i);
			}
   		});
   		
   		
		return v;
    }

    public class ContactsAdapter extends BaseAdapter {
  	  
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.row_alarm_contact, parent, false);
			
			TextViewFont tvContact = (TextViewFont)v.findViewById(R.id.tv_contact);
			Friend contact = Settings.contacts.get(position);
			tvContact.setText(contact.getName().toUpperCase());
			TextViewFont tvNumberEmail = (TextViewFont)v.findViewById(R.id.tv_number_email);
			tvNumberEmail.setText((contact.getNumber()!=null?contact.getNumber():"") + (contact.getEmail()!=null?" / " +contact.getEmail():""));
			
			((ImageView)v.findViewById(R.id.iv_contact)).setVisibility(View.GONE);
			
			return v;
		}
		@Override
		public int getCount() {
			return Settings.contacts.size();
		}
		@Override
		public Object getItem(int position) {
			return Settings.contacts.get(position);
		}
		@Override
		public long getItemId(int position) {
			return Settings.contacts.get(position).getUid();
		}
    }		
}