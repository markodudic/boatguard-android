package si.noemus.boatguard.activities;

import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.R;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.DialogFactory;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = Utils.getPrefernciesInt(this, Settings.SETTING_THEME);
		System.out.println("SET="+theme);
		if (theme != -1) {
			setTheme(theme);			
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final ActionBar actionBar = getActionBar();

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.actionbar_text);
		
		TextViewFont tvTitle = (TextViewFont) findViewById(R.id.actionbar_text);
        tvTitle.setText(R.string.title_activity_login);
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.GONE);
        tvTitle.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_big));
        
        TextView btnRegister = (TextView) findViewById(R.id.button_register);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				loginRegister("register");
			}
		});
		
		TextView btnLogin = (TextView) findViewById(R.id.button_login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginRegister("login");
			} 
		});

		ImageView ivUsername = (ImageView) findViewById(R.id.iv_username);
		ivUsername.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) findViewById(R.id.username)).setText("");
			} 
		});

		
		ImageView ivPassword = (ImageView) findViewById(R.id.iv_password);
		ivPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) findViewById(R.id.password)).setText("");
			} 
		});

		
		ImageView ivObuId = (ImageView) findViewById(R.id.iv_obu_id);
		ivObuId.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) findViewById(R.id.obu_id)).setText("");
			} 
		});
		
		CheckBox cbRememberMe = (CheckBox) findViewById(R.id.checkBox_remember_me);
		if (Utils.getPrefernciesInt(LoginActivity.this, Settings.SETTING_THEME) == R.style.AppThemeDay) {
			cbRememberMe.setButtonDrawable(R.drawable.btn_check_holo_light);
		}
		else {
			cbRememberMe.setButtonDrawable(R.drawable.btn_check_holo_light_day);			
		}
	}

	
	private void loginRegister(String type) {
		try {
			EditText etUsername = (EditText) findViewById(R.id.username);
	        EditText etPassword = (EditText) findViewById(R.id.password);
	        EditText etObuid = (EditText) findViewById(R.id.obu_id);
	        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
   		    TelephonyManager mTelephonyMgr; 
	   	    mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 

	        String urlString = LoginActivity.this.getString(R.string.server_url) + 
					"login?type="+type +
					"&username=" + etUsername.getText().toString() + 
					"&password=" + etPassword.getText().toString() + 
					"&obu_sn=" + etObuid.getText().toString() + 
					"&app_version=" + URLEncoder.encode(pInfo.versionName) +
					"&device_name="+URLEncoder.encode(Build.MODEL)+
					"&device_platform="+Build.VERSION.SDK_INT+
					"&device_version="+URLEncoder.encode(Build.VERSION.RELEASE)+
					"&device_uuid="+URLEncoder.encode(Build.SERIAL);

			if (mTelephonyMgr!=null && mTelephonyMgr.getLine1Number()!=null && mTelephonyMgr.getLine1Number().length() > 0) {
				urlString += "&phone_number="+URLEncoder.encode(mTelephonyMgr.getLine1Number());
			}     
			
	        if (Utils.isNetworkConnected(LoginActivity.this, true)) {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	if (jRes.has("error") && !jRes.getString("error").equals("null")) {
	    	   		String msg = ((JSONObject)jRes.get("error")).getString("msg");
	    	   		String name = ((JSONObject)jRes.get("error")).getString("name");
	    	   		DialogFactory.getInstance().displayWarning(LoginActivity.this, name, msg, false);
	    	   	} else {
	    	   		CheckBox cbRememberMe = (CheckBox) findViewById(R.id.checkBox_remember_me);
			        Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_USERNAME, etUsername.getText().toString());
	    	   		Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_PASSWORD, etPassword.getText().toString());
	    	   		String uid = ((JSONObject)jRes.get("obu")).getString("uid");
	    	   		Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_OBU_ID, uid);
	    	   		Utils.savePrefernciesBoolean(LoginActivity.this, Settings.SETTING_REMEMBER_ME, cbRememberMe.isChecked());
	    	   		Intent i = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(i);								    	   		
	    	   	}
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        	Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.json_error), Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        	toast.show();
   		}
		
	} 

}
