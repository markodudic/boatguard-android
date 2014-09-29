package si.noemus.boatguard.components;

import si.noemus.boatguard.R;
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

    private float letterSpacing = LetterSpacing.NORMAL;
    private Editable originalText;

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
    	
        originalText = super.getText();
        letterSpacing = array.getFloat(R.styleable.boatguard_letter_spacing, getResources().getInteger(R.integer.letter_spacing_small));
        if (letterSpacing != 0) {
        	applyLetterSpacing();
        }
        this.invalidate();

    }
    
    public EditTextFont(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        if (letterSpacing != 0) {
        	applyLetterSpacing();
        }
    }

    /*@Override
    public void setText(CharSequence text, BufferType type) {
        originalText.append(text);
        applyLetterSpacing();
    }*/

    @Override
    public Editable getText() {
        return originalText;
    }

    private void applyLetterSpacing() {
        StringBuilder builder = new StringBuilder();
        if (originalText == null) return;
        for(int i = 0; i < originalText.length(); i++) {
            String c = ""+ originalText.charAt(i);
            builder.append(c);
            if(i+1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }

    public class LetterSpacing {
        public final static float NORMAL = 0;
        public final static float NORMALBIG = (float)0.025;
        public final static float BIG = (float)0.05;
        public final static float BIGGEST = (float)0.2;
    }
     
}
