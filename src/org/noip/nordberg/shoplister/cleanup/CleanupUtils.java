package org.noip.nordberg.shoplister.cleanup;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.CHECKED;

import org.noip.nordberg.shoplister.asynctasks.CleanupListAsyncTask;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.Animations;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.database.Cursor;
import android.view.View;

public class CleanupUtils {

	public static void cleanup(CursorProvider cursorProvider){

		Cursor cursor = cursorProvider.getCursor();
		boolean noAnimsRan = false;

		// Find checked items in list
		cursor.moveToFirst();	
		while(cursor.getPosition() < cursor.getCount()){   
			int checkedStatus = Integer.parseInt(cursor.getString(CHECKED));
			if (checkedStatus == 1) {
				// Run an animation, moving view off screen:
				// 1. Find wantedChild row position in on-screen visible list.
				int wantedRow = cursor.getPosition();
				int firstVisiblePosition = Statics.currentListView.getFirstVisiblePosition() - Statics.currentListView.getHeaderViewsCount();
				int wantedChild = wantedRow - firstVisiblePosition;
				// Add wantedChild to list of views to hide after anim until DB update finished
				Statics.rowsToHideAfterAnim.add(wantedRow);
				
				// 2. Run the animation.
				if(wantedChild >= 0 && wantedChild < Statics.currentListView.getChildCount()) { // only animate if view is on-screen
					final View viewToMove = Statics.currentListView.getChildAt(wantedChild);
					viewToMove.startAnimation(Animations.SLIDE_OUT_RIGHT_ANIM);  // DB operations happen onAnimationEnd
				}
				else noAnimsRan = true;
			}
			cursor.moveToNext();
		};
		
		// If no views were animated, onAnimationEnd won't run, so we need run the DB cleanup here
		if(noAnimsRan) new CleanupListAsyncTask().execute();

		Statics.cleaningRequested = false;
	}
}
