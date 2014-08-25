package si.noemus.boatguard.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import si.noemus.boatguard.R;
import si.noemus.boatguard.components.TextViewFont;
import si.noemus.boatguard.objects.ObuState;
import si.noemus.boatguard.objects.State;
import si.noemus.boatguard.utils.Comm;
import si.noemus.boatguard.utils.Comm.OnTaskCompleteListener;
import si.noemus.boatguard.utils.Settings;
import si.noemus.boatguard.utils.Utils;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

public class HistoryFragment  extends Fragment {
	private static Gson gson = new Gson();	
	private ListView lvHistory = null;
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        final View v = inflater.inflate(R.layout.fragment_history, null);
        lvHistory = (ListView)v.findViewById(R.id.lv_history);
        
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        
        getObuHistoryData();

        return v;
    }

	public void getObuHistoryData() {
    	String obuId = Utils.getPrefernciesString(getActivity(), Settings.SETTING_OBU_ID);
   		
    	String urlString = this.getString(R.string.server_url) + "gethistorydata?obuid="+obuId;
    	if (Utils.isNetworkConnected(getActivity(), false)) {
            	Comm at = new Comm();
    			at.setCallbackListener(clGetObuHistoryData);
    			at.execute(urlString, null); 
    	}
	}
	
    private OnTaskCompleteListener clGetObuHistoryData = new OnTaskCompleteListener() {

        @Override
        public void onComplete(String res) {
        	//System.out.println("RES="+res);
        	 
        	JSONObject jRes;
			try {
				jRes = (JSONObject)new JSONTokener(res).nextValue();
	    	   	JSONArray jsonStates = (JSONArray)jRes.get("states");
	    	   	String[] values = new String[jsonStates.length()];
	        	for (int i=0; i< jsonStates.length(); i++) {
	    	   		JSONArray jsonState = (JSONArray)jsonStates.get(i);
    	   			//System.out.println("jsonState="+jsonState.toString());
	    	   		for (int ii=0; ii< jsonState.length(); ii++) {
	    	   			ObuState obuState = gson.fromJson(jsonState.get(ii).toString(), ObuState.class);
	    	   			if (obuState.getId_state() == ((State)Settings.states.get(Settings.STATE_ROW_STATE)).getId()) {
	    	   				values[i] = obuState.getId_state() + ":" + obuState.getValue() + "; "; 
	    	   			}
	    	   		
	    	   		}
		   		}
		   		final HistoryAdapter adapter = new HistoryAdapter(getActivity(), values);
		   		lvHistory.setAdapter(adapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    public class HistoryAdapter extends ArrayAdapter<String> {
    	private final Context context;
    	private final String[] values;
    	  
		public HistoryAdapter(Context context, String[] values) {
		    super(context, R.layout.row_history, values);
		    this.context = context;
		    this.values = values;
		}
    	  
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.row_history, parent, false);
			TextViewFont tvHistory = (TextViewFont)v.findViewById(R.id.tv_history);
			tvHistory.setText(values[position]);
	        
			return v;
		}
    }
}



