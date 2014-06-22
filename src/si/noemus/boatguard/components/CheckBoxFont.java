package si.noemus.boatguard.components;

import si.noemus.boatguard.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckBoxFont extends CheckBox {

	public CheckBoxFont(Context context) { 
    	super(context);
    }
	
    public CheckBoxFont(Context context, AttributeSet attrs) { 
    	super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.boatguard, 0, 0);
		String fontName = array.getString(R.styleable.boatguard_font_name);
		//if (fontName == null) fontName = context.getString(R.string.font_semibold);
		Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);  
    	this.setTypeface(font); 
    }
}
