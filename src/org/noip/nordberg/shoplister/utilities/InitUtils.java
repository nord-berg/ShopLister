package org.noip.nordberg.shoplister.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

public class InitUtils {
	
	public static void setupPreferences(Activity activity) {
		SharedPreferences sharedPrefs = activity.getSharedPreferences("ShoplisterSharedPreferences",Context.MODE_PRIVATE);
		Statics.prefEditor = sharedPrefs.edit();
	
		// Get GlobalFields.currentListName from SharedPrefs, set to "Default" on first run to allow initialization of spinner etc.
		Statics.currentListName = sharedPrefs.getString("current_list_name","Default");
	
		// Setup Settings Preferences
		SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
		Statics.prefShowQuantity = settingsPrefs.getBoolean("pref_show_quantity", true);
		Statics.prefDragHandleSide = settingsPrefs.getString("pref_drag_handle_side", "left");
		Statics.sortByTotal = settingsPrefs.getBoolean("pref_sort_by_total", false);
	}

	public static void setupTypeFaces() {
		 Statics.robotoSerifTypeFace = Typeface.createFromAsset(AppRef.context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
		 Statics.robotoSerifBoldTypeFace = Typeface.createFromAsset(AppRef.context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
	 }
}
