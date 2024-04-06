package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;

public class SetFavAsyncTask extends QueryHelperBaseAsyncTask {

	private Boolean isFav;
	
	/**
	 * @param rowId
	 * @param isFav
	 */
	public SetFavAsyncTask(int rowId, Boolean isFav) {
		super(rowId);
		this.isFav = isFav;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		if(isFav) newValues.put("favourite", 1);
		else newValues.put("favourite", 0);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
