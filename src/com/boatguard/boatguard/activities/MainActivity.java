package com.boatguard.boatguard.activities;
 
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.Alarm;
import com.boatguard.boatguard.objects.AppSetting;
import com.boatguard.boatguard.objects.ObuAlarm;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.ObuState;
import com.boatguard.boatguard.objects.State;
import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.Comm.OnTaskCompleteListener;
import com.boatguard.boatguard.utils.CyclicTransitionDrawable;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
import com.google.gson.Gson;

public class MainActivity extends Activity {
	
	private int initialPosition;
	private boolean refreshing = false;
	private boolean scrollRefresh = false;
    private TextViewFont tvLastUpdate;
    private ImageView ivRefresh;
    private AnimationDrawable refreshAnimation;
    private Handler handler = new Handler();
    public static List<Integer> activeAlarms = new ArrayList<Integer>();
    
	public static HashMap<Integer,ObuState> obuStates = new HashMap<Integer,ObuState>(){};
	public static HashMap<Integer,ObuAlarm> obuAlarms = new HashMap<Integer,ObuAlarm>(){};
	public static List<HashMap> history = new ArrayList<HashMap>();
	
	public static HashMap<Integer,ObjectAnimator> alarmAnimaations = new HashMap<Integer,ObjectAnimator>(){};

	private static Gson gson = new Gson();	
	
	private int accuStep = 0;
	private int accuComponentId = 0;
	private boolean isAccuConnected = false;
	
	private int mActivePointerId = 123456;
	
	private float mLastTouchX, mPosX;
    private float mLastTouchY, mPosY;

    private Dialog dialogAlarm = null;
    private Integer dialogAlarmActive = -1;
    private int notificationAlarmId = -1;
    
    private LinearLayout lMenu;
    private TypedArray stylesAttributes = null;
 
	//private ComponentsAdapter componentsAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = Utils.getPrefernciesInt(this, Settings.SETTING_THEME);
		if (theme != -1) {
			setTheme(theme);			
		}
		
