package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;
import org.noip.nordberg.shoplister.dragndrop.DragNDropUtils;

public class SetSortRankAsyncTask extends QueryHelperBaseAsyncTask {

	private int sortRank;
	
	/**
	 * @param rowId
	 * @param sortRank
	 */
	public SetSortRankAsyncTask(int rowId, int sortRank) {
		super(rowId);
		this.sortRank = sortRank;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		newValues.put("sort_rank", sortRank);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		DragNDropUtils.cleanListMapping();
		super.onPostExecute(result);
	}

}
