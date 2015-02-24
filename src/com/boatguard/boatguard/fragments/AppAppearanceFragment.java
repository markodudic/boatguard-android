package com.boatguard.boatguard.fragments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.MainActivity;
import com.boatguard.boatguard.activities.SettingsActivity;
import com.boatguard.boatguard.activities.SplashScreenActivity;
import com.boatguard.boatguard.objects.ObuSetting;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;

public class AppAppearanceFragment  extends Fragment {
	
	private int langPos = -1;
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_app_appearance, null);

        final Spinner spinnerRefreshTime = (Spinner) v.findViewById(R.id.spinner_refresh_time);
        MySpinnerAdapter adapter = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.refresh))
        );
        spinnerRefreshTime.setAdapter(adapter);
        spinnerRefreshTime.setSelection(Utils.getIndex(spinnerRefreshTime, Utils.getPrefernciesInt(getActivity(), Settings.SETTING_REFRESH_TIME)/60/1000+""), false);
        spinnerRefreshTime.setOnItemSelectedListener(new OnItemSelectedListener() {
 			@Override
 			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
 				String val = (String)spinnerRefreshTime.getSelectedItem();
		        HashMap<Integer,ObuSetting> obuSettings = Settings.obuSettings;
		        obuSettings.get(Settings.getObuSetting(Settings.SETTING_REFRESH_TIME)).setValue(val.length()==1?'0'+val:val);
		        Settings.setObuSettings(getActivity());
		        Utils.savePrefernciesInt(getActivity(), Settings.SETTING_REFRESH_TIME, Integer.parseInt(val)*60*1000);
			}


			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});	
        
        final Switch switchColorTheme = (Switch) v.findViewById(R.id.switch_color_theme);
        switchColorTheme.setChecked(Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME) == R.style.AppThemeDay);
        switchColorTheme.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				if (switchColorTheme.isChecked()) {
					getActivity().setTheme(R.style.AppThemeDay);			
					Utils.savePrefernciesInt(getActivity(), Settings.SETTING_THEME, R.style.AppThemeDay);
				} else {
					getActivity().setTheme(R.style.AppThemeNight);			
					Utils.savePrefernciesInt(getActivity(), Settings.SETTING_THEME, R.style.AppThemeNight);					
				}
				Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
			}
		});	

		final Spinner spinnerLanguage = (Spinner) v.findViewById(R.id.language);
        MySpinnerAdapter adapterC = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.languages))
        );
        spinnerLanguage.setAdapter(adapterC);
		String lang = Utils.getPrefernciesString(getActivity(), Settings.SETTING_LANG);
		langPos = SplashScreenActivity.languageCodes.indexOf(lang);
        spinnerLanguage.setSelection(langPos);
        spinnerLanguage.setOnItemSelectedListener(spinnerSelector);

        return v;
    }
    
    private void changeLanguage(String lang) {
		SplashScreenActivity.setLanguage(getActivity(), lang); 
		Intent i = new Intent(getActivity(), SettingsActivity.class);
		i.putExtra("id", -1);
		i.putExtra("title", getResources().getString(R.string.menu));
		startActivity(i);
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Dosis-Regular.otf");

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            view.setGravity(Gravity.LEFT);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            view.setGravity(Gravity.LEFT);
            return view;
        }
    }     
    
    private OnItemSelectedListener spinnerSelector = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_default_day));
            ((TextView) parent.getChildAt(0)).setTextSize(getResources().getDimension(R.dimen.spinner_text_size));
            ((TextView) parent.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.transparent_color));
    		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Dosis-Medium.otf");  
    		((TextView) parent.getChildAt(0)).setTypeface(font); 
    		if (pos != langPos) {
    			changeLanguage(SplashScreenActivity.languageCodes.get(pos));
    		}
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };        
    
}
