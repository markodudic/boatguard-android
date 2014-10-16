package com.boatguard.boatguard.widget;

import org.json.JSONArray;
import org.json.JSONObject;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.boatguard.boatguard.utils.Comm;
import com.boatguard.boatguard.utils.Utils;


/**
 * The AppWidgetProvider for our sample MojTelekom widget.
 */
public class BoatGuardDataProvider extends ContentProvider {
	public static final String TAG = "BoatGuardDataProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://com.boatguard.boatguard.widget.provider");

    public static class Columns {
        public static final String ID = "_id";
        public static final String TEMPLATEID = "templateId";
        public static final String ACCOUNT = "AccountIdentification";
        public static final String PRIORITYID = "priorityId";
        public static final String LEAD = "lead";
        public static final String UNITVALUE = "unitValue";
        public static final String SUFFIX = "suffix";
        public static final String PERCENTAGE = "percentage";
    }

    @Override
    public boolean onCreate() {
        return true;
    }


    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        assert(uri.getPathSegments().isEmpty());

        final MatrixCursor c = new MatrixCursor(new String[]{ Columns.ID, Columns.TEMPLATEID, Columns.ACCOUNT, Columns.PRIORITYID, Columns.LEAD, Columns.UNITVALUE, Columns.SUFFIX, Columns.PERCENTAGE});
/*
        try {
            String res = null;
            int cnt=0;
            while(res == null) {
                String url = getContext().getResources().getString(R.string.account_summary)+"/"+selection;

                //boolean msidn = Utils.getPrefernciesdataBoolean(getContext(), MojTelekomWidgetProvider.MSIDN_LOGIN, false);
                String user = Utils.getPrefernciesdataString(getContext(), "USERNAME");
                String pass = Utils.getPrefernciesdataString(getContext(), "PASSWORD");
                //TODO samo za test
                if (user == null) {
                	return null;
                }
                AsyncTask at = new Comm().execute(url, user, pass);
                res = (String) at.get();
                Log.d(TAG, "CNT="+(cnt++));
                Log.d(TAG, "Data="+res);

                if (res != null) {
                    JSONObject jObject = new JSONObject(res);
                    int templateId = jObject.getInt("TemplateId");
                    String account = jObject.getString("AccountIdentification");
                    Log.d(TAG, templateId+":"+account);

                    JSONArray jData = jObject.getJSONArray("Data");
                    for (int i = 0; i < jData.length(); ++i) {
                        JSONObject jDataItem= (JSONObject)jData.get(i);
                        String priorityId 	= jDataItem.getString("PriorityId");
                        String lead 		= jDataItem.getString("Lead");
                        String unitValue 	= jDataItem.getString("UnitValue");
                        String suffix 		= jDataItem.getString("Suffix");
                        String percentage 	= jDataItem.getString("Percentage");
                        Log.d(TAG, priorityId+":"+lead+":"+unitValue+":"+suffix+":"+percentage);
                        c.addRow(new Object[]{ new Integer(i), templateId, account, priorityId, lead, unitValue, suffix, percentage });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.BoatGuardlistwidget.temperature";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection,
                                   String[] selectionArgs) {
        Log.d(TAG, "update");
        getContext().getContentResolver().notifyChange(uri, null);

        return 1;
    }

}
