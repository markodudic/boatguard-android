package si.noemus.boatguard.activities;

import si.noemus.boatguard.R;
import si.noemus.boatguard.fragments.AlarmTypeFragment;
import si.noemus.boatguard.fragments.AnchorDriftingFragment;
import si.noemus.boatguard.fragments.GeoFenceFragment;
import si.noemus.boatguard.fragments.SettingsFragment;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends Activity {

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

		TextView tvTitle = (TextView) findViewById(R.id.actionbar_text);
        tvTitle.setText(extras.getString("title"));
 
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
    	System.out.println("id="+extras.getInt("id"));
        switch (extras.getInt("id")) {
	        case -1:
	            fragmentTransaction.add(R.id.fragment_settings, new SettingsFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 0:
	            fragmentTransaction.add(R.id.fragment_settings, new GeoFenceFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 2:
	            fragmentTransaction.add(R.id.fragment_settings, new AnchorDriftingFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 5:
	            fragmentTransaction.add(R.id.fragment_settings, new AlarmTypeFragment(), extras.getString("title"));
	            fragmentTransaction.commit();
	        	break;
	        case 8:
		   		if (theme == R.style.AppThemeDay) {
					Utils.savePrefernciesInt(this, Settings.SETTING_THEME, R.style.AppThemeNight);
				} else {
					Utils.savePrefernciesInt(this, Settings.SETTING_THEME, R.style.AppThemeDay);					
				}
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				
				//finish();
				//startActivity(getIntent());    	
				break;
        }
                
		ImageView btnBack = (ImageView) findViewById(R.id.iv_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});  
		

	}  

}
