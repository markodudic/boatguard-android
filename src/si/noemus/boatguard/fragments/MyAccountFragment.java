package si.noemus.boatguard.fragments;

import si.noemus.boatguard.R;
import si.noemus.boatguard.objects.Customer;
import si.noemus.boatguard.utils.Settings;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

		final EditText etBoatname = (EditText) v.findViewById(R.id.boatname);
		etBoatname.setText(Settings.customer.getBoat_name());
		ImageView ivBoatname = (ImageView) v.findViewById(R.id.iv_boatname);
		ivBoatname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View vv) {
				etBoatname.setText("");
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
				Settings.customer.setBoat_name(etBoatname.getText().toString());
				Settings.customer.setEmail(etEmail.getText().toString());
				Settings.setCustomer(getActivity());
				getActivity().finish();
			} 
		});
		
   		return v;
    }
     	
}
