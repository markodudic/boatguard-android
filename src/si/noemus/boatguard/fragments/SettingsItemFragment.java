package si.noemus.boatguard.fragments;

import si.noemus.boatguard.R;
import si.noemus.boatguard.R.attr;
import si.noemus.boatguard.R.id;
import si.noemus.boatguard.R.layout;
import si.noemus.boatguard.activities.SettingsActivity;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SettingsItemFragment  extends Fragment {
	
	private Bundle args = null;
	private View v = null;
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings_item, null);

        args = getArguments();
        ((TextViewFont)v.findViewById(R.id.title)).setText(args.getString("title"));
        ((TextViewFont)v.findViewById(R.id.text)).setText(args.getString("text"));
        
        TypedArray img = getActivity().getTheme().obtainStyledAttributes(Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME), new int[] {R.attr.ic_forward});     
        ImageView ivForward = (ImageView)v.findViewById(R.id.iv_forward);
        ivForward.setImageDrawable(getResources().getDrawable(img.getResourceId(0, 0)));
        ivForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				openSetting();
			}
		});
        
        v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				openSetting();
			}
		});
 		  
 		  
        return v;
    }
 
    private void openSetting(){
    	Intent i = new Intent(getActivity(), SettingsActivity.class);
		i.putExtra("id", args.getInt("id"));
		i.putExtra("title", args.getString("title"));
		startActivity(i);
    }
    
    public void setText(String text, int id){
    	((TextViewFont)v.findViewById(R.id.text)).setText(text);
    	if (id != 0) {
    		((TextViewFont)v.findViewById(R.id.text)).setTextColor(getResources().getColor(id));
    	}
    }    
}
