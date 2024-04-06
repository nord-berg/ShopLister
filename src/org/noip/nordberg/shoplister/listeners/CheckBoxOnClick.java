package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.SetCheckedInDbAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.MiscUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

public class CheckBoxOnClick implements OnClickListener {

	private CursorProvider cursorProvider;

	public CheckBoxOnClick(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	};

	@Override
	public void onClick(View v) {
		int cursorPositionOfView = (Integer) v.getTag();
		cursorProvider.getCursor().moveToPosition(cursorPositionOfView);
		int rowId = cursorProvider.getCursor().getInt(ROW_ID);

		if (((CompoundButton) v).isChecked()) new SetCheckedInDbAsyncTask(rowId, true).execute();
		else new SetCheckedInDbAsyncTask(rowId, false).execute();
		// Hide keyboard just in case it's still open from footer EditText
		MiscUtils.hideKeyboard(v);
	}
}