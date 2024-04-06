package org.noip.nordberg.shoplister.listeners;

import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.QUANTITY;
import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.ROW_ID;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.asynctasks.queries.SetCheckedInDbAsyncTask;
import org.noip.nordberg.shoplister.asynctasks.queries.SetQuantityAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;

import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;

public class FavRowItemTextOnClick implements OnClickListener {

	private CursorProvider cursorProvider;

	public FavRowItemTextOnClick(CursorProvider cursorProvider){
		this.cursorProvider = cursorProvider;

	};

	@Override
	public void onClick(View v) {
		Cursor cursor = cursorProvider.getCursor();
		// Setup cursor position
		int cursorPositionOfView = (Integer) v.getTag();
		cursor.moveToPosition(cursorPositionOfView);
		int rowId = cursor.getInt(ROW_ID);

		// Change the background immediately to make it responsive
		View vParent = (View) v.getParent();
		if (vParent.getBackground() == null) vParent.setBackgroundResource(R.color.fav_row_background_selected);
		else vParent.setBackgroundResource(0);

		Integer quantityValue = cursor.getInt(QUANTITY);  // find the item's quantity
		// Add to  current list if not already there
		if(quantityValue.equals(0)) new SetQuantityAsyncTask(rowId, 1).execute();
		else { // Or if already there, remove from current list and uncheck (do not increase total)
			new SetQuantityAsyncTask(rowId, 0).execute();
			new SetCheckedInDbAsyncTask(rowId, false).execute();
		}
	}
}
