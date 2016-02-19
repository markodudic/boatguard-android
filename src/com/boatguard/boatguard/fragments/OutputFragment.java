package com.boatguard.boatguard.fragments;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.components.TextViewFont;
import com.boatguard.boatguard.objects.ObuAlarm;
import com.boatguard.boatguard.objects.ObuComponent;
import com.boatguard.boatguard.objects.Setting;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;

public class OutputFragment  extends Fragment {
	protected LayoutInflater inflater;
	String type = null;
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_output, null);
        this.inflater = inflater;
        
        type = getArguments().getString("type");    
        
		final EditText etName = (EditText) v.findViewById(R.id.outputname);
		etName.setText(getArguments().getString("name"));
		etName.addTextChangedListener(new TextWatcher(){
		    public void afterTextChanged(Editable s) {
		        HashMap<Integer,ObuComponent> obuComponents = Settings.obuComponents;
		        obuComponents.get(Integer.parseInt(type)).setLabel(etName.getText().toString());
		    }
		    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		    public void onTextChanged(CharSequence s, int start, int before, int count){}
		}); 
		ImageView ivName = (ImageView) v.findViewById(R.id.iv_outputname);
		ivName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etName.setText("");
				etName.requestFocus();
			} 
		});

			
   		return v;
    }
    
    
}
