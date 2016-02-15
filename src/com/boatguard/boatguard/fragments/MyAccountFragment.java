package com.boatguard.boatguard.fragments;

import java.util.Arrays;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boatguard.boatguard.R;
import com.boatguard.boatguard.activities.LoginActivity;
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
				etEmail.requestFocus();
			} 
		});

        final EditText etPhoneNumber = (EditText) v.findViewById(R.id.phone_number);
        etPhoneNumber.setText(Settings.customer.getPhone_number());
		ImageView ivPhoneNumber = (ImageView) v.findViewById(R.id.iv_phone_number);
		ivPhoneNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etPhoneNumber.setText("");
				etPhoneNumber.requestFocus();
			} 
		});
		
		final EditText etName = (EditText) v.findViewById(R.id.name);
		etName.setText(Settings.customer.getName());
		ImageView ivName = (ImageView) v.findViewById(R.id.iv_name);
		ivName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etName.setText("");
				etName.requestFocus();
			} 
		});

		final EditText etSurname = (EditText) v.findViewById(R.id.surname);
		etSurname.setText(Settings.customer.getSurname());
		ImageView ivSurname = (ImageView) v.findViewById(R.id.iv_surname);
		ivSurname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etSurname.setText("");
				etSurname.requestFocus();
			} 
		});

		final Spinner spinnerBirthyear = (Spinner) v.findViewById(R.id.birthyear);
        MySpinnerAdapter adapterB = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.birthyear))
        );
        spinnerBirthyear.setAdapter(adapterB);
		spinnerBirthyear.setSelection(Utils.getIndex(spinnerBirthyear, Settings.customer.getBirth_year()+""));
		spinnerBirthyear.setOnItemSelectedListener(spinnerSelector);

		
		final Spinner spinnerCountry = (Spinner) v.findViewById(R.id.country);
        MySpinnerAdapter adapterC = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.country))
        );
        spinnerCountry.setAdapter(adapterC);
		spinnerCountry.setSelection(Utils.getIndex(spinnerCountry, Settings.customer.getCountry()));
		spinnerCountry.setOnItemSelectedListener(spinnerSelector);
		
		final EditText etBoatname = (EditText) v.findViewById(R.id.boatname);
		etBoatname.setText(Settings.customer.getBoat_name());
		ImageView ivBoatname = (ImageView) v.findViewById(R.id.iv_boatname);
		ivBoatname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatname.setText("");
				etBoatname.requestFocus();
			} 
		});
		
		final Spinner spinnerManafacturer = (Spinner) v.findViewById(R.id.boatmanafacturer);
        MySpinnerAdapter adapterMF = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.manafacturer))
        );
        spinnerManafacturer.setAdapter(adapterMF);
		spinnerManafacturer.setSelection(Utils.getIndex(spinnerManafacturer, Settings.customer.getBoat_manafacturer()));
		spinnerManafacturer.setOnItemSelectedListener(spinnerSelector);
		
		final Spinner spinnerModel = (Spinner) v.findViewById(R.id.boatmodel);
        MySpinnerAdapter adapterM = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.model))
        );
        spinnerModel.setAdapter(adapterM);
		spinnerModel.setSelection(Utils.getIndex(spinnerModel, Settings.customer.getBoat_model()));
		spinnerModel.setOnItemSelectedListener(spinnerSelector);
		
		final Spinner spinnerBoatCountry = (Spinner) v.findViewById(R.id.boatcountry);
        MySpinnerAdapter adapterBC = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.country))
        );
        spinnerBoatCountry.setAdapter(adapterBC);
		spinnerBoatCountry.setSelection(Utils.getIndex(spinnerBoatCountry, Settings.customer.getBoat_country()));
		spinnerBoatCountry.setOnItemSelectedListener(spinnerSelector);
		
		EditText etUsername = (EditText) v.findViewById(R.id.username);
		etUsername.setText(Settings.customer.getUsername());
		EditText etObuId = (EditText) v.findViewById(R.id.obu_id);
		etObuId.setText(Settings.customer.getSerial_number());
		
		final EditText etPassword1 = (EditText) v.findViewById(R.id.password1);
		etPassword1.setText(Settings.customer.getPassword());
		ImageView ivPassword1 = (ImageView) v.findViewById(R.id.iv_password1);
		ivPassword1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etPassword1.setText("");
				etPassword1.requestFocus();
			} 
		});
		final EditText etPassword2 = (EditText) v.findViewById(R.id.password2);
		etPassword2.setText(Settings.customer.getPassword());
		ImageView ivPassword2 = (ImageView) v.findViewById(R.id.iv_password2);
		ivPassword2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etPassword2.setText("");
				etPassword2.requestFocus();
			} 
		});

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
				if (etPassword1.getText().toString().equalsIgnoreCase(etPassword2.getText().toString())) {
					Settings.customer.setPassword(etPassword1.getText().toString());
					Settings.customer.setName(etName.getText().toString());
					Settings.customer.setSurname(etSurname.getText().toString());
					Settings.customer.setBirth_year(Integer.parseInt((String)spinnerBirthyear.getSelectedItem()));
					Settings.customer.setCountry((String)spinnerCountry.getSelectedItem());
					Settings.customer.setBoat_name(etBoatname.getText().toString());
					Settings.customer.setBoat_manafacturer((String)spinnerManafacturer.getSelectedItem());
					Settings.customer.setBoat_model((String)spinnerModel.getSelectedItem());
					Settings.customer.setBoat_country((String)spinnerBoatCountry.getSelectedItem());
					Settings.customer.setEmail(etEmail.getText().toString());
					Settings.customer.setPhone_number(etPhoneNumber.getText().toString());
					Settings.setCustomer(getActivity());
					getActivity().finish();
				}
				else {
		        	Toast toast = Toast.makeText(getActivity(), getString(R.string.password_error), Toast.LENGTH_LONG);
		        	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		        	toast.show();					
				}
			} 
		});
		
   		return v;
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
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };    
     	
}
