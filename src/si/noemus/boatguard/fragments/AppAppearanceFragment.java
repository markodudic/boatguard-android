package si.noemus.boatguard.fragments;

import si.noemus.boatguard.R;
import si.noemus.boatguard.activities.MainActivity;
import si.noemus.boatguard.activities.SettingsActivity;
import si.noemus.boatguard.activities.SplashScreenActivity;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

public class AppAppearanceFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_app_appearance, null);

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

		TextViewFont tvLanguageEn = (TextViewFont) v.findViewById(R.id.tv_language_en);
        tvLanguageEn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				changeLanguage("en"); 
			}
		});	

        TextViewFont tvLanguageSlo = (TextViewFont) v.findViewById(R.id.tv_language_slo);
        tvLanguageSlo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				changeLanguage("sl"); 
			}
		});	

        TextViewFont tvLanguageHr = (TextViewFont) v.findViewById(R.id.tv_language_hr);
        tvLanguageHr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				changeLanguage("hr"); 
			}
		});	

		int theme = Utils.getPrefernciesInt(getActivity(), Settings.SETTING_THEME);
		String lang = Utils.getPrefernciesString(getActivity(), Settings.SETTING_LANG);
        if (lang.equals("en")) {
			if (theme == R.style.AppThemeDay) {
				setStyle(v, R.color.text_green, R.drawable.ic_bilgepump, R.color.text_default_day, R.drawable.ic_delete_day, R.color.text_default_day, R.drawable.ic_delete_day);
			}
			else {
				setStyle(v, R.color.text_green, R.drawable.ic_bilgepump, R.color.text_default_night, R.drawable.ic_delete, R.color.text_default_night, R.drawable.ic_delete);
			}
		}
		else if (lang.equals("sl")) {
			if (theme == R.style.AppThemeDay) {
				setStyle(v, R.color.text_default_day, R.drawable.ic_delete_day, R.color.text_green, R.drawable.ic_bilgepump, R.color.text_default_day, R.drawable.ic_delete_day);
			}
			else {
				setStyle(v, R.color.text_default_night, R.drawable.ic_delete, R.color.text_green, R.drawable.ic_bilgepump, R.color.text_default_night, R.drawable.ic_delete);
			}
		}
		else if (lang.equals("hr")) {
			if (theme == R.style.AppThemeDay) {
				setStyle(v, R.color.text_default_day, R.drawable.ic_delete_day, R.color.text_default_day, R.drawable.ic_delete_day, R.color.text_green, R.drawable.ic_bilgepump);
			}
			else {
				setStyle(v, R.color.text_default_night, R.drawable.ic_delete, R.color.text_default_night, R.drawable.ic_delete_day, R.color.text_green, R.drawable.ic_bilgepump);
			}
		}

        return v;
    }
    
    private void changeLanguage(String lang) {
		SplashScreenActivity.setLanguage(getActivity(), lang); 
		Intent i = new Intent(getActivity(), SettingsActivity.class);
		i.putExtra("id", -1);
		i.putExtra("title", getResources().getString(R.string.menu));
		startActivity(i);
    }
 
    private void setStyle(View v, int enColor, int enImg, int sloColor, int sloImg, int hrColor, int hrImg) {
		TextViewFont tvLanguageEn = (TextViewFont) v.findViewById(R.id.tv_language_en);
        TextViewFont tvLanguageSlo = (TextViewFont) v.findViewById(R.id.tv_language_slo);
        TextViewFont tvLanguageHr = (TextViewFont) v.findViewById(R.id.tv_language_hr);
		ImageView ivLanguageEn = (ImageView) v.findViewById(R.id.iv_language_en);
		ImageView ivLanguageSlo = (ImageView) v.findViewById(R.id.iv_language_slo);
		ImageView ivLanguageHr = (ImageView) v.findViewById(R.id.iv_language_hr);
		tvLanguageEn.setTextColor(getResources().getColor(enColor));
		ivLanguageEn.setBackgroundResource(enImg);
		tvLanguageSlo.setTextColor(getResources().getColor(sloColor));
		ivLanguageSlo.setBackgroundResource(sloImg);
		tvLanguageHr.setTextColor(getResources().getColor(hrColor));
		ivLanguageHr.setBackgroundResource(hrImg);
    }
    
}
