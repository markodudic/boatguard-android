package com.boatguard.boatguard.fragments;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.R.attr;
import com.boatguard.boatguard.R.id;
import com.boatguard.boatguard.R.layout;

import com.boatguard.boatguard.activities.SettingsActivity;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;
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
        ((TextViewFont)v.findViewById(R.id.title)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
        ((TextViewFont)v.findViewById(R.id.text)).setText(args.getString("text"));
        ((TextViewFont)v.findViewById(R.id.text)).setLetterSpacing(getResources().getInteger(R.integer.letter_spacing_small_set));
        
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
