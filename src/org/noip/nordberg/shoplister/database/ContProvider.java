package org.noip.nordberg.shoplister.database;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class ContProvider extends ContentProvider {
	
	private DBHelper database;
		
	// Strings to help build the URI
	private static final String AUTHORITY = "org.noip.nordberg.shoplister";
	private static final String PATH = "items";  // the table in my db. this needs a rethink for multiple tables
	private static final String PATH_SINGLE_ROW = "items/#"; // # is a wildcard allowing numbers only
	private static final String PATH_SINGLE_ROW_INCREMENT_QUANTITY = "items/increment_quantity/#"; // special case for increasing quantity by 1
	private static final String PATH_SINGLE_ROW_INCREMENT_TOTAL = "items/increment_total/#"; // special case for increasing quantity by 1
	
	// Build the URI
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY 
			+ "/" + PATH);
	
	// Create UriMatcher object for the purpose of assigning integers to
	// URI patterns, so we can handle them easily in a switch
	private static final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	// Static initialization block to initialize myUriMatcher
	static {
	// Sets the integer value of "10" for multiple rows
	myUriMatcher.addURI(AUTHORITY, PATH, 10);
	
	// Sets the integer value of "20" for single rows.  
	myUriMatcher.addURI(AUTHORITY, PATH_SINGLE_ROW, 20);
	
	// Sets the integer value of "30" for single rows increment quantity by one.  
	myUriMatcher.addURI(AUTHORITY, PATH_SINGLE_ROW_INCREMENT_QUANTITY, 30);
	
	// Sets the integer value of "40" for single rows increment total by one.  
	myUriMatcher.addURI(AUTHORITY, PATH_SINGLE_ROW_INCREMENT_TOTAL, 40);
	}
		
	@Override
	public boolean onCreate() {
		database = new DBHelper(getContext());
		return true;
	}
	
	// Needed for calling custom methods in this content provider
	@Override
	public Bundle call(String method, String arg, Bundle extras) {

		if (method.equalsIgnoreCase("bulkUpdate")){
			bulkUpdate(extras.getStringArrayList("rows"), (ContentValues[]) extras.getParcelableArray("values"));
		}
		if (method.equalsIgnoreCase("bulkUpdateAndIncTotal")){
			bulkUpdateAndIncTotal(extras.getStringArrayList("rows"), (ContentValues[]) extras.getParcelableArray("values"));
		}
		return super.call(method, arg, extras);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = database.getReadableDatabase();
		// Take the URI pattern received and assign it an integer with help from the URIMatcher class
		int uriPattern = myUriMatcher.match(uri);
		switch(uriPattern) {
		case 10: // Multiple rows
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(PATH);
			Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			// Make sure listeners are notified
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		default: return null;
		}
	}
	
	@Override
	public String getType(Uri uri) {
		final int uriPattern = myUriMatcher.match(uri);
		switch(uriPattern) {
		case 10:  // Multiple rows
			return ContentResolver.CURSOR_DIR_BASE_TYPE;
		case 20:  // Single rows
			return ContentResolver.CURSOR_ITEM_BASE_TYPE;
		}
		return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriPattern = myUriMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriPattern) {
		case 10:  // Multiple rows
			rowsDeleted = db.delete(PATH, selection, selectionArgs);
			break;
		}
		return rowsDeleted;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getReadableDatabase();
		int token = myUriMatcher.match(uri);
		long id = 0;
		switch(token) {
		case 10: // Multiple Rows
			id = db.insert(PATH, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri,  null);
		return Uri.parse(PATH + "/" + id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int token = myUriMatcher.match(uri);
		SQLiteDatabase db = database.getReadableDatabase();
		int numberOfRowsUpdated = 0;
		switch(token) {
		case 10: // Multiple Rows
			numberOfRowsUpdated = db.update(PATH, values, selection, selectionArgs);
			break;
		case 20: // Single Row
			String id1 = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				numberOfRowsUpdated = db.update(PATH, values, DBHelper.KEY_ROWID + "=" + id1, null);
			} else {
				numberOfRowsUpdated = db.update(PATH, values, DBHelper.KEY_ROWID + "=" + id1
						+ " and " + selection, selectionArgs);
			}
			break;
		case 30: // Special update case for incrementing quantity by one
			String id2 = uri.getLastPathSegment();
			int id2Int = Integer.parseInt(id2);
			db.execSQL("UPDATE " + PATH + " SET " + DBHelper.KEY_QUANTITY + " = " + DBHelper.KEY_QUANTITY + "+ 1"
					+ " WHERE " + DBHelper.KEY_ROWID + " = " + id2Int);
			numberOfRowsUpdated = 1;
			break;
		case 40: // Special update case for incrementing total by one
			String id3 = uri.getLastPathSegment();
			int id3Int = Integer.parseInt(id3);
			db.execSQL("UPDATE " + PATH + " SET " + DBHelper.KEY_TOTAL + " = " + DBHelper.KEY_TOTAL + "+ 1"
					+ " WHERE " + DBHelper.KEY_ROWID + " = " + id3Int);
			numberOfRowsUpdated = 1;
			break;	
		default:
			throw new IllegalArgumentException("Update failed");
		}
		getContext().getContentResolver().notifyChange(uri, null);	
		return numberOfRowsUpdated;
	}

	public void bulkUpdate(ArrayList<String> rowIdList, ContentValues[] valuesArray) {
		SQLiteDatabase db = database.getReadableDatabase();
		db.beginTransaction();
		try {
			// Iterate through rowIdList, updating db with valuesArray
			for (int i = 0; i < rowIdList.size(); i++) {
				db.update(PATH, valuesArray[i], DBHelper.KEY_ROWID + "="  + rowIdList.get(i), null);
			}	
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);	
	}
	
	public void bulkUpdateAndIncTotal(ArrayList<String> rowIdList, ContentValues[] valuesArray) {
		SQLiteDatabase db = database.getReadableDatabase();
		db.beginTransaction();
		try {
			// Iterate through rowIdList, updating db with valuesArray, and inc total by one
			int rowIdListSize = rowIdList.size();
			for (int i = 0; i < rowIdListSize; i++) {
				db.update(PATH, valuesArray[i], DBHelper.KEY_ROWID + "="  + rowIdList.get(i), null);
				db.execSQL("UPDATE " + PATH + " SET " + DBHelper.KEY_TOTAL + " = " + DBHelper.KEY_TOTAL + "+ 1"
						+ " WHERE " + DBHelper.KEY_ROWID + " = " + rowIdList.get(i));
			}	
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);	
	}
}
