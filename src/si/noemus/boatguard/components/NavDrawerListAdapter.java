package si.noemus.boatguard.components;

 
import java.util.ArrayList;

import si.noemus.boatguard.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class NavDrawerListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
     
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }
 
    @Override
    public int getCount() {
        return navDrawerItems.size();
    }
 
    @Override
    public Object getItem(int position) {       
        return navDrawerItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
          
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtText = (TextView) convertView.findViewById(R.id.text);
        ImageView img = (ImageView) convertView.findViewById(R.id.iv_forward);
        LinearLayout item = (LinearLayout) convertView.findViewById(R.id.layout_item);
          
        txtTitle.setText(navDrawerItems.get(position).getTitle());
        txtTitle.setTextColor(navDrawerItems.get(position).getTitleColor());
        txtText.setText(navDrawerItems.get(position).getText());
        txtText.setTextColor(navDrawerItems.get(position).getTextColor());
        img.setImageResource(navDrawerItems.get(position).getImage());
        item.setBackgroundColor(navDrawerItems.get(position).getColorBackground());
         
        return convertView;
    }
 
    public void updateData(String str){
        for (int i=0; i<navDrawerItems.size(); i++){
        	NavDrawerItem item = navDrawerItems.get(i);
        	item.setText(str);
        	navDrawerItems.set(i, item);
        }
    	notifyDataSetChanged();
    }    
}