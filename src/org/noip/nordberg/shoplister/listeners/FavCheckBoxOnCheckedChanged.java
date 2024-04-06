package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.SetFavAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.MiscUtils;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FavCheckBoxOnCheckedChanged implements OnCheckedChangeListener{

	private CursorProvider cursorProvider;

	public FavCheckBoxOnCheckedChanged(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if(v.isPressed()){ // Check if change was clicked by a humanoid
			// Get rowId
			int cursorPositionOfView = (Integer) v.getTag();
			cursorProvider.getCursor().moveToPosition(cursorPositionOfView);
			int rowId = cursorProvider.getCursor().getInt(ROW_ID);
			// Change fav state in DB
			if(isChecked) new SetFavAsyncTask(rowId, true).execute();
			else new SetFavAsyncTask(rowId, false).execute();
			// Hide keyboard just in case it's still open from footer EditText
			MiscUtils.hideKeyboard(v);
		}
	}
}
