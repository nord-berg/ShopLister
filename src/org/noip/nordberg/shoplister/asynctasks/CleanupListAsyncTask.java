package org.noip.nordberg.shoplister.asynctasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

public class CleanupListAsyncTask extends AsyncTask<Integer, Void, Void>{

	private static final int ROW_ID = 0;
	private static final int CHECKED = 3;
	
	private Cursor cursor;
	private ContentResolver contentResolver;
	private ContentValues contentValuesArray[];

	@Override
	protected Void doInBackground(Integer... integer) {

		List<String> rowIdList = new ArrayList<String>();
		contentResolver = AppRef.context.getContentResolver();
		
		// Get a cursor and create a list of items to modify in DB
		cursor = getCursor();
		cursor.moveToFirst();
		while(cursor.getPosition() < cursor.getCount()){   
			int checkedStatus = Integer.parseInt(cursor.getString(CHECKED));
			final int rowId = (cursor.getInt(ROW_ID));
			if (checkedStatus == 1) {
				rowIdList.add(Integer.toString(rowId));
			}
			cursor.moveToNext();
		};
		cursor.close();
		
		// Modify items in DB
		bulkCleanDb(rowIdList);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		
		Statics.rowsToHideAfterAnim.clear();
		super.onPostExecute(result);
	}
	
	private void bulkCleanDb(List<String> rows){
		Uri uri = Uri.parse("content://org.noip.nordberg.shoplister/items");
		// Make a ContentValues template and fill it to an array, because every
		// item will be updated to the same values
		ContentValues newValues = new ContentValues();
		newValues.put("quantity", 0);
		newValues.put("extras", "");
		newValues.put("checked", 0);
		contentValuesArray = new ContentValues[rows.size()];
		Arrays.fill(contentValuesArray, newValues);

		// This bundle BS is because i can't call custom methods in my content provider,
		// they make you use call() and pass parameters in a bundle of sticks.
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("rows", (ArrayList<String>) rows);
		bundle.putParcelableArray("values", contentValuesArray);
		contentResolver.call(uri,"bulkUpdateAndIncTotal", null, bundle);
	}

	private Cursor getCursor(){
		// Prepare query strings
		Uri uri = Uri.parse("content://org.noip.nordberg.shoplister/items");
		String[] projection = {DBHelper.KEY_ROWID,DBHelper.KEY_QUANTITY,
				DBHelper.KEY_LIST,DBHelper.KEY_CHECKED,DBHelper.KEY_SORT_RANK};
		String selection = DBHelper.KEY_QUANTITY + ">0 AND " + DBHelper.KEY_LIST + "=?";
		String[] selectionArgs ={Statics.currentListName};
		String sortOrder =  DBHelper.KEY_CHECKED + "," + DBHelper.KEY_SORT_RANK;
		// Run the query
		return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
	}
}
