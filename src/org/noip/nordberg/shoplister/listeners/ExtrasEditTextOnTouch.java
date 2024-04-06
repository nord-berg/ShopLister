package org.noip.nordberg.shoplister.listeners;

import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ExtrasEditTextOnTouch implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Only used for the extrasExposed functionality - i.e. to kill abandoned extras views
		if(MotionEvent.ACTION_UP == event.getAction() && Statics.extrasExposed) {
			if(Statics.extrasExposedRow == (Integer) v.getTag()){ // User clicked the same view again
				v.requestFocus();
				return false;
			}
			Log.i("nord","extrasExposedRow= " + Statics.extrasExposedRow + " and v.tag= " + v.getTag().toString());
			// If user has opened an extrasEditText and not written anything, kill it
			Statics.currentListView.invalidateViews();
			MiscUtils.hideKeyboard(v);
			Statics.extrasExposed = false;
			Statics.extrasExposedRow = -1;
			return true; // hides the keyboard
		}
		return false;
	}
}
