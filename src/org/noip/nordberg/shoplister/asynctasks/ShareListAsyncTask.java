package org.noip.nordberg.shoplister.asynctasks;

import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;


public class ShareListAsyncTask extends AsyncTask<Object, Void, Void>{

	private static final int ITEM = 0;
	private static final int EXTRAS = 1;
	private static final int QUANTITY = 2;
	
	private Cursor cursor;
	private String shareString;

	@Override
	protected Void doInBackground(Object... objects) {
		shareString = getShareString(Statics.currentListName);
		return null;

	}
	@Override
	protected void onPostExecute(Void result) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
		sendIntent.setType("text/plain");
		AppRef.activity.startActivity(sendIntent);
		super.onPostExecute(result);
	}

	private String getShareString(String list){
		String outputString = Statics.currentListName + ":" + "\n";
		// Prepare query strings
		String[] projection = {DBHelper.KEY_ITEM,DBHelper.KEY_EXTRAS,DBHelper.KEY_QUANTITY};
		String selection = DBHelper.KEY_LIST + "=? AND " + DBHelper.KEY_QUANTITY + ">0";
		String[] selectionArgs = new String[]{Statics.currentListName};
		// Run the query
		cursor = AppRef.context.getContentResolver().query(Uri.parse("content://org.noip.nordberg.shoplister/items"),
				projection, selection, selectionArgs, null);

		// Iterate through DB, adding items to string
		cursor.moveToFirst();
		// using while(cursor.moveToNext()) is a common way to do this, but it skips the first row. Ask Joe
		do { 
			String cursorString = cursor.getString(ITEM) + " - " + cursor.getString(EXTRAS) + " (" + cursor.getString(QUANTITY) + ")";
		outputString = outputString + "\n" + cursorString; 
		}
		while (cursor.moveToNext()); 
		cursor.close();
		return outputString;
	}
}


