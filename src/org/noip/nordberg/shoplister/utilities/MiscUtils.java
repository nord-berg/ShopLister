package org.noip.nordberg.shoplister.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class MiscUtils {

	/**
	 * Resolution converter.  For when you want to specify a measurement in dp
	 * @param inputDp
	 * @return
	 */
	public static int dpToPx(int inputDp){
		int outputPx= (int) (AppRef.activity.getResources().getDisplayMetrics().density * inputDp);
		return outputPx;
	}

	public static void showKeyboard(final View input, Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		//    	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
	}

	public static void hideKeyboard(final View input) {
		InputMethodManager imm = (InputMethodManager) AppRef.context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(),0);
	}

	public static void showKeyboardInAlertDialog(final AlertDialog ad){
		ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// Automatically hides itself when the AlertDialog is dismissed
	}

	public static void updateBackgroundResourceWithRetainedPadding(View view, int resourceID) {
		int bottom = view.getPaddingBottom();
		int top = view.getPaddingTop();
		int right = view.getPaddingRight();
		int left = view.getPaddingLeft();
		view.setBackgroundResource(resourceID);
		view.setPadding(left, top, right, bottom);
	}

	/**
	  * Restarts the LoaderManagers on my list fragments.  This is needed because
	  * the selection args in my loaders are dynamic (based on list selected in spinner),
	  * and loaders don't pick up on this change and re-query automatically,
	  * like they do with a change in the data itself. 
	  * @param fragment
	  */
	 @SuppressWarnings("unchecked")  // defs going to be a cursor
	 public static void restartLoaderManager(Fragment fragment){
		 fragment.getLoaderManager().restartLoader(0, null, (LoaderCallbacks<Cursor>) fragment);
	 }
}
