package org.noip.nordberg.shoplister.listeners;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.utilities.Animations;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class ExtrasFillerOnTouch implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP){
			View root = (View) v.getParent().getParent();
			View parent = root.findViewById(R.id.rightLL);
			EditText et = (EditText) parent.findViewById(R.id.extrasText);
			if(Statics.extrasExposed) {
				// If user has opened an extrasEditText and not saved it...byebye
				Statics.currentListView.invalidateViews();
				Statics.extrasExposed = false;
				Statics.extrasExposedRow = -1;
				return false;
			}
			if(et.getVisibility() == View.VISIBLE) return false;
			et.setVisibility(View.INVISIBLE);
			et.startAnimation(Animations.SCALE_ANIM);
			et.setVisibility(View.VISIBLE);
			et.requestFocus();
			Statics.extrasExposedRow = (Integer) et.getTag();
			Statics.extrasExposed = true;
			MiscUtils.showKeyboard(et, AppRef.context);
		}
		return false;
	}
}
