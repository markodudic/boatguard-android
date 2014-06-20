package si.noemus.boatguard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.objects.ObuAlarm;
import si.noemus.boatguard.objects.ObuState;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity {
	
	private int initialPosition;
	private boolean refreshing = false;
    private TextView tvLastUpdate;
    private ImageView ivRefresh;
    private AnimationDrawable refreshAnimation;
    private Handler handler = new Handler();
    private List<Integer> activeAlarms = new ArrayList<Integer>();
    
	public static HashMap<Integer,ObuState> obuStates = new HashMap<Integer,ObuState>(){};
	public static HashMap<Integer,ObuAlarm> obuAlarms = new HashMap<Integer,ObuAlarm>(){};

	private static MainFragment mFragment;
	private static LocationFragment lFragment;
    
	private static Gson gson = new Gson();																					

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = Utils.getPrefernciesInt(this, Settings.SETTING_THEME);
		System.out.println("SET="+theme);
		if (theme != -1) {
			setTheme(theme);			
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_icon);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        
        tvLastUpdate = (TextView)findViewById(R.id.tv_last_update);
        ivRefresh = (ImageView)findViewById(R.id.iv_refresh);
        refreshAnimation = (AnimationDrawable) ivRefresh.getBackground();

        
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mFragment = (MainFragment) fm.findFragmentById(R.id.fragment_main);
        lFragment = (LocationFragment) fm.findFragmentById(R.id.fragment_location);
        ft.hide(lFragment);
        ft.commit();
        addShowHideListener(R.id.fragment_location, lFragment);
        addShowHideListener(R.id.fragment_main, mFragment);
        
        
        ImageView ivSettings = (ImageView)findViewById(R.id.iv_settings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				int theme = Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_THEME);
				System.out.println("CURR="+theme);
				if (theme == R.style.AppThemeDay) {
					Utils.savePrefernciesInt(MainActivity.this, Settings.SETTING_THEME, R.style.AppThemeNight);
				} else {
					Utils.savePrefernciesInt(MainActivity.this, Settings.SETTING_THEME, R.style.AppThemeDay);					
				}
				finish();
				startActivity(getIntent());
			}
		});


        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_main);
        sv.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View v, MotionEvent event) {
        	    switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL:
                	break;
                case MotionEvent.ACTION_MOVE:
             	    final float scale = MainActivity.this.getResources().getDisplayMetrics().density;
                    int px = (int) (MainActivity.this.getResources().getDimension(R.dimen.menu_height) * scale + 0.5f);
                    LinearLayout lMenu = (LinearLayout)findViewById(R.id.layout_menu);
             	   	TranslateAnimation anim = null;
                	int newPosition = sv.getScrollY();
                	if (newPosition > initialPosition) {
                		//System.out.println("up="+lMenu.getY()+":"+px);
             	   		anim=new TranslateAnimation(0,0,0,px);
                	} else if ((newPosition < initialPosition) || (newPosition==0 && initialPosition==0)) {
             	   		anim=new TranslateAnimation(0,0,200,0);
             	   		if (newPosition == 0 && !refreshing) {
                			//System.out.println("refresh");
                			getObudata();
                		}
             	   	}
                	if (anim!=null) {
	                	anim.setDuration(1000);
	             	    anim.setFillAfter(true);
	             	    lMenu.setAnimation(anim);
                	}
             	    initialPosition = sv.getScrollY();
                    break;
                case MotionEvent.ACTION_DOWN:
                	break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
                }
                return false;
            }
        });       
        
        Settings.getSettings(this);        
        Settings.getObuSettings(this);  
   		handler.postDelayed(startRefresh, 1000);
        
	}

	
    void addShowHideListener(int buttonId, final Fragment fragment) {
        ImageView ivHome = (ImageView)findViewById(R.id.iv_home);
        ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				System.out.println("HOME");
				FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(mFragment);
                ft.hide(lFragment);
                ft.commit();
            }
		});
		ImageView ivLocation = (ImageView)findViewById(R.id.iv_loction);
		ivLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(lFragment);
                ft.hide(mFragment);
                ft.commit();
			}
		});
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    public void getObudata() {
		refreshing = true;
    	String obuId = Utils.getPrefernciesString(this, Settings.SETTING_OBU_ID);
   		
    	String urlString = this.getString(R.string.server_url) + "getdata?obuid="+obuId;
    	if (Utils.isNetworkConnected(this)) {
  			try {
  				tvLastUpdate.setVisibility(View.GONE);	
    			ivRefresh.setVisibility(View.VISIBLE);	
    			refreshAnimation.start();
    			
    			AsyncTask at = new Comm().execute(urlString); 
	            String res = (String) at.get();
	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	obuStates.clear();
    	   		for (int i=0; i< jsonStates.length(); i++) {
    	   			ObuState obuState = gson.fromJson(jsonStates.get(i).toString(), ObuState.class);
    	   			System.out.println(obuState.toString());
    	   			obuStates.put(obuState.getId_state(), obuState);
    	   		}
	    	   	
	    	   	JSONArray jsonAlarms = (JSONArray)jRes.get("alarms");
	    	   	obuAlarms.clear();
	    	   	for (int i=0; i< jsonAlarms.length(); i++) {
    	   			ObuAlarm obuAlarm = gson.fromJson(jsonAlarms.get(i).toString(), ObuAlarm.class);
    	   			System.out.println(obuAlarm.toString());
    	   			obuAlarms.put(obuAlarm.getId_alarm(), obuAlarm);
    	   			if (activeAlarms.indexOf(obuAlarm.getId_alarm()) == -1) {
    	   				showNotification(obuAlarm.getId_alarm(), obuAlarm.getTitle(), obuAlarm.getMessage(), obuAlarm.getDate_alarm());
    	   				activeAlarms.add(obuAlarm.getId_alarm());
    	   			}
    	   		}
    	   		

	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(this, this.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
   		handler.postDelayed(endRefresh, 1000);
    }	
    
    
	private void showNotification(int id, String title, String message, Timestamp date){
		Intent resultIntent = new Intent(this, MainActivity.class);
		
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.notification)
		        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_icon))
		        .setContentTitle(title)
		        .setContentText(message)
		        .setAutoCancel(true)
		        .setContentIntent(pIntent)
		        .setWhen(date.getTime());
		
		NotificationManager mNotificationManager =  (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.build());
		
		beepVibrate();
	}
	
	public void beepVibrate() {
	    try {
	        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	        r.play();
	    } catch (Exception e) {}
	    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
	}
	
    private Runnable startRefresh = new Runnable() {
	   @Override
	   public void run() {
		   getObudata();
		   handler.postDelayed(startRefresh, Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_REFRESH_TIME));
	   }
	};
		
	private Runnable endRefresh = new Runnable() {
	   @Override
	   public void run() {
			tvLastUpdate.setVisibility(View.VISIBLE);	
			ivRefresh.setVisibility(View.GONE);	
			refreshAnimation.stop();
	   		refreshing = false;
	   }
	};	    

	


}
