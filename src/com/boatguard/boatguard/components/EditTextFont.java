package com.boatguard.boatguard.components;

import com.boatguard.boatguard.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextFont extends EditText {

    public EditTextFont(Context context) { 
    	super(context);
    }
	
    public EditTextFont(Context context, AttributeSet attrs) { 
    	super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.boatguard, 0, 0);
		String fontName = array.getString(R.styleable.boatguard_font_name);
		//if (fontName == null) fontName = context.getString(R.string.font_semibold);
		Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);  
    	this.setTypeface(font); 
    }

     
}
