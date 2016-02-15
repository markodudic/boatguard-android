package com.boatguard.boatguard.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Switch;

import com.boatguard.boatguard.R;

public class SwitchFont extends Switch {

    public SwitchFont(Context context) { 
    	super(context);
    }
	
    @SuppressLint("NewApi")
	public SwitchFont(Context context, AttributeSet attrs) { 
    	super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.boatguard, 0, 0);
		String fontName = array.getString(R.styleable.boatguard_font_name);
		Typeface font = Typeface.createFromAsset(context.getAssets(), fontName); 
		this.setSwitchTypeface(font); 
		if (Build.VERSION.SDK_INT < 20) {
			this.setThumbDrawable(getResources().getDrawable(R.drawable.switcher_thumb));
		}
    }
    
    public SwitchFont(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }
}
