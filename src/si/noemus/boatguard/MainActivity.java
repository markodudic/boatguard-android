package si.noemus.boatguard;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.objects.AppSetting;
import si.noemus.boatguard.objects.ObuAlarm;
import si.noemus.boatguard.objects.ObuComponent;
import si.noemus.boatguard.objects.ObuState;
import si.noemus.boatguard.objects.State;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
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
	//private static LocationFragment lFragment;
    
	private static Gson gson = new Gson();	
	
	private int accuStep = 0;
	private int accuComponentId = 0;

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
        //lFragment = (LocationFragment) fm.findFragmentById(R.id.fragment_location);
        //ft.hide(lFragment);
        ft.commit();
        //addShowHideListener(R.id.fragment_location, lFragment);
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
        Settings.getObuComponents(this);  
        showObuComponents();
   		handler.postDelayed(startRefresh, 1000);
        
	}

	private void showObuComponents(){
        LinearLayout lComponents = (LinearLayout)findViewById(R.id.components);
      
        /*TypedArray a1 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.text_input});     
        int backgroundId = a1.getResourceId(0, 0);       
 
        TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.background});     
        int backgroundLineId = a2.getResourceId(0, 0);       
*/
        HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		Set set = obuComponents.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			System.out.println(map.getValue());
			ObuComponent obuComponent = (ObuComponent)map.getValue();
			if (obuComponent.getShow() == 0) continue;
			
			int lc = R.layout.list_component;
			if (obuComponent.getType().equals("ACCU")) { 
				lc = R.layout.list_component_accu;
			}

			
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View component = inflater.inflate(lc, null);
		    component.setId(obuComponent.getId_component());
			
		    LinearLayout l = (LinearLayout)((LinearLayout)component).getChildAt(0);
		    TextView textView = (TextView)(l).getChildAt(0);
			textView.setText(obuComponent.getName());
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				LinearLayout ll = (LinearLayout)(l).getChildAt(1);
			    ImageView imageView = (ImageView)(ll).getChildAt(0);
				imageView.setImageResource(R.drawable.ic_geofence_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
				LinearLayout ll = (LinearLayout)(l).getChildAt(1);
			    ImageView imageView = (ImageView)(ll).getChildAt(0);
				imageView.setImageResource(R.drawable.ic_bilgepump);				
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
				LinearLayout ll = (LinearLayout)(l).getChildAt(1);
			    ImageView imageView = (ImageView)(ll).getChildAt(0);
				imageView.setImageResource(R.drawable.ic_anchor_not_active);				
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ACCU)) { 
				FrameLayout ll = (FrameLayout)(l).getChildAt(1);
				TextView tvAccu = (TextView)(ll).getChildAt(0);
				tvAccu.setText("");	
				accuComponentId = obuComponent.getId_component();
			} 
				
			//component.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(this, (int)getResources().getDimension(R.dimen.component_height))));
			lComponents.addView(component);
				
			/*LinearLayout component = new LinearLayout(this);
			component.setId((Integer)obuComponent.getKey());
			component.setBackgroundColor(this.getResources().getColor(backgroundId));
			component.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(this, (int)getResources().getDimension(R.dimen.component_height))));
			lComponents.addView(component);		

			LinearLayout line = new LinearLayout(this);
			line.setBackgroundColor(this.getResources().getColor(backgroundLineId));
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.dpToPx(this, (int)getResources().getDimension(R.dimen.line_height)));
			layoutParams.setMargins(60, 0, 60, 0);
			lComponents.addView(line, layoutParams);	*/		
		}
		
	}
	
	
    void addShowHideListener(int buttonId, final Fragment fragment) {
        ImageView ivHome = (ImageView)findViewById(R.id.iv_home);
        ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				System.out.println("HOME");
				FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(mFragment);
                //ft.hide(lFragment);
                ft.commit();
            }
		});
		ImageView ivLocation = (ImageView)findViewById(R.id.iv_loction);
		ivLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.show(lFragment);
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
    	   		
	    	   	showObuData();

	        } catch (Exception e) {
   	        	e.printStackTrace();
   	        	Toast toast = Toast.makeText(this, this.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();
   	   		}
    	}
   		handler.postDelayed(endRefresh, 1000);
    }	
    
	@SuppressWarnings("deprecation")
	private void showObuData(){
        Set set = obuStates.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			System.out.println(map.getValue());
			ObuState obuState = (ObuState)map.getValue();
			int idState = obuState.getId_state();
			
			if (idState == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
            	tvLastUpdate.setText(getResources().getString(R.string.last_update) + " " + Utils.formatDate(obuState.getDateState()));
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()) { 
				LinearLayout component = (LinearLayout)findViewById(idState);
				LinearLayout ll = (LinearLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);
			    ImageView imageView = (ImageView)(ll).getChildAt(0);
				String geofence = obuState.getValue();
				
				if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_DISABLED)).getValue())) {
					imageView.setImageResource(R.drawable.ic_geofence_disabled);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ENABLED)).getValue())) {
					imageView.setImageResource(R.drawable.ic_geofence_home);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ALARM)).getValue())) {
					imageView.setImageResource(R.drawable.ic_geofence_alarm_1);
				}
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
				LinearLayout component = (LinearLayout)findViewById(idState);
				LinearLayout ll = (LinearLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);
			    ImageView imageView = (ImageView)(ll).getChildAt(0);
				String pumpState = obuState.getValue();
				
				if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_OK)).getValue())) {
					imageView.setImageResource(R.drawable.ic_bilgepump);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
					imageView.setImageResource(R.drawable.bilge_pump_animation);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_CLODGED)).getValue())) {
					imageView.setImageResource(R.drawable.ic_bilgepump_clodged);
				}
			}
			else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR_STATE)).getId()) { 
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) { 
				LinearLayout component = (LinearLayout)findViewById(idState);
				FrameLayout ll = (FrameLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);
			    TextView tvAccu = (TextView)(ll).getChildAt(0);
				tvAccu.setText(obuState.getValue() + "%");
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) { 
				LinearLayout component = (LinearLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId());
				FrameLayout ll = (FrameLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);
			    TextView tvAccu = (TextView)(ll).getChildAt(1);
			    String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				tvAccu.setText(f + "AH");
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) { 
				LinearLayout component = (LinearLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId());
				FrameLayout ll = (FrameLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);
			    TextView tvAccu = (TextView)(ll).getChildAt(2);
			    String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				tvAccu.setText(f + "A");
			}	
		}
	}
	
	
	public void changeAccuStep (View v) {
		LinearLayout component = (LinearLayout)findViewById(accuComponentId);
		FrameLayout ll = (FrameLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);	
		TextView tvAccuNapetost = (TextView)(ll).getChildAt(0);
		TextView tvAccuAH = (TextView)(ll).getChildAt(1);
		TextView tvAccuTok = (TextView)(ll).getChildAt(2);
		ImageView ivStep = (ImageView)(ll).getChildAt(3);
		
		switch (accuStep) {
		case 0:
			tvAccuNapetost.setVisibility(View.GONE);
			tvAccuAH.setVisibility(View.VISIBLE);
			tvAccuTok.setVisibility(View.GONE);
			ivStep.setImageResource(R.drawable.ic_battery_step_2);
			accuStep = 1;
			break;
		case 1:
			tvAccuNapetost.setVisibility(View.GONE);
			tvAccuAH.setVisibility(View.GONE);
			tvAccuTok.setVisibility(View.VISIBLE);
			ivStep.setImageResource(R.drawable.ic_battery_step_3);
			accuStep = 2;
			break;
		case 2:
			tvAccuNapetost.setVisibility(View.VISIBLE);
			tvAccuAH.setVisibility(View.GONE);
			tvAccuTok.setVisibility(View.GONE);
			ivStep.setImageResource(R.drawable.ic_battery_step_1);
			accuStep = 0;
			break;
		}
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
