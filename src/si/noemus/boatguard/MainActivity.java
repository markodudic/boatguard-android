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
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class MainActivity extends Activity {
	
	private int initialPosition;
	private boolean refreshing = false;
	private boolean scrollRefresh = false;
    private TextView tvLastUpdate;
    private ImageView ivRefresh;
    private AnimationDrawable refreshAnimation;
    private Handler handler = new Handler();
    private List<Integer> activeAlarms = new ArrayList<Integer>();
    
	public static HashMap<Integer,ObuState> obuStates = new HashMap<Integer,ObuState>(){};
	public static HashMap<Integer,ObuAlarm> obuAlarms = new HashMap<Integer,ObuAlarm>(){};
	
	public static HashMap<Integer,ObjectAnimator> alarmAnimaations = new HashMap<Integer,ObjectAnimator>(){};

	private static MainFragment mFragment;
	private static MapFragment lFragment;
    
	private static Gson gson = new Gson();	
	
	private int accuStep = 0;
	private int accuComponentId = 0;
	
	private int mActivePointerId = 123456;
	
	private float mLastTouchX, mPosX;
    private float mLastTouchY, mPosY;

    private Dialog dialogAlarm = null;
    private Integer dialogAlarmActive = -1;
    
    GoogleMap map;
    		
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

        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_main);
        final LinearLayout lLocation = (LinearLayout)findViewById(R.id.lLocation);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_location));
        
        map = mapFragment.getMap();
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);

        
        ImageView ivHome = (ImageView)findViewById(R.id.iv_home);
        ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				sv.setVisibility(View.VISIBLE);
				lLocation.setVisibility(View.GONE);
            } 
		});
		ImageView ivLocation = (ImageView)findViewById(R.id.iv_loction);
		ivLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				double lat=0, lon=0;
				String date = "";
		        Set set = obuStates.entrySet(); 
				Iterator i = set.iterator();
				while(i.hasNext()) { 
					Map.Entry map = (Map.Entry)i.next(); 
					ObuState obuState = (ObuState)map.getValue();
					if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_LAT)).getId()) { 
				        lat = Double.parseDouble(obuState.getValue());
					}
					else if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_LON)).getId()) { 
				        lon = Double.parseDouble(obuState.getValue());
					} else if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
						date = Utils.formatDate(obuState.getDateState());
					}
				}
				if (lat != 0 && lon != 0) {
					double latF = Math.floor(lat/100);
					double latD = (lat/100 - latF)/0.6;
					lat = latF + latD;
					double lonF = Math.floor(lon/100);
					double lonD = (lon/100 - lonF)/0.6;
					lon = lonF + lonD;
					
					LatLng latlng = new LatLng(lon, lat);
			        Marker newmarker = map.addMarker(new MarkerOptions().position(latlng).title(getResources().getString(R.string.location_title) + " " + date).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
			        CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(14.0f).build();
			        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
			        map.moveCamera(cameraUpdate); 
				}

				sv.setVisibility(View.GONE);
				lLocation.setVisibility(View.VISIBLE);
			}
		});
		
		
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


        sv.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View v, MotionEvent ev) {
        	   	final int action = MotionEventCompat.getActionMasked(ev); 
        	    
        	    switch (ev.getAction()) {
                case MotionEvent.ACTION_SCROLL:
                	break;
                case MotionEvent.ACTION_MOVE:
                	
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);  
                
		            final float x = MotionEventCompat.getX(ev, pointerIndex);
		            final float y = MotionEventCompat.getY(ev, pointerIndex);
		                
		            // Calculate the distance moved
		            final float dx = x - mLastTouchX;
		            final float dy = y - mLastTouchY;
		            mPosX += dx;
		            mPosY += dy;

		            mLastTouchX = x;
		            mLastTouchY = y;
            
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
             	   		if (newPosition == 0 && !refreshing && !scrollRefresh && (mPosX > 200 || mPosY > 200)) {
                			//System.out.println("refresh");
             	   			scrollRefresh = true;
             	   			if (Utils.isNetworkConnected(MainActivity.this, true)) {
             	       			getObudata();
             	   			}
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
                    final int pointerIndex1 = MotionEventCompat.getActionIndex(ev); 
                    final float x1 = MotionEventCompat.getX(ev, pointerIndex1); 
                    final float y1 = MotionEventCompat.getY(ev, pointerIndex1); 
                        
                    // Remember where we started (for dragging)
                    mLastTouchX = x1;
                    mLastTouchY = y1;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                	
                	break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                	mPosX=0;
                	mPosY=0;
                	scrollRefresh = false;
                    break;
                case MotionEvent.ACTION_POINTER_UP:                     
                    final int pointerIndex2 = MotionEventCompat.getActionIndex(ev); 
                    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex2); 

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex2 == 0 ? 1 : 0;
                        mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex); 
                        mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex); 
                        mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    }
                    break; 
                }
                return false;
            }
        });       
        
        
		dialogAlarm = new Dialog(this,R.style.Dialog);
		dialogAlarm.setContentView(R.layout.dialog_alarm); 
		dialogAlarm.setCanceledOnTouchOutside(false);
		dialogAlarm.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    
		//TextView confirmationTitle = (TextView) dialogConfirmation.findViewById(R.id.confirmation_title);
		//confirmationTitle.setTypeface(tf);
		
	    
		LinearLayout close = (LinearLayout) dialogAlarm.findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activeAlarms.remove(dialogAlarmActive);
				cancelNotification(dialogAlarmActive);
				dialogAlarmActive = -1;
				dialogAlarm.dismiss();
			}
		}); 
		 
		FrameLayout confirm = (FrameLayout) dialogAlarm.findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	if (Utils.isNetworkConnected(MainActivity.this, true)) {
		    		String obuId = Utils.getPrefernciesString(MainActivity.this, Settings.SETTING_OBU_ID);
		       		String urlString = MainActivity.this.getString(R.string.server_url) + "confirmalarm?obuid="+obuId+"&alarmid="+dialogAlarmActive;
		    		AsyncTask at = new Comm().execute(urlString); 
		    	}
		    	
				activeAlarms.remove(dialogAlarmActive);
				cancelNotification(dialogAlarmActive);
				dialogAlarmActive = -1;
				dialogAlarm.dismiss();
			}
		});	
		
	
        Settings.getSettings(this);        
        Settings.getObuSettings(this);  
        Settings.getObuComponents(this);  
        showObuComponents();
   		handler.postDelayed(startRefresh, 1000);
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		getObudata();
	}
    
	
	private void showObuComponents(){
		LinearLayout lComponents = (LinearLayout)findViewById(R.id.components);

        HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		Set set = obuComponents.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			//System.out.println(map.getValue());
			ObuComponent obuComponent = (ObuComponent)map.getValue();
			if (obuComponent.getShow() == 0) continue;
			
			int lc = R.layout.list_component;
			if (obuComponent.getType().equals("ACCU")) { 
				lc = R.layout.list_component_accu;
			}

			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View component = inflater.inflate(lc, null);
		    component.setId(obuComponent.getId_component());
			
		    ((TextView)component.findViewById(R.id.label)).setText(obuComponent.getName());
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_geofence_disabled);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
			    ((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_bilgepump);
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
			    ((ImageView)component.findViewById(R.id.logo)).setImageResource(R.drawable.ic_anchor_disabled); 
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ACCU)) { 
				((TextView)component.findViewById(R.id.accu_napetost)).setText("");
				accuComponentId = obuComponent.getId_component();
			} 
			
			lComponents.addView(component);
		}
		
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
    	if (Utils.isNetworkConnected(this, false)) {
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
    	   			//System.out.println(obuState.toString());
    	   			obuStates.put(obuState.getId_state(), obuState);
    	   		}
	    	   	
	    	   	JSONArray jsonAlarms = (JSONArray)jRes.get("alarms");
	    	   	obuAlarms.clear();
	    	   	for (int i=0; i< jsonAlarms.length(); i++) {
    	   			ObuAlarm obuAlarm = gson.fromJson(jsonAlarms.get(i).toString(), ObuAlarm.class);
    	   			//System.out.println(obuAlarm.toString());
    	   			obuAlarms.put(obuAlarm.getId_alarm(), obuAlarm);
    	   			if (activeAlarms.indexOf(obuAlarm.getId_alarm()) == -1) {
    	   				showNotification(obuAlarm.getId_alarm(), obuAlarm.getTitle(), obuAlarm.getMessage(), obuAlarm.getDate_alarm(), obuAlarm.getVibrate(), obuAlarm.getSound());
    	   				activeAlarms.add(obuAlarm.getId_alarm());
    	   			}
    	   			if (dialogAlarmActive == -1) {
    	   				showAlarmDialog(obuAlarm.getId_alarm(), obuAlarm.getTitle(), obuAlarm.getMessage(), obuAlarm.getAction(), obuAlarm.getType());
    	   			}
    	   		}
    	   		
	    	   	showObuData();

	        } catch (Exception e) {
   	        	/*e.printStackTrace();
   	        	Toast toast = Toast.makeText(this, this.getString(R.string.json_error), Toast.LENGTH_LONG);
   	        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
   	        	toast.show();*/
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
				FrameLayout component = (FrameLayout)findViewById(idState);
				ImageView imageView = (ImageView)component.findViewById(R.id.logo);
				String geofence = obuState.getValue();
				cancelAlarmAnimation(component);
				
				if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_DISABLED)).getValue())) {
					imageView.setImageResource(R.drawable.ic_geofence_disabled);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ENABLED)).getValue())) {
					imageView.setImageResource(R.drawable.ic_geofence_home);
				} 
				else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ALARM)).getValue())) {
					showAlarmAnimation(component, imageView, R.drawable.geofence_animation);
				}
				else {
					imageView.setImageResource(android.R.color.transparent);
				}
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				ImageView imageView = (ImageView)component.findViewById(R.id.logo);
				String pumpState = obuState.getValue();
				cancelAlarmAnimation(component);
				
				if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_OK)).getValue())) {
					imageView.setImageResource(R.drawable.ic_bilgepump);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
					showAlarmAnimation(component, imageView, R.drawable.bilge_pumping_animation);
				}
				else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_CLODGED)).getValue())) {
					showAlarmAnimation(component, imageView, R.drawable.pump_clodged_animation);
				}
				else {
					imageView.setImageResource(android.R.color.transparent); 
				}
			}
			else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR_STATE)).getId()) { 
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				((TextView)component.findViewById(R.id.accu_napetost)).setText(obuState.getValue() + "%");
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				((TextView)component.findViewById(R.id.accu_ah)).setText(f + "AH");
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId());
				String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
				((TextView)component.findViewById(R.id.accu_tok)).setText(f + "A");
			}	
		}
	}
	
	private void showAlarmAnimation (FrameLayout layout, ImageView imageView, int anim_id) {
	    TypedArray a1 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.component});     
        int backgroundId = a1.getResourceId(0, 0);       

		ObjectAnimator colorFade = ObjectAnimator.ofObject((LinearLayout)layout.findViewById(R.id.main), "backgroundColor", 
				new ArgbEvaluator(), 
				getResources().getColor(backgroundId), 
				getResources().getColor(R.color.alarm_red));
		colorFade.setDuration(getResources().getInteger(R.integer.animation_interval));
		colorFade.setRepeatCount(-1);
		colorFade.setRepeatMode(Animation.REVERSE);
		colorFade.start();
				
		imageView.setImageResource(anim_id);
		/*Animation anim = new AlphaAnimation(0, 1);
		anim.setDuration(3000);
		anim.setRepeatCount(-1);
		anim.setRepeatMode(Animation.REVERSE);
		imageView.startAnimation(anim);*/
		
		((LinearLayout)layout.findViewById(R.id.line)).setVisibility(View.GONE);
		((LinearLayout)layout.findViewById(R.id.shadow_top)).setVisibility(View.VISIBLE);
		((LinearLayout)layout.findViewById(R.id.shadow_bottom)).setVisibility(View.VISIBLE);
		
		alarmAnimaations.put(layout.getId(), colorFade);
		
	    TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.ic_logotype_alarm});     
        int logoId = a2.getResourceId(0, 0);       
		((ImageView)findViewById(R.id.actionBarLogo)).setImageResource(logoId);
	}
	
	private void cancelAlarmAnimation (FrameLayout layout) {
		if (alarmAnimaations.containsKey(layout.getId())) {
	        ObjectAnimator colorFade = (ObjectAnimator)alarmAnimaations.get(layout.getId());
			colorFade.end();

		    TypedArray a1 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.component});     
	        int backgroundId = a1.getResourceId(0, 0);       
	        ((LinearLayout)layout.findViewById(R.id.main)).setBackgroundColor(getResources().getColor(backgroundId));
	        
			((LinearLayout)layout.findViewById(R.id.line)).setVisibility(View.VISIBLE);
			((LinearLayout)layout.findViewById(R.id.shadow_top)).setVisibility(View.GONE);
			((LinearLayout)layout.findViewById(R.id.shadow_bottom)).setVisibility(View.GONE);
			
		    TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.ic_logotype});     
	        int logoId = a2.getResourceId(0, 0);       
			((ImageView)findViewById(R.id.actionBarLogo)).setImageResource(logoId);
			
		}
	}
	
	
	public void changeAccuStep (View v) {
		FrameLayout component = (FrameLayout)findViewById(accuComponentId);
		LinearLayout ll = (LinearLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);	
		TextView tvAccuNapetost = (TextView)component.findViewById(R.id.accu_napetost);
		TextView tvAccuAH = (TextView)component.findViewById(R.id.accu_ah);
		TextView tvAccuTok = (TextView)component.findViewById(R.id.accu_tok);
		ImageView ivStep = (ImageView)component.findViewById(R.id.step);
		
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
	
	private void showAlarmDialog(int id, String msg, String desc, String action, String type){
		dialogAlarmActive = id;
		if (type!=null && type.equalsIgnoreCase(Settings.ALARM_GREEN)) {
			((LinearLayout)dialogAlarm.findViewById(R.id.lAlarm)).setBackgroundColor(getResources().getColor(R.color.alarm_green));
		}
		else {
			((LinearLayout)dialogAlarm.findViewById(R.id.lAlarm)).setBackgroundColor(getResources().getColor(R.color.alarm_red));			
		}
		((TextView)dialogAlarm.findViewById(R.id.alarm_msg)).setText(msg.toUpperCase());
		((TextView)dialogAlarm.findViewById(R.id.alarm_desc)).setText(desc.toUpperCase());
		((TextView)dialogAlarm.findViewById(R.id.alarm_confirm)).setText(action.toUpperCase());
		dialogAlarm.show();
	}
	
	
	private void showNotification(int id, String title, String message, Timestamp date, int vibrate, int sound){
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
		
		beepVibrate(vibrate, sound);
	}
	
	
	public void cancelNotification(int notifyId) {
	    NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	    nMgr.cancel(notifyId);
	}
	
	public void beepVibrate(int vibrate, int sound) {
	    try {
	        if (sound == 1) {
		    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
	        }
	    } catch (Exception e) {}
	    if (vibrate == 1) {
	    	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
	    }
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
