package si.noemus.boatguard;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getActionBar().setCustomView(R.layout.actionbar_text);
		
		TextView tvTitle = (TextView) findViewById(R.id.actionbar_text);
        tvTitle.setText(R.string.title_activity_login);
        
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
	}

	
	private void loginRegister(String type) {
		try {
			EditText etUsername = (EditText) findViewById(R.id.username);
	        EditText etPassword = (EditText) findViewById(R.id.password);
	        EditText etObuid = (EditText) findViewById(R.id.obu_id);
	        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

	        String urlString = LoginActivity.this.getString(R.string.server_url) + 
					"login?type="+type +
					"&username=" + etUsername.getText().toString() + 
					"&password=" + etPassword.getText().toString() + 
					"&obu_sn=" + etObuid.getText().toString() + 
					"&app_version=" + pInfo.versionName;
	        
	        AsyncTask at = new Comm().execute(urlString); 
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
    	   		Utils.savePrefernciesString(LoginActivity.this, Settings.SETTING_OBU_ID, etObuid.getText().toString());
    	   		Utils.savePrefernciesBoolean(LoginActivity.this, Settings.SETTING_REMEMBER_ME, cbRememberMe.isChecked());
    	   		Intent i = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(i);								    	   		
    	   	}

        } catch (Exception e) {
        	e.printStackTrace();
        	Toast toast = Toast.makeText(LoginActivity.this, getString(R.string.json_error), Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        	toast.show();
   		}
		
	} 

}
