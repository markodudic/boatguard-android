package si.noemus.boatguard;

import android.app.ActionBar;
import android.app.Activity;
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
        final ScrollView sv = (ScrollView)findViewById(R.id.scroll_main);
        sv.setOnTouchListener(new View.OnTouchListener() {
           @Override
            public boolean onTouch(View v, MotionEvent event) {
        	    switch (event.getAction()) {
                case MotionEvent.ACTION_SCROLL:
                	break;
                case MotionEvent.ACTION_MOVE:
                	int newPosition = sv.getScrollY();
                	if (newPosition > initialPosition) {
             	   		System.out.println("up");
             	   		LinearLayout lMenu = (LinearLayout)findViewById(R.id.layout_menu);
	             	   	TranslateAnimation anim=new TranslateAnimation(0,0,lMenu.getY(),170);
	             	   	anim.setDuration(1000);
	             	    anim.setFillAfter(true);
	             	    lMenu.setAnimation(anim);
             	   
                	} else if (newPosition < initialPosition) {
                		if (newPosition == 0 && initialPosition != 0) {
                			System.out.println("refresh");
                			TextView tvLastUpdate = (TextView)findViewById(R.id.tv_last_update);
                			tvLastUpdate.setVisibility(View.GONE);	
                			ImageView ivRefresh = (ImageView)findViewById(R.id.iv_refresh);
                			ivRefresh.setVisibility(View.VISIBLE);	
                			
                			
                			//TODO naredim resfresh podatkov in vrnem last update
                		}
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
