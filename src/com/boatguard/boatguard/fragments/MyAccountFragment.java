package com.boatguard.boatguard.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.utils.Settings;
import com.boatguard.boatguard.utils.Utils;

public class MyAccountFragment  extends Fragment {
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_my_account, null);

        final EditText etEmail = (EditText) v.findViewById(R.id.email);
        etEmail.setText(Settings.customer.getEmail());
		ImageView ivEmail = (ImageView) v.findViewById(R.id.iv_email);
		ivEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etEmail.setText("");
			} 
		});

		final EditText etName = (EditText) v.findViewById(R.id.name);
		etName.setText(Settings.customer.getName());
		ImageView ivName = (ImageView) v.findViewById(R.id.iv_name);
		ivName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etName.setText("");
			} 
		});

		final EditText etSurname = (EditText) v.findViewById(R.id.surname);
		etSurname.setText(Settings.customer.getSurname());
		ImageView ivSurname = (ImageView) v.findViewById(R.id.iv_surname);
		ivSurname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etSurname.setText("");
			} 
		});

		final Spinner spinnerBirthyear = (Spinner) v.findViewById(R.id.birthyear);
		spinnerBirthyear.setSelection(Utils.getIndex(spinnerBirthyear, Settings.customer.getBirth_year()+""));
		spinnerBirthyear.setOnItemSelectedListener(spinnerSelector);

		
		final Spinner spinnerCountry = (Spinner) v.findViewById(R.id.country);
		spinnerCountry.setSelection(Utils.getIndex(spinnerCountry, Settings.customer.getCountry()));
		spinnerCountry.setOnItemSelectedListener(spinnerSelector);
		
		final EditText etBoatname = (EditText) v.findViewById(R.id.boatname);
		etBoatname.setText(Settings.customer.getBoat_name());
		ImageView ivBoatname = (ImageView) v.findViewById(R.id.iv_boatname);
		ivBoatname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatname.setText("");
			} 
		});
		
		final Spinner spinnerManafacturer = (Spinner) v.findViewById(R.id.boatmanafacturer);
		spinnerManafacturer.setSelection(Utils.getIndex(spinnerManafacturer, Settings.customer.getBoat_manafacturer()));
		spinnerManafacturer.setOnItemSelectedListener(spinnerSelector);
		
		final Spinner spinnerModel = (Spinner) v.findViewById(R.id.boatmodel);
		spinnerModel.setSelection(Utils.getIndex(spinnerModel, Settings.customer.getBoat_model()));
		spinnerModel.setOnItemSelectedListener(spinnerSelector);
		
		final Spinner spinnerBoatCountry = (Spinner) v.findViewById(R.id.boatcountry);
		spinnerBoatCountry.setSelection(Utils.getIndex(spinnerBoatCountry, Settings.customer.getBoat_country()));
		spinnerBoatCountry.setOnItemSelectedListener(spinnerSelector);
		
		EditText etUsername = (EditText) v.findViewById(R.id.username);
		etUsername.setText(Settings.customer.getUsername());
		EditText etObuId = (EditText) v.findViewById(R.id.obu_id);
		etObuId.setText(Settings.customer.getSerial_number());
		
        TextView btnCancel = (TextView) v.findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { 
				getActivity().finish();
			}
		});
		
		TextView btnOk = (TextView) v.findViewById(R.id.button_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Settings.customer.setName(etName.getText().toString());
				Settings.customer.setSurname(etSurname.getText().toString());
				Settings.customer.setBirth_year(Integer.parseInt((String)spinnerBirthyear.getSelectedItem()));
				Settings.customer.setCountry((String)spinnerCountry.getSelectedItem());
				Settings.customer.setBoat_name(etBoatname.getText().toString());
				Settings.customer.setBoat_manafacturer((String)spinnerManafacturer.getSelectedItem());
				Settings.customer.setBoat_model((String)spinnerModel.getSelectedItem());
				Settings.customer.setBoat_country((String)spinnerBoatCountry.getSelectedItem());
				Settings.customer.setEmail(etEmail.getText().toString());
				Settings.setCustomer(getActivity());
				getActivity().finish();
			} 
		});
		
   		return v;
    }
    
    private OnItemSelectedListener spinnerSelector = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_default_day));
            ((TextView) parent.getChildAt(0)).setTextSize(getResources().getDimension(R.dimen.spinner_text_size));
            ((TextView) parent.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.transparent_color));
    		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Dosis-Medium.otf");  
    		((TextView) parent.getChildAt(0)).setTypeface(font); 
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };    
     	
}
