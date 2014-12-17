package com.boatguard.boatguard.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

		final EditText etCountry = (EditText) v.findViewById(R.id.country);
		etCountry.setText(Settings.customer.getCountry());
		ImageView ivCountry = (ImageView) v.findViewById(R.id.iv_country);
		ivCountry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etCountry.setText("");
			} 
		});
		
		final EditText etBoatname = (EditText) v.findViewById(R.id.boatname);
		etBoatname.setText(Settings.customer.getBoat_name());
		ImageView ivBoatname = (ImageView) v.findViewById(R.id.iv_boatname);
		ivBoatname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatname.setText("");
			} 
		});
		
		final EditText etBoatmanafacturer = (EditText) v.findViewById(R.id.boatmanafacturer);
		etBoatmanafacturer.setText(Settings.customer.getBoat_manafacturer());
		ImageView ivBoatmanafacturer = (ImageView) v.findViewById(R.id.iv_boatmanafacturer);
		ivBoatmanafacturer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatmanafacturer.setText("");
			} 
		});
		
		final EditText etBoatmodel = (EditText) v.findViewById(R.id.boatmodel);
		etBoatmodel.setText(Settings.customer.getBoat_model());
		ImageView ivBoatmodel = (ImageView) v.findViewById(R.id.iv_boatmodel);
		ivBoatmodel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatmodel.setText("");
			} 
		});
		
		final EditText etBoatcountry = (EditText) v.findViewById(R.id.boatcountry);
		etBoatcountry.setText(Settings.customer.getBoat_country());
		ImageView ivBoatcountry = (ImageView) v.findViewById(R.id.iv_boatcountry);
		ivBoatcountry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatcountry.setText("");
			} 
		});
		
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
				Settings.customer.setCountry(etCountry.getText().toString());
				Settings.customer.setBoat_name(etBoatname.getText().toString());
				Settings.customer.setBoat_manafacturer(etBoatmanafacturer.getText().toString());
				Settings.customer.setBoat_model(etBoatmodel.getText().toString());
				Settings.customer.setBoat_country(etBoatcountry.getText().toString());
				Settings.customer.setEmail(etEmail.getText().toString());
				Settings.setCustomer(getActivity());
				getActivity().finish();
			} 
		});
		
   		return v;
    }
     	
}
