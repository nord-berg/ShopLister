package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.SetExtrasAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ExtrasEditTextOnEditorAction implements OnEditorActionListener {

	private CursorProvider cursorProvider;

	public ExtrasEditTextOnEditorAction(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// Work around for the gayness of hardware keyboard making two events 
		if(event != null && event.getAction() == KeyEvent.ACTION_UP) return true;

		// Take input and enter into DB
		int cursorPositionOfView = (Integer) v.getTag();
		cursorProvider.getCursor().moveToPosition(cursorPositionOfView);
		String enteredText = new String(v.getText().toString()).trim();

		new SetExtrasAsyncTask(cursorProvider.getCursor().getInt(ROW_ID),enteredText).execute();
		v.setMinEms(3);
		Statics.extrasExposed = false;
		Statics.extrasExposedRow = -1;
		MiscUtils.hideKeyboard(v);
		return false;
	}
}
