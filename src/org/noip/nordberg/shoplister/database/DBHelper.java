package org.noip.nordberg.shoplister.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	public static final String KEY_ROWID = "_id";
	public static final String KEY_ITEM = "item";
	public static final String KEY_QUANTITY = "quantity";
	public static final String KEY_LIST = "list";
	public static final String KEY_FAVOURITE = "favourite";
	public static final String KEY_CHECKED = "checked";
	public static final String KEY_TOTAL = "total";
	public static final String KEY_EXTRAS = "extras";
	public static final String KEY_SORT_RANK = "sort_rank";
	public static final String DB_TABLE = "items";
	private static final String DB_NAME = "db_shoplister";
	private static final int DB_VER = 1;

	// More constants: SQL statement to create the table
	// This was not working when i tried using the constants.. try again some time
	private static final String CREATE_TABLE = 
			"CREATE TABLE " + DB_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_ITEM + " TEXT NOT NULL, " +
					KEY_QUANTITY + " INTEGER NOT NULL, " +
					KEY_LIST + " TEXT NOT NULL, " +
					KEY_FAVOURITE + " INTEGER NOT NULL, " +
					KEY_CHECKED + " INTEGER NOT NULL, " +
					KEY_TOTAL + " INTEGER NOT NULL, " +
					KEY_EXTRAS + " TEXT NOT NULL, " +
					KEY_SORT_RANK + " INTEGER NOT NULL);";

	DBHelper(Context c) {
		// Instantiate a SQLiteOpenHelper object
		super(c, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ DB_TABLE);
		onCreate(db);
	}
} 

