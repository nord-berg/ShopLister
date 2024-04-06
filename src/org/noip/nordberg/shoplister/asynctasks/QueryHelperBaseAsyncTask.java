package org.noip.nordberg.shoplister.asynctasks;

import org.noip.nordberg.shoplister.database.ContProvider;
import org.noip.nordberg.shoplister.utilities.AppRef;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;

public abstract class QueryHelperBaseAsyncTask extends AsyncTask<Object, Void, Void>{

	protected ContentValues newValues;
	protected ContentResolver contentResolver;
	protected Uri uri;
	public static Boolean newItemWasJustAdded = false;

	public QueryHelperBaseAsyncTask(int rowId) {
		super();
		newValues = new ContentValues();
		uri = Uri.parse(ContProvider.CONTENT_URI + "/" + rowId);
		contentResolver = AppRef.context.getContentResolver();
	}
}

