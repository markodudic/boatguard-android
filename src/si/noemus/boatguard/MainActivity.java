package si.noemus.boatguard;

import si.noemus.boatguard.utils.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private int initialPosition;
	private boolean refreshing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_icon);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        
        /*if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
        final TextView tvLastUpdate = (TextView)findViewById(R.id.tv_last_update);
        final ImageView ivRefresh = (ImageView)findViewById(R.id.iv_refresh);
        final AnimationDrawable refreshAnimation = (AnimationDrawable) ivRefresh.getBackground();

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
                			tvLastUpdate.setVisibility(View.GONE);	
                			ivRefresh.setVisibility(View.VISIBLE);	
                			refreshAnimation.start();
                			
                			//TODO naredim resfresh podatkov in vrnem last update
                			refreshing = true;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
*/
}
