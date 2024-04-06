package org.noip.nordberg.shoplister.adapters;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.CHECKED;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.EXTRAS;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.FAVOURITE;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ITEM;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.SORT_RANK;

import java.util.ArrayList;
import java.util.HashSet;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.cleanup.CleanupUtils;
import org.noip.nordberg.shoplister.customviews.ListRowView;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.listeners.CurrentListViewOnScroll;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.Context;
import android.database.Cursor;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;

public class CurrentListAdapter extends BasicAbstractListAdapter implements CursorProvider {

	private Cursor cursor;
	private LayoutInflater layoutInflater;

	public CurrentListAdapter(){
		Statics.cleaningRequested = false;
		Statics.rowsToHideAfterAnim = new ArrayList<Integer>();
		Statics.checkedRows = new HashSet<Integer>();
	}

	@Override
	public int getCount() {
		if (cursor == null) return 0;
		return cursor.getCount();
	}

	@Override
	public View getView(int preDropPosition, View oldView, ViewGroup parent) { // "View" means entire row as one view
		ListRowView listRowView;
	
		// Trick this adapter to getting a phony position based on drag n drop
		int position;
		position = Statics.dragNDropMap.get(preDropPosition, preDropPosition);
		
		// Recycle oldView if possible
		if (oldView instanceof ListRowView) {  
			listRowView = (ListRowView) oldView; 
		} else { 
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listRowView = (ListRowView) layoutInflater.inflate(R.layout.list_row_currentlist, null);
		}

		// Is there a better place i can do this?
		Statics.currentListView = (DragSortListView) parent.getRootView().findViewById(R.id.currentListView);		
		
		listRowView.setup(this, position);
		
		// Setup listeners
		Statics.currentListView.setOnScrollListener(new CurrentListViewOnScroll(this));

		return listRowView;
	}

	public void swapCursor(Cursor cursor) {
		this.cursor = cursor;

		// Build array of rowIds when fresh data arrives, for use in drag n drop
		if (getCursor() != null) {
			int cursorCount = getCursor().getCount();
			Statics.preDropSortRanksFull = new ArrayList<Integer>();
			for(int i = 0; i < cursorCount; i++){
				getCursor().moveToPosition(i);
				Statics.preDropSortRanksFull.add(getCursor().getInt(SORT_RANK));
			}
		}
	}

	/**
	 * Items are removed from current list by setting quantity = 0
	 */
	public void initCleanList(){
		Vibrator v = (Vibrator) AppRef.context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(50);
		
		// Do nothing if list is empty
		if(Statics.currentListView.getCount() == 0) return; 
		
		// Clean if list is already scrolled to top
		if(Statics.currentListView.getFirstVisiblePosition() == 0) {
			Statics.cleaningRequested = true;
			CleanupUtils.cleanup(this);
			return;
		}
		// Otherwise, move to top.  A scroll listener will trigger the clean when the list 
		// reaches the top and cleaningRequested = true
		Statics.currentListView.smoothScrollToPosition(0);
		Statics.cleaningRequested = true;
	}

	protected boolean getFav(int cursorPositionOfView){
		boolean isFav = false;
		cursor.moveToPosition(cursorPositionOfView);
		Integer favStatus = cursor.getInt(FAVOURITE);
		if (favStatus.equals(1)) isFav = true; 
		return isFav;
	}

	protected boolean getCheckedInDb(int cursorPositionOfView){
		boolean isChecked = false;
		cursor.moveToPosition(cursorPositionOfView);
		Integer checkedStatus = cursor.getInt(CHECKED);
		if (checkedStatus.equals(1)) isChecked = true; 
		return isChecked;
	}

	protected String getExtras(int cursorPositionOfView){
		cursor.moveToPosition(cursorPositionOfView);
		String extras = cursor.getString(EXTRAS);
		return extras;
	}

	protected String getItemName(int cursorPositionOfView){
		cursor.moveToPosition(cursorPositionOfView);
		String item = cursor.getString(ITEM);
		return item;
	}

	@Override
	public Cursor getCursor() {
		return cursor;
	}
}
