package com.boatguard.boatguard.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.fragments.AlarmContactsFragment;
import com.boatguard.boatguard.fragments.AlarmTypeFragment;
import com.boatguard.boatguard.fragments.AnchorDriftingFragment;
import com.boatguard.boatguard.fragments.AppAppearanceFragment;
import com.boatguard.boatguard.fragments.BatteryFragment;
import com.boatguard.boatguard.fragments.BilgePumpFragment;
import com.boatguard.boatguard.fragments.ContactsFragment;
import com.boatguard.boatguard.fragments.ExtFragment;
import com.boatguard.boatguard.fragments.GeoFenceFragment;
import com.boatguard.boatguard.fragments.HistoryFragment;
import com.boatguard.boatguard.fragments.MyAccountFragment;
import com.boatguard.boatguard.fragments.SensorsFragment;
import com.boatguard.boatguard.fragments.SettingsFragment;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;

public class SettingsActivity extends Activity {

	int fragmentId = -9;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = Utils.getPrefernciesInt(this, Settings.SETTING_THEME);
		if (theme != -1) {
			setTheme(theme);			
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_back);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.actionbar_text);
		
		Bundle extras = getIntent().getExtras();

		TextViewFont tvTitle = (TextViewFont) findViewById(R.id.actionbar_text);
        tvTitle.setText(extras.getString("title"));
        tvTitle.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_big));
         
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentId = extras.getInt("id");

        switch (fragmentId) {
	        case -2:
	            fragmentTransaction.add(R.id.fragment_settings, new ContactsFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case -1:
	            fragmentTransaction.add(R.id.fragment_settings, new SettingsFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 0:
	            fragmentTransaction.add(R.id.fragment_settings, new GeoFenceFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 1:
	            fragmentTransaction.add(R.id.fragment_settings, new BilgePumpFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 2:
	            fragmentTransaction.add(R.id.fragment_settings, new AnchorDriftingFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 3:
	            fragmentTransaction.add(R.id.fragment_settings, new BatteryFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 4:
	            fragmentTransaction.add(R.id.fragment_settings, new AlarmContactsFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 5:
	            fragmentTransaction.add(R.id.fragment_settings, new AlarmTypeFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 6:
	            fragmentTransaction.add(R.id.fragment_settings, new MyAccountFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
				break;
	        case 7:
	            fragmentTransaction.add(R.id.fragment_settings, new HistoryFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
				break;
	        case 8:
	            fragmentTransaction.add(R.id.fragment_settings, new AppAppearanceFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
				break;
	        case 9:
    	   		Utils.savePrefernciesBoolean(SettingsActivity.this, Settings.SETTING_REMEMBER_ME, false);
	   			Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
	   			startActivity(i);						
	   			finish();
				break;
	        case 10:
	        	Bundle bundleLight = new Bundle();
	        	bundleLight.putString("type", Settings.STATE_LIGHT);
	        	bundleLight.putStringArray("sensors", Settings.obuLights);
	        	SensorsFragment fragLight = new SensorsFragment();
	        	fragLight.setArguments(bundleLight);
	        	
	            fragmentTransaction.add(R.id.fragment_settings, fragLight, extras.getString("title"));
	            fragmentTransaction.commit();
				break;
	        case 11:
	        	Bundle bundleFan = new Bundle();
	        	bundleFan.putString("type", Settings.STATE_FAN);
	        	bundleFan.putStringArray("sensors", Settings.obuFans);
	        	SensorsFragment fragFan = new SensorsFragment();
	        	fragFan.setArguments(bundleFan);
	        	
	        	fragmentTransaction.add(R.id.fragment_settings, fragFan, extras.getString("title"));
	            fragmentTransaction.commit();
				break;
	        case 12:
	        	Bundle bundleExt = new Bundle();
	        	bundleExt.putString("name", extras.getString("title"));
	        	bundleExt.putString("type", extras.getString("type"));
	        	ExtFragment extFragment = new ExtFragment();
	        	extFragment.setArguments(bundleExt);
	        	
	        	fragmentTransaction.add(R.id.fragment_settings, extFragment, extras.getString("title"));
	            fragmentTransaction.commit();
				break;
		}
                
        FrameLayout btnBack = (FrameLayout) findViewById(R.id.fl_action_bar);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	if ((fragmentId == 0) || (fragmentId == 1) || (fragmentId == 2) || (fragmentId == 3)) {
		    		new Settings(SettingsActivity.this).setObuSettings();
		    		finish();
		    	}
		    	else if (fragmentId == -1) {
					Intent i = new Intent(SettingsActivity.this, MainActivity.class);
					startActivity(i);
		    	}
		    	else if (fragmentId == 4) {
					Intent i = new Intent(SettingsActivity.this, SettingsActivity.class);
					i.putExtra("id", -1);
					i.putExtra("title", getResources().getString(R.string.menu));
					startActivity(i);
		    	}
		    	else if (fragmentId == 12) {
		    		new Settings(SettingsActivity.this).setObuComponents();
		    		finish();
		    	}
		    	else {
					finish();
		    	}
			} 
		});  
		
	}  

}
