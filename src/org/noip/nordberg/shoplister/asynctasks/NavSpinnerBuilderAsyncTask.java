package org.noip.nordberg.shoplister.asynctasks;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;


public class NavSpinnerBuilderAsyncTask extends AsyncTask<Object, Void, Void>{

	private static final int LIST = 0;
	
	private Cursor cursor;

	@Override
	protected Void doInBackground(Object... objects) {
		
		cursor = AppRef.context.getContentResolver().query(Uri.parse("content://org.noip.nordberg.shoplister/items"),
				new String[]{DBHelper.KEY_LIST} ,null, null, null);
		
		// Setup spinner list items
		Statics.dropdownValues = new ArrayList<String>();
		Statics.dropdownValues = buildSpinnerArray();
        
        // Add footer - bit of a hack but it's the only way
		Statics.dropdownValues.add(AppRef.context.getString(R.string.spinner_footer));
        
        cursor.close();
		return null;

	}
	
	private ArrayList<String> buildSpinnerArray(){
		// Iterate through cursor to create a HashSet of all list names
		cursor.moveToFirst();
		Set<String> listNameSet = new TreeSet<String>();
		while(cursor.getPosition() < cursor.getCount()){   
			String listName = cursor.getString(LIST);
			listNameSet.add(listName);
			cursor.moveToNext();
		} 
		// NavSpinner extends ArrayAdapter, which needs a list.. give it a list
		ArrayList<String> uniqueNameList = new ArrayList<String>(listNameSet);
		return uniqueNameList;
	}
}


