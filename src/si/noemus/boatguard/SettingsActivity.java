package si.noemus.boatguard;

import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
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
        GeoFenceFragment fragment = new GeoFenceFragment();
        fragmentTransaction.add(R.id.fragment_settings, fragment, extras.getString("title"));
        fragmentTransaction.commit();
        
		ImageView btnBack = (ImageView) findViewById(R.id.iv_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});  
		
		
	}

    public void onGeoFenceClicked(View view) {
        // Is the toggle on?
        boolean on = ((Switch) view).isChecked();
        
        if (on) {
            // Enable vibrate
        } else {
            // Disable vibrate
        }
    }    

}
