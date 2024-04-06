package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.SetFavAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;

import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;

public class FavOffButtonOnClick implements OnClickListener {

	private CursorProvider cursorProvider;

	public FavOffButtonOnClick(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	};

	@Override
	public void onClick(View v) {
		Cursor cursor = cursorProvider.getCursor();
		
		// Setup cursor position
		int cursorPositionOfView = (Integer) v.getTag();
		cursor.moveToPosition(cursorPositionOfView);
		int rowId = cursor.getInt(ROW_ID);
		
		new SetFavAsyncTask(rowId, false).execute();
	}
}
