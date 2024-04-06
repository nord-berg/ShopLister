package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.IncQuantityByOneAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.MiscUtils;

import android.view.View;
import android.view.View.OnClickListener;

public class IncQuantityButtonOnClick implements OnClickListener {

	private CursorProvider cursorProvider;

	public IncQuantityButtonOnClick(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	};

	@Override
	public void onClick(View v) {
		int cursorPositionOfView = (Integer) v.getTag();
		cursorProvider.getCursor().moveToPosition(cursorPositionOfView);
		int rowId = cursorProvider.getCursor().getInt(ROW_ID);
		new IncQuantityByOneAsyncTask(rowId).execute();
		// Hide keyboard just in case it's still open from footer EditText
		MiscUtils.hideKeyboard(v);
	}
}