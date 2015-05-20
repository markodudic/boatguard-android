package com.boatguard.boatguard.activities;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.Device;
import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.DialogFactory;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.gson.Gson;

public class LoginActivity extends Activity {

	@SuppressLint("NewApi")
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
        
   		String username = Utils.getPrefernciesString(LoginActivity.this, Settings.SETTING_USERNAME);
   		String password = Utils.getPrefernciesString(LoginActivity.this, Settings.SETTING_PASSWORD);
   		boolean remember = Utils.getPrefernciesBoolean(LoginActivity.this, Settings.SETTING_REMEMBER_ME, false);

   		
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

		final EditText etUsername = (EditText) findViewById(R.id.username);
		if (remember) etUsername.setText(username);
		ImageView ivUsername = (ImageView) findViewById(R.id.iv_username);
		ivUsername.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etUsername.setText("");
				etUsername.requestFocus();
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etUsername, InputMethodManager.SHOW_FORCED);
			} 
		});

		
		final EditText etPassword = (EditText) findViewById(R.id.password);
		if (remember) etPassword.setText(password);
		ImageView ivPassword = (ImageView) findViewById(R.id.iv_password);
		ivPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etPassword.setText("");
				etPassword.requestFocus();
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etPassword, InputMethodManager.SHOW_FORCED);
			} 
		});

		
		final EditText etObuid = (EditText) findViewById(R.id.obu_id);
		ImageView ivObuId = (ImageView) findViewById(R.id.iv_obu_id);
		ivObuId.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etObuid.setText("");
				etObuid.requestFocus();
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etObuid, InputMethodManager.SHOW_FORCED);
			} 
		});
		
		CheckBox cbRememberMe = (CheckBox) findViewById(R.id.checkBox_remember_me);
		cbRememberMe.setChecked(remember);
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
	        //PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
   		    //TelephonyManager mTelephonyMgr; 
	   	    //mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 

	    	String sessionid = Utils.getPrefernciesString(this, Settings.SETTING_SESSION_ID);
	        String urlString = LoginActivity.this.getString(R.string.server_url) + 
					"login?type="+type +
					"&username=" + Uri.encode(etUsername.getText().toString()) + 
					"&password=" + Uri.encode(etPassword.getText().toString()) + 
					"&obu_sn=" + Uri.encode(etObuid.getText().toString()) +
					"&sessionid="+sessionid; 
					/*"&app_version=" + URLEncoder.encode(pInfo.versionName) +
					"&device_name="+URLEncoder.encode(Build.MODEL)+
					"&device_platform="+Build.VERSION.SDK_INT+
					"&device_version="+URLEncoder.encode(Build.VERSION.RELEASE)+
					"&device_uuid="+URLEncoder.encode(Build.SERIAL);

			if (mTelephonyMgr!=null && mTelephonyMgr.getLine1Number()!=null && mTelephonyMgr.getLine1Number().length() > 0) {
				urlString += "&phone_number="+URLEncoder.encode(mTelephonyMgr.getLine1Number());
			}     */
			
	        if (Utils.isNetworkConnected(LoginActivity.this, true)) {
	        	AsyncTask at = new Comm().execute(urlString, null); 
	            String res = (String) at.get();
	            System.out.println(res);
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
					setDevice();
	    	   		String sessionId = (String)jRes.get("sessionId");
	    	   		//Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_SESSION_ID, sessionId);
	    	   		Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_SESSION_ID, getResources().getString(R.string.session_id));
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
	
    private void setDevice() {
		try {
			String gcm_registration_id = Utils.getPrefernciesString(LoginActivity.this, SplashScreenActivity.PROPERTY_REG_ID);
		    String obu_id = Utils.getPrefernciesString(LoginActivity.this, Settings.SETTING_OBU_ID);
		    Settings.getCustomer(this); 
	        if (gcm_registration_id != null && obu_id != null) {
		   		PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				TelephonyManager mTelephonyMgr;
				mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
				
				Device device = new Device();
				device.setId_obu(Integer.parseInt(obu_id));
				device.setId_customer(Settings.customer.getUid());
				device.setGcm_registration_id(gcm_registration_id);
				device.setPhone_model(Build.MODEL);
				device.setPhone_platform(Build.VERSION.SDK_INT+"");
				device.setPhone_platform_version(Build.VERSION.RELEASE);
				device.setPhone_uuid(Build.SERIAL);
				device.setApp_version(pInfo.versionName);
				
				if (mTelephonyMgr!=null && mTelephonyMgr.getLine1Number()!=null && mTelephonyMgr.getLine1Number().length() > 0) {
					device.setPhone_number(mTelephonyMgr.getLine1Number());
				}       
	        
			    Gson gson = new Gson();
			    String data = gson.toJson(device);
			    
		    	String sessionid = Utils.getPrefernciesString(this, Settings.SETTING_SESSION_ID);
			    String urlString = LoginActivity.this.getString(R.string.server_url) + "setdevice?sessionid="+sessionid;
			    if (Utils.isNetworkConnected(LoginActivity.this, true)) {
			    	AsyncTask at = new Comm().execute(urlString, "json", data); 
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
