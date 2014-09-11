package si.noemus.boatguard.fragments;

import si.noemus.boatguard.R;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
           android.R.id.text1
    };
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;
   
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        Contacts.DISPLAY_NAME_PRIMARY :
                        Contacts.DISPLAY_NAME

            };
    
 // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;
    
    // Empty public constructor, required by the system
    public ContactsFragment() {}

    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        
        // Gets the ListView from the View list of the parent activity
        mContactsList = (ListView) getActivity().findViewById(R.layout.contact_list_view);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
    }	
    
    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
        /*
    	// Get the Cursor
        Cursor cursor = parent.getAdapter().getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = getString(CONTACT_KEY_INDEX);
        // Create the contact's content Uri
        mContactUri = Contacts.getLookupUri(mContactId, mContactKey);
         */
    }    
	
}