		stylesAttributes = getTheme().obtainStyledAttributes(
	    		Utils.getPrefernciesInt(this, Settings.SETTING_THEME), 
	    		new int[] {R.attr.horizontal_line,
	    					R.attr.text_content, 
	    					R.attr.component, 
	    					R.attr.ic_logotype,
	    					R.attr.ic_logotype_alarm});     

	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_icon);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        
        //last update
        tvLastUpdate = (TextViewFont)findViewById(R.id.tv_last_update);
        ivRefresh = (ImageView)findViewById(R.id.iv_refresh);
		if (theme == R.style.AppThemeDay) {
			ivRefresh.setBackgroundResource(R.drawable.refresh_animation_day);
		} 
		else {
			ivRefresh.setBackgroundResource(R.drawable.refresh_animation);
		}
        refreshAnimation = (AnimationDrawable) ivRefresh.getBackground();

        
        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_main);
        final LinearLayout lLocation = (LinearLayout)findViewById(R.id.lLocation);
        
        ImageView ivHome = (ImageView)findViewById(R.id.iv_home);
        ivHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				sv.setVisibility(View.VISIBLE);
				//lLocation.setVisibility(View.GONE);
            } 
		});
		ImageView ivLocation = (ImageView)findViewById(R.id.iv_loction);
		ivLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				Intent in = new Intent(MainActivity.this, LocationActivity.class);
				startActivity(in);				
			}
		});
		
		
        ImageView ivSettings = (ImageView)findViewById(R.id.iv_settings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				showSettings();
			}
		});


    	
        //ListView lvComponents = (ListView)findViewById(R.id.components);
        sv.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View v, MotionEvent ev) {
        	   	final int action = MotionEventCompat.getActionMasked(ev); 
        	    
        	    switch (ev.getAction()) {
                case MotionEvent.ACTION_SCROLL:
                	break;
                case MotionEvent.ACTION_MOVE:
                	
                    //final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);  
                
		            //final float x = MotionEventCompat.getX(ev, pointerIndex);
		            //final float y = MotionEventCompat.getY(ev, pointerIndex);
		            final float x = ev.getX();
		            final float y = ev.getY();
		            //if (y <= 0 || mLastTouchY <= 0 || y - mLastTouchY <= 0) break;
		                
		            // Calculate the distance moved
		            final float dx = x - mLastTouchX;
		            final float dy = y - mLastTouchY;
		            
		            mPosX += dx;
		            mPosY += dy;

		            mLastTouchX = x;
		            mLastTouchY = y;
            
		            //v.setY(mPosY);
		            
		            final float scale = MainActivity.this.getResources().getDisplayMetrics().density;
                    int px = (int) (MainActivity.this.getResources().getDimension(R.dimen.menu_height) * scale + 0.5f);
                    TranslateAnimation anim = null;
                	int newPosition = sv.getScrollY();
                	if (newPosition > initialPosition) {
                		anim=new TranslateAnimation(0,0,0,px);
                	} else if ((newPosition < initialPosition) || (newPosition==0 && initialPosition==0)) {
                		anim=new TranslateAnimation(0,0,px,0);
             	   		if (newPosition == 0 && !refreshing && !scrollRefresh && (mPosX > 100 || mPosY > 100)) {
                			scrollRefresh = true;
             	   			if (Utils.isNetworkConnected(MainActivity.this, true)) {
             	       			getObudata();
             	       			getObuHistoryData();
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
             	    mLastTouchY = 0;
                	scrollRefresh = false;
                	v.setY(getResources().getDimension(R.dimen.menu_height));
                	anim=null;
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
		
	    
		LinearLayout close = (LinearLayout) dialogAlarm.findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	if (Utils.isNetworkConnected(MainActivity.this, true)) {
		    		confirmAlarm(dialogAlarmActive);
		    	}
			}
		}); 
		 
		FrameLayout confirm = (FrameLayout) dialogAlarm.findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	if (Utils.isNetworkConnected(MainActivity.this, true)) {
		    		confirmAlarm(dialogAlarmActive);
		    	}
			}
		});	
		
        lMenu = (LinearLayout)findViewById(R.id.fragment_menu);
 
        Settings.getSettings(this);        
        Settings.getObuSettings(this);  
        Settings.getObuComponents(this);  
        Settings.getObuAlarms(this); 
        Settings.getCustomer(this); 
        Settings.getFriends(this);
        showObuComponents();
        
        
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	getObudata();
            }
        }, new IntentFilter("GCMMessageReceived"));
        

        //componentsAdapter = new ComponentsAdapter();
        //lvComponents.setAdapter(componentsAdapter);
   		
        //handler.postDelayed(startRefresh, Settings.OBU_REFRESH_TIME);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getObudata();
		getObuHistoryData();
	}
	 
	@Override
	public void onNewIntent(Intent intent){
		Bundle extras = intent.getExtras();
		if (extras != null) {
			notificationAlarmId = extras.getInt("alarmId");
		}
	}
	
	private void confirmAlarm(int alarmId){
		String obuId = Utils.getPrefernciesString(MainActivity.this, Settings.SETTING_OBU_ID);
    	String sessionid = Utils.getPrefernciesString(this, Settings.SETTING_SESSION_ID);
   		String urlString = MainActivity.this.getString(R.string.server_url) + "confirmalarm?obuid="+obuId+"&alarmid="+alarmId+"&sessionid="+sessionid;
   		new Comm().execute(urlString, null); 
   		
   		if (activeAlarms.indexOf(alarmId) != -1) {
   			activeAlarms.remove(activeAlarms.indexOf(alarmId));
   		}
		cancelNotification(alarmId);
		dialogAlarmActive = -1;
		dialogAlarm.dismiss();
	}
	
	@SuppressLint("NewApi")
	private void showObuComponents(){
		TableLayout lComponents = (TableLayout)findViewById(R.id.components);

	    //TypedArray a = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.horizontal_line});     
        int lineId = stylesAttributes.getResourceId(0, 0);       

        HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		Set set = obuComponents.entrySet(); 
		Iterator i = set.iterator();
		int ii = 0;
		TableRow tr = new TableRow(this);
		View component = null;
		//View componentLast = null;
		
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuComponent obuComponent = (ObuComponent)map.getValue();
			if (obuComponent.getShow() == 0) continue;
			
			int lc = R.layout.list_component;
			if (obuComponent.getType().equals("ACCU")) { 
				lc = R.layout.list_component_accu;
			}
			
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//componentLast = component;
		    component = inflater.inflate(lc, null);
		    component.setId(obuComponent.getId_component());
		    
		    /*if (obuComponent.getType().equals("LIGHT") || obuComponent.getType().equals("FAN")) { 
				((Switch) component.findViewById(R.id.switch_comp)).setVisibility(View.VISIBLE);
				((ImageView)component.findViewById(R.id.logo)).setVisibility(View.INVISIBLE);
			} else if (obuComponent.getType().equals("GEO") || obuComponent.getType().equals("PUMP") || obuComponent.getType().equals("ANCHOR") || obuComponent.getType().equals("DOOR")) {
				((Switch) component.findViewById(R.id.switch_comp)).setVisibility(View.INVISIBLE);
				((ImageView)component.findViewById(R.id.logo)).setVisibility(View.VISIBLE);
			}*/

			/*if (Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_THEME) == R.style.AppThemeDay) {
			    Display display = getWindowManager().getDefaultDisplay();
			    Point size = new Point();
			    display.getSize(size);
			    int width = size.x;
			    int height = size.y;
			    component.setLayoutParams(new TableRow.LayoutParams(width/2, width/2));
			    
			    LinearLayout content = ((LinearLayout)component.findViewById(R.id.content));
			    content.setOrientation(LinearLayout.VERTICAL);
			    int pad = Utils.dpToPx(this, (int)getResources().getDimension(R.dimen.components_margin));
			    int lin = Utils.dpToPx(this, (int)getResources().getDimension(R.dimen.line_height));
			    content.setLayoutParams(new TableRow.LayoutParams(width/2, width/2-lin));
			    content.setPadding(0, (int)getResources().getDimension(R.dimen.components_margin), 0, (int)getResources().getDimension(R.dimen.components_margin));
			    
			    TextViewFont label = ((TextViewFont)component.findViewById(R.id.label));
			    TableRow.LayoutParams lpV = new TableRow.LayoutParams(width/2-(2*(int)getResources().getDimension(R.dimen.components_margin)), width/6);
		        label.setLayoutParams(lpV);
			    label.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

			    FrameLayout icon = ((FrameLayout)component.findViewById(R.id.lIcon));
			    TableRow.LayoutParams lpIc = new TableRow.LayoutParams(width/2-(2*(int)getResources().getDimension(R.dimen.components_margin)), LayoutParams.MATCH_PARENT);
			    icon.setLayoutParams(lpIc);

			    FrameLayout.LayoutParams lpAccu = new FrameLayout.LayoutParams(width/2-(2*(int)getResources().getDimension(R.dimen.components_margin)), LayoutParams.MATCH_PARENT);
			    TextViewFont tvNapetost = ((TextViewFont)component.findViewById(R.id.accu_napetost));
			    if (tvNapetost != null) {
			    	tvNapetost.setLayoutParams(lpAccu);
			    }
			    TextViewFont tvAh = ((TextViewFont)component.findViewById(R.id.accu_ah));
			    if (tvAh != null) {
			    	tvAh.setLayoutParams(lpAccu);
			    }
			    TextViewFont tvTok = ((TextViewFont)component.findViewById(R.id.accu_tok));
			    if (tvTok != null) {
			    	tvTok.setLayoutParams(lpAccu);
			    }
			    
			    ImageView ivStep = (ImageView)component.findViewById(R.id.step);
			    if (ivStep != null) {
			    	FrameLayout.LayoutParams lpI = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			    	lpI.gravity = Gravity.BOTTOM | Gravity.LEFT;
			    	lpI.setMargins((int)getResources().getDimension(R.dimen.components_margin)-20, 0, 0, (int)getResources().getDimension(R.dimen.component_step_margin2));
				    ivStep.setLayoutParams(lpI);		    
			    }
			}*/
			    
			TextView label = ((TextView)component.findViewById(R.id.label));
			label.setText(obuComponent.getName());
		    ((TextViewFont)component.findViewById(R.id.label)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
	        
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_geofence_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(0);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
			    ((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_bilgepump);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(1);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
			    ((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_anchor_disabled); 
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(2);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ACCU)) { 
				((TextView)component.findViewById(R.id.accu_napetost)).setText("");
				accuComponentId = obuComponent.getId_component();
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(3);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_LIGHT)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_light_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(10);
					}
				});				
				/*final Switch switch_comp = ((Switch) component.findViewById(R.id.switch_comp));
				switch_comp.setVisibility(View.INVISIBLE);
				switch_comp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
						//set settings
				        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
				        obuSettings.get(((State)Settings.states.get(Settings.STATE_LIGHT)).getId()).setValue(switch_comp.isChecked()?"1":"0");
				        Settings.setObuSettings(MainActivity.this);
						
					}
				});*/
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_FAN)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_fan_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(11);
					}
				});	
				/*final Switch switch_comp = ((Switch) component.findViewById(R.id.switch_comp));
				switch_comp.setVisibility(View.INVISIBLE);
				switch_comp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
				        obuSettings.get(((State)Settings.states.get(Settings.STATE_FAN)).getId()).setValue(switch_comp.isChecked()?"1":"0");
				        Settings.setObuSettings(MainActivity.this);
					}
				});*/
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_DOOR)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_door_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSettings();
					}
				});
			} 			
			/*if (Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_THEME) == R.style.AppThemeDay) {
				if (ii%2 == 0) {
					tr = new TableRow(this);
					tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				}
				tr.addView(component);
				
				if (ii%2 == 1) {
					lComponents.addView(tr);
				} else {
			        LinearLayout lineV = new LinearLayout(this);
			        lineV.setBackgroundColor(this.getResources().getColor(lineId));
			        TableRow.LayoutParams lpV = new TableRow.LayoutParams((int)getResources().getDimension(R.dimen.line_height), LayoutParams.MATCH_PARENT);
			        lpV.setMargins(0, (int)getResources().getDimension(R.dimen.components_margin), 0, (int)getResources().getDimension(R.dimen.components_margin));
			        lineV.setLayoutParams(lpV);
			        tr.addView(lineV);
				}
				ii++;
			} 
			else {*/
				lComponents.addView(component);
			//}
		}
		
		int lc = R.layout.list_component;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		component = inflater.inflate(lc, null);
		TextView label = ((TextView)component.findViewById(R.id.label));
		label.setText("");
		lComponents.addView(component);	    
	    
	    
		//zadnjo crto skrijem
		if (component != null) {
			((LinearLayout)component.findViewById(R.id.line)).setVisibility(View.GONE);
			/*if (Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_THEME) == R.style.AppThemeDay) {
				((LinearLayout)componentLast.findViewById(R.id.line)).setVisibility(View.GONE);
			}*/
		}

	}

	private void showSetting(int item) {
		Intent i = new Intent(MainActivity.this, SettingsActivity.class);
		i.putExtra("id", item);
		i.putExtra("title", getResources().getStringArray(R.array.settings_items_titles)[item]);
		startActivity(i);
	}


	private void showSettings(){
		Intent i = new Intent(MainActivity.this, SettingsActivity.class);
		i.putExtra("id", -1);
		i.putExtra("title", getResources().getString(R.string.menu));
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
	  
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
		//mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
    public void getObudata() {
		System.out.println("************************GET DATA**************************");
		refreshing = true;
    	String obuId = Utils.getPrefernciesString(this, Settings.SETTING_OBU_ID);
    	String sessionid = Utils.getPrefernciesString(this, Settings.SETTING_SESSION_ID);
   		
    	String urlString = this.getString(R.string.server_url) + "getdata?obuid="+obuId+"&sessionid="+sessionid;
    	if (Utils.isNetworkConnected(this, false)) {
  				tvLastUpdate.setVisibility(View.GONE);	
    			ivRefresh.setVisibility(View.VISIBLE);	
    			refreshAnimation.start();
    			
            	Comm at = new Comm();
    			at.setCallbackListener(clGetObuData);
    			at.execute(urlString, null); 
    	}
    }	 
    
    
    private OnTaskCompleteListener clGetObuData = new OnTaskCompleteListener() {

        @Override
        public void onComplete(String res) {
  			try {
	            JSONObject jRes = (JSONObject)new JSONTokener(res).nextValue();
    	   		JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	obuStates.clear();
		   		for (int i=0; i< jsonStates.length(); i++) {
		   			ObuState obuState = gson.fromJson(jsonStates.get(i).toString(), ObuState.class);
		   			obuStates.put(obuState.getId_state(), obuState);
		   		}
	    	   	
	    	   	JSONArray jsonAlarms = (JSONArray)jRes.get("alarms");
	    	   	obuAlarms.clear();
	    	   	for (int i=0; i< jsonAlarms.length(); i++) {
		   			ObuAlarm obuAlarm = gson.fromJson(jsonAlarms.get(i).toString(), ObuAlarm.class);
		   			obuAlarms.put(obuAlarm.getId_alarm(), obuAlarm);
		   			if (activeAlarms.indexOf(obuAlarm.getId_alarm()) == -1) {
		   				showNotification(obuAlarm.getId_alarm(), obuAlarm.getTitle(), obuAlarm.getMessage(), obuAlarm.getDate_alarm(), obuAlarm.getVibrate(), obuAlarm.getSound());
		   				activeAlarms.add(obuAlarm.getId_alarm());
		   			}
		   			if ((dialogAlarmActive == -1) && Utils.getPrefernciesBoolean(MainActivity.this, Settings.SETTING_POP_UP, false)) {
		   				if ((notificationAlarmId == -1) || ((notificationAlarmId != -1) && (notificationAlarmId == obuAlarm.getId_alarm()))) {
		   					showAlarmDialog(obuAlarm.getId_alarm(), obuAlarm.getTitle(), obuAlarm.getMessage(), obuAlarm.getAction(), obuAlarm.getType());
		   				}
		   			}
		   		    if ((notificationAlarmId != -1) && !Utils.getPrefernciesBoolean(MainActivity.this, Settings.SETTING_POP_UP, false)) {
		   		    	confirmAlarm(notificationAlarmId);
		   		    }
		   		}
		   		
	    	   	showObuData();
	    	   	notificationAlarmId = -1;
	    	   	
	    	   	//sam za to da se prikaze spinner
	    	   	handler.postDelayed(endRefresh, 1000);

	        } catch (Exception e) {
	    	   	handler.postDelayed(endRefresh, 1000);
	        	e.printStackTrace();
	        	e.getLocalizedMessage();
   	   		}
        }
    };
    
	public void getObuHistoryData() {
    	String obuId = Utils.getPrefernciesString(this, Settings.SETTING_OBU_ID);
   		
    	String sessionid = Utils.getPrefernciesString(this, Settings.SETTING_SESSION_ID);
    	String urlString = this.getString(R.string.server_url) + "gethistorydata?obuid="+obuId+"&sessionid="+sessionid;
    	if (Utils.isNetworkConnected(this, false)) {
            	Comm at = new Comm();
    			at.setCallbackListener(clGetObuHistoryData);
    			at.execute(urlString, null); 
    	}
	}
	
    private OnTaskCompleteListener clGetObuHistoryData = new OnTaskCompleteListener() {

        @Override
        public void onComplete(String res) {
        	//System.out.println("RES="+res);
        	 
        	JSONObject jRes;
			try {
				jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	history.clear();
	        	for (int i=0; i< jsonStates.length(); i++) {
	    	   		JSONArray jsonState = (JSONArray)jsonStates.get(i);
    	   			//System.out.println("jsonState="+jsonState.toString());
	    	   		LinkedHashMap<Integer,ObuState> obuStates = new LinkedHashMap<Integer,ObuState>(){};
	    	   		for (int ii=0; ii< jsonState.length(); ii++) {
	    	   			ObuState obuState = gson.fromJson(jsonState.get(ii).toString(), ObuState.class);
	    	   			//System.out.println(obuState.getId_state()+":"+obuState.getDateState());
	    	   			obuStates.put(obuState.getId_state(), obuState);
	    	   		}
    	   			history.add(obuStates);
		   		}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };

			
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void showObuData(){
		boolean alarm = false;
		
		String accuDisconnected = ((ObuState)obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId())).getValue();
		if (accuDisconnected != null) {
			isAccuConnected = !accuDisconnected.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue());
		}
		
        Set set = obuStates.entrySet(); 
		Iterator i = set.iterator();
		while(i.hasNext()) { 
			Map.Entry map = (Map.Entry)i.next(); 
			ObuState obuState = (ObuState)map.getValue();
			int idState = obuState.getId_state();

			if (idState == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) { 
            	tvLastUpdate.setText(getResources().getString(R.string.last_update) + " " + Utils.formatDate(obuState.getDateState()));
            	tvLastUpdate.setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));

			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_GEO_FENCE)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String geofence = obuState.getValue();
					cancelAlarmAnimation(component, null, false);
					
					if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_DISABLED)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_geofence_disabled);
					} 
					else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ENABLED)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_geofence_home);
					} 
					else if (geofence.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_GEO_FENCE_ALARM)).getValue())) {
						alarm = true;
						showAlarmAnimation(component, imageView, R.drawable.ic_geofence_alarm_1, R.drawable.ic_geofence_alarm, true);
					}
					else {
						imageView.setBackgroundResource(android.R.color.transparent);
					}
				}
			}			
			else if (idState == ((State)Settings.states.get(Settings.STATE_PUMP_STATE)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String pumpState = obuState.getValue();
					cancelAlarmAnimation(component, null, false);
					
					if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_OK)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_bilgepump);
					}
					else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_PUMPING)).getValue())) {
						alarm = true;
						if (Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_THEME) == R.style.AppThemeDay) {
							showAlarmAnimation(component, imageView, R.drawable.bilge_pumping_animation_day, 0, false);
						} 
						else {
							showAlarmAnimation(component, imageView, R.drawable.bilge_pumping_animation, 0, false);
						}
					}
					else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_CLODGED)).getValue())) {
						alarm = true;
						showAlarmAnimation(component, imageView, R.drawable.ic_bilgepump_clodged_1, R.drawable.ic_bilgepump_clodged, true);
					}
					else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_DEMAGED)).getValue())) {
						alarm = true;
						showAlarmAnimation(component, imageView, R.drawable.ic_bilgepump_demaged_1, R.drawable.ic_bilgepump_demaged, true);
					}
					else if (pumpState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_PUMP_SERVIS)).getValue())) {
						alarm = true;
						showAlarmAnimation(component, imageView, R.drawable.ic_bilgepump_repair_1, R.drawable.ic_bilgepump_repair, true);
					}
					else {
						imageView.setBackgroundResource(android.R.color.transparent); 
					}
				}
			}
			else if (idState == ((State)Settings.states.get(Settings.STATE_ANCHOR)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String anchorState = obuState.getValue(); 
					cancelAlarmAnimation(component, null, false);
					
					if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DISABLED)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_anchor_disabled);
					}			
					else if (anchorState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_ENABLED)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_anchor);
						int anchorDriftingId = ((State)Settings.states.get(Settings.STATE_ANCHOR_DRIFTING)).getId();
						String anchorDrifting = ((ObuState) obuStates.get(anchorDriftingId)).getValue();
						if (anchorDrifting.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ANCHOR_DRIFTING)).getValue())) {
							alarm = true;
							showAlarmAnimation(component, imageView, R.drawable.ic_anchor_alarm_1, R.drawable.ic_anchor_alarm, true);
						}			
					}
				}
			}		
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId()) && (isAccuConnected)) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					((TextViewFont)component.findViewById(R.id.accu_ah)).setText(obuState.getValue() + "%");
			        ((TextViewFont)component.findViewById(R.id.accu_ah)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
					cancelAlarmAnimation(component, (TextView)component.findViewById(R.id.accu_ah), true);
					
					String accuEmpty = ((ObuState)obuStates.get(((State)Settings.states.get(Settings.STATE_ACCU_EMPTY)).getId())).getValue();
					if (accuEmpty.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ALARM_BATTERY_EMPTY)).getValue())) {
						alarm = true;
						showAlarmAccuAnimation(component, (TextView)component.findViewById(R.id.accu_ah));
					}
				}
			}
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_NAPETOST)).getId()) && (isAccuConnected)) { 
				FrameLayout component = (FrameLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId());
				if (component != null) {
					//String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
					((TextViewFont)component.findViewById(R.id.accu_napetost)).setText(obuState.getValue() + "V");
			        ((TextViewFont)component.findViewById(R.id.accu_napetost)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
				}
			}	
			else if ((idState == ((State)Settings.states.get(Settings.STATE_ACCU_TOK)).getId()) && (isAccuConnected)) { 
				FrameLayout component = (FrameLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId());
				if (component != null) {
					//String f = new DecimalFormat("#.##").format(Float.parseFloat(obuState.getValue()));
					((TextViewFont)component.findViewById(R.id.accu_tok)).setText(obuState.getValue() + "A");
			        ((TextViewFont)component.findViewById(R.id.accu_tok)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
				}
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_ACCU_DISCONNECT)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(((State)Settings.states.get(Settings.STATE_ACCU_AH)).getId());
				if (component != null) {				
					ImageView imageView = (ImageView)component.findViewById(R.id.accu_disconnected);
					imageView.setVisibility(View.VISIBLE);
					TextView tvAccuNapetost = (TextView)component.findViewById(R.id.accu_napetost);
					TextView tvAccuAH = (TextView)component.findViewById(R.id.accu_ah);
					TextView tvAccuTok = (TextView)component.findViewById(R.id.accu_tok);
					ImageView ivStep = (ImageView)component.findViewById(R.id.step);
					//FrameLayout fl = (FrameLayout)component.findViewById(R.id.lIcon);
					
					String accuDisconnectedState = obuState.getValue();
					cancelAlarmAnimation(component, null, false);
					if (accuDisconnectedState.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ACCU_DISCONNECT)).getValue())) {
						showAlarmAnimation(component, imageView, R.drawable.ic_accu_disconnected_1, R.drawable.ic_accu_disconnected, true);
					 
						//fl.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 5f));
						tvAccuNapetost.setVisibility(View.GONE);
						tvAccuAH.setVisibility(View.GONE);
						tvAccuTok.setVisibility(View.GONE);
						ivStep.setVisibility(View.GONE);
					}
					else {
						switch (accuStep) {
						case 0:
							tvAccuNapetost.setVisibility(View.GONE);
							tvAccuAH.setVisibility(View.VISIBLE);
							tvAccuTok.setVisibility(View.GONE);
							ivStep.setImageResource(R.drawable.ic_battery_step_1);
							break;
						case 1:
							tvAccuNapetost.setVisibility(View.VISIBLE);
							tvAccuAH.setVisibility(View.GONE);
							tvAccuTok.setVisibility(View.GONE);
							ivStep.setImageResource(R.drawable.ic_battery_step_2);
							break;
						case 2: 
							tvAccuNapetost.setVisibility(View.GONE);
							tvAccuAH.setVisibility(View.GONE);
							tvAccuTok.setVisibility(View.VISIBLE);
							ivStep.setImageResource(R.drawable.ic_battery_step_3);
							break;
						} 
	
						//fl.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 5f));
						((ImageView)component.findViewById(R.id.accu_disconnected)).setVisibility(View.GONE);
						((ImageView)component.findViewById(R.id.step)).setVisibility(View.VISIBLE);
					}
				}
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_LIGHT)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String light = obuState.getValue();
					
					if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_light_disabled);
					} 
					else if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_light);
					} 
					else {
						imageView.setBackgroundResource(R.drawable.ic_light_disabled);
					}
					
					/*Switch switch_comp = ((Switch) component.findViewById(R.id.switch_comp));
					
					if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_OFF)).getValue())) {
						switch_comp.setVisibility(View.VISIBLE);
						switch_comp.setChecked(false);
					} 
					else if (light.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_LIGHT_ON)).getValue())) {
						switch_comp.setVisibility(View.VISIBLE);
						switch_comp.setChecked(true);
					} 
					else {
						((Switch) component.findViewById(R.id.switch_comp)).setVisibility(View.INVISIBLE);
					}*/
				}
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_FAN)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String fan = obuState.getValue();
					
					if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_FAN_OFF)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_fan_disabled);
					} 
					else if (fan.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_FAN_ON)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_fan);
					} 
					else {
						imageView.setBackgroundResource(R.drawable.ic_fan_disabled);
					}
				}
			}	
			else if (idState == ((State)Settings.states.get(Settings.STATE_DOOR)).getId()) { 
				FrameLayout component = (FrameLayout)findViewById(idState);
				if (component != null) {
					ImageView imageView = (ImageView)component.findViewById(R.id.logo);
					String door = obuState.getValue();
					cancelAlarmAnimation(component, null, false);
					
					if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_OK)).getValue())) {
						imageView.setBackgroundResource(R.drawable.ic_door);
					} 
					else if (door.equals(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_DOOR_ALARM)).getValue())) {
						alarm = true;
						showAlarmAnimation(component, imageView, R.drawable.ic_door_alarm_1, R.drawable.ic_door_alarm, true);
					}
					else {
						imageView.setBackgroundResource(R.drawable.ic_door_disabled);
					}
				}
			}
		}
		
		if (alarm) {
			Settings.OBU_REFRESH_TIME = Integer.parseInt(((AppSetting)Settings.appSettings.get(Settings.APP_STATE_ALARM_REFRESH_TIME)).getValue());
			handler.removeCallbacks(startRefresh);
			handler.postDelayed(startRefresh, Settings.OBU_REFRESH_TIME);
		}
		else {
			handler.removeCallbacks(startRefresh);
			//Settings.OBU_REFRESH_TIME = Utils.getPrefernciesInt(MainActivity.this, Settings.SETTING_REFRESH_TIME);
		}
	}
	
	private void showAlarmAnimation (FrameLayout layout, final ImageView imageView, int anim1, int anim2, boolean transition) {
        LinearLayout main = (LinearLayout)layout.findViewById(R.id.main);
        main.setBackgroundResource(0);
	     
        LinearLayout background = (LinearLayout)layout.findViewById(R.id.background);
        background.setBackgroundResource(R.drawable.alarm_confirm);
		Animation anim = new AlphaAnimation(0, 1);
		anim.setDuration(getResources().getInteger(R.integer.animation_interval));
		anim.setRepeatCount(-1);
		anim.setRepeatMode(Animation.REVERSE);
		background.startAnimation(anim);
        		
		if (imageView != null) {
			if (transition) {
				CyclicTransitionDrawable ctd = new CyclicTransitionDrawable(new Drawable[] { 
						  getResources().getDrawable(anim1),
						  getResources().getDrawable(anim2)
				});
				imageView.setBackgroundDrawable(ctd);
				ctd.startTransition(getResources().getInteger(R.integer.animation_interval), 0);
			}
			else {
				imageView.setBackgroundResource(anim1);
				AnimationDrawable imageViewAnimation = (AnimationDrawable) imageView.getBackground();
				imageViewAnimation.start();
			}
		}
		
	    //TypedArray a = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.text_content});     
        ObjectAnimator colorFadeLabel = ObjectAnimator.ofObject((TextView)layout.findViewById(R.id.label), "textColor", 
				new ArgbEvaluator(), 
				getResources().getColor(stylesAttributes.getResourceId(1, 0)), 
				getResources().getColor(R.color.text_alarm_title));
        colorFadeLabel.setDuration(getResources().getInteger(R.integer.animation_interval));
        colorFadeLabel.setRepeatCount(-1);
        colorFadeLabel.setRepeatMode(Animation.REVERSE);
        colorFadeLabel.start();
		
		alarmAnimaations.put(layout.getId(), null);
		alarmAnimaations.put(layout.getId()+1, colorFadeLabel);
		
	    //TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.ic_logotype_alarm});     
        int logoId = stylesAttributes.getResourceId(4, 0);       
		((ImageView)findViewById(R.id.actionBarLogo)).setImageResource(logoId);
	}

	
	private void showAlarmAccuAnimation (FrameLayout layout, TextView textView) {
        LinearLayout main = (LinearLayout)layout.findViewById(R.id.main);
        main.setBackgroundResource(0);
        System.out.println("main="+main);
	     
        LinearLayout background = (LinearLayout)layout.findViewById(R.id.background);
        background.setBackgroundResource(R.drawable.alarm_confirm);
		Animation anim = new AlphaAnimation(0, 1);
		anim.setDuration(getResources().getInteger(R.integer.animation_interval));
		anim.setRepeatCount(-1);
		anim.setRepeatMode(Animation.REVERSE);
		background.startAnimation(anim);
        System.out.println("background="+background);
        
        ObjectAnimator colorFade = ObjectAnimator.ofObject(textView, "textColor", 
				new ArgbEvaluator(), 
				getResources().getColor(R.color.alarm_red), 
				getResources().getColor(R.color.text_alarm_title));
		colorFade.setDuration(getResources().getInteger(R.integer.animation_interval));
		colorFade.setRepeatCount(-1);
		colorFade.setRepeatMode(Animation.REVERSE);
		colorFade.start();
		
	    //TypedArray a = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.text_content});     
        ObjectAnimator colorFadeLabel = ObjectAnimator.ofObject((TextView)layout.findViewById(R.id.label), "textColor", 
				new ArgbEvaluator(), 
				getResources().getColor(stylesAttributes.getResourceId(1, 0)), 
				getResources().getColor(R.color.text_alarm_title));
        colorFadeLabel.setDuration(getResources().getInteger(R.integer.animation_interval));
        colorFadeLabel.setRepeatCount(-1);
        colorFadeLabel.setRepeatMode(Animation.REVERSE);
        colorFadeLabel.start();
		
		alarmAnimaations.put(layout.getId(), colorFade);
		alarmAnimaations.put(layout.getId()+1, colorFadeLabel);
		
	    //TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.ic_logotype_alarm});     
        int logoId = stylesAttributes.getResourceId(4, 0);       
		((ImageView)findViewById(R.id.actionBarLogo)).setImageResource(logoId);
	}

	private void cancelAlarmAnimation (FrameLayout layout, TextView tv, boolean cancelAccuNapetostAnimation) {
		if (alarmAnimaations.containsKey(layout.getId())) {
	        ObjectAnimator colorFade = (ObjectAnimator)alarmAnimaations.get(layout.getId());
	        if (colorFade != null && cancelAccuNapetostAnimation) {
	        	colorFade.end();
	        	tv.setTextColor(getResources().getColor(R.color.text_green));
	        }
	        
	        ObjectAnimator colorFadeLabel = (ObjectAnimator)alarmAnimaations.get(layout.getId()+1);
	        if (colorFadeLabel != null) {
	        	colorFadeLabel.end();
	    	    //TypedArray a = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.text_content});     
	    	    ((TextView)layout.findViewById(R.id.label)).setTextColor(getResources().getColor(stylesAttributes.getResourceId(1, 0)));
	        }
			
	        LinearLayout background = (LinearLayout)layout.findViewById(R.id.background);
	        background.clearAnimation();

	        //TypedArray a1 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.component});     
	        int backgroundId = stylesAttributes.getResourceId(2, 0);       
	        ((LinearLayout)layout.findViewById(R.id.main)).setBackgroundColor(getResources().getColor(backgroundId));
	        
		    //TypedArray a2 = getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(this, Settings.SETTING_THEME), new int[] {R.attr.ic_logotype});     
	        int logoId = stylesAttributes.getResourceId(3, 0);       
			((ImageView)findViewById(R.id.actionBarLogo)).setImageResource(logoId);
		}
	}

	
	public void changeAccuStep (View v) {
		if (!isAccuConnected) return;
		FrameLayout component = (FrameLayout)findViewById(accuComponentId);
		//LinearLayout ll = (LinearLayout)((LinearLayout)(component).getChildAt(0)).getChildAt(1);	
		TextView tvAccuNapetost = (TextView)component.findViewById(R.id.accu_napetost);
		TextView tvAccuAH = (TextView)component.findViewById(R.id.accu_ah);
		TextView tvAccuTok = (TextView)component.findViewById(R.id.accu_tok);
		ImageView ivStep = (ImageView)component.findViewById(R.id.step);
		
		switch (accuStep) {
		case 0:
			tvAccuNapetost.setVisibility(View.VISIBLE);
			tvAccuAH.setVisibility(View.GONE);
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
			tvAccuNapetost.setVisibility(View.GONE);
			tvAccuAH.setVisibility(View.VISIBLE);
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
		
		dialogAlarm.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialogAlarm.show();
	}
	
	
	private void showNotification(int id, String title, String message, Timestamp date, int vibrate, int sound){
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("alarmId", id);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		String uri = "drawable/"+ ((Alarm)Settings.alarms.get(id)).getIcon(); 
		int imageResource = getResources().getIdentifier(uri, null, getPackageName());
		Bitmap bm = BitmapFactory.decodeResource(getResources(), imageResource);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_notification)
		        .setLargeIcon(bm)
		        .setColor(getResources().getColor(R.color.background_night))
		        //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.notification))
		        .setContentTitle(title)
		        .setContentText(message)
		        .setAutoCancel(true)
		        .setContentIntent(pIntent)
		        .setWhen(date.getTime());
		
		NotificationManager mNotificationManager =  (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.build());
		
		beepVibrate(vibrate, sound);
	}

		
	private void cancelNotification(int notifyId) {
	    NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	    nMgr.cancel(notifyId);
	}
	
	private void beepVibrate(int vibrate, int sound) {
	    try {
	        if (sound == 1 && (Utils.getPrefernciesBoolean(this, Settings.SETTING_PLAY_SOUND, false))) {
		    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
	        }
	    } catch (Exception e) {}
	    if (vibrate == 1 && (Utils.getPrefernciesBoolean(this, Settings.SETTING_VIBRATE, false))) {
	    	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
	    }
	}
	
    private Runnable startRefresh = new Runnable() {
	   @Override
	   public void run() {
		   getObudata();
		   getObuHistoryData();
		   handler.postDelayed(startRefresh, Settings.OBU_REFRESH_TIME);
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

	/*
    public class ComponentsAdapter extends BaseAdapter {
  	  
        @SuppressLint("NewApi")
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	LayoutInflater inflater = getLayoutInflater();
        	
			View component = null;
			
			ObuComponent obuComponent = (ObuComponent)getItem(position);
			int lc = R.layout.list_component;
			if (obuComponent.getType().equals("ACCU")) { 
				lc = R.layout.list_component_accu;
			}

			component = inflater.inflate(lc, null);
		    component.setId(obuComponent.getId_component());
		    
			TextView label = ((TextView)component.findViewById(R.id.label));
			label.setText(obuComponent.getName());
		    ((TextViewFont)component.findViewById(R.id.label)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
	        
			
			if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_GEO)) {
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_geofence_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(0);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_PUMP)) {
			    ((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_bilgepump_disabled);
			    label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(1);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ANCHOR)) { 
			    ((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_anchor_disabled); 
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(2);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_ACCU)) { 
				((TextView)component.findViewById(R.id.accu_napetost)).setText("");
				accuComponentId = obuComponent.getId_component();
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
						showSetting(3);
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_LIGHT)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_light_disabled);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_FAN)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_fan_disabled);
				ImageView imageView = (ImageView)component.findViewById(R.id.logo);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
					}
				});
			} else if (obuComponent.getType().equals(Settings.COMPONENT_TYPE_DOOR)) { 
				((ImageView)component.findViewById(R.id.logo)).setBackgroundResource(R.drawable.ic_door_disabled);
				ImageView imageView = (ImageView)component.findViewById(R.id.logo);
				label.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) { 
					}
				});
			} 
		    
			if (position == getCount()) {
				((LinearLayout)component.findViewById(R.id.line)).setVisibility(View.GONE);
			}

            return component;
        }

		@Override
		public int getCount() {
			return Settings.obuComponents.size();
		}

		@Override
		public Object getItem(int position) {
	        Integer key = (Integer) Settings.obuComponents.keySet().toArray()[position]; 
			return Settings.obuComponents.get(key);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
	
    }*/


}
