package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.asynctasks.queries.SetQuantityAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;

import android.view.View;
import android.view.View.OnLongClickListener;

public class IncQuantityButtonOnLongClick implements OnLongClickListener{

private CursorProvider cursorProvider;
	
	public IncQuantityButtonOnLongClick(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;
	};
	@Override
	public boolean onLongClick(View v) {
			int cursorPositionOfView = (Integer) v.getTag();
			cursorProvider.getCursor().moveToPosition(cursorPositionOfView);
			int rowId = cursorProvider.getCursor().getInt(ROW_ID);
			new SetQuantityAsyncTask(rowId, 1).execute();
		return false;
	}
}
