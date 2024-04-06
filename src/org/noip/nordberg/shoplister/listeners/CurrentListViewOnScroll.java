package org.noip.nordberg.shoplister.listeners;

import org.noip.nordberg.shoplister.cleanup.CleanupUtils;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class CurrentListViewOnScroll implements OnScrollListener {

	private CursorProvider cursorProvider;

	public CurrentListViewOnScroll(CursorProvider cursorProvider) {
		this.cursorProvider = cursorProvider;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if(Statics.cleaningRequested && firstVisibleItem == 0){
			CleanupUtils.cleanup(cursorProvider);
			// Put code here to pause the drag-sort-list for a second or so
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

}
