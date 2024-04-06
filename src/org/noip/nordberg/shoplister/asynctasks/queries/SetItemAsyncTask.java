package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;

public class SetItemAsyncTask extends QueryHelperBaseAsyncTask {

	private String item;
	
	/**
	 * @param rowId
	 * @param item
	 */
	public SetItemAsyncTask(int rowId, String item) {
		super(rowId);
		this.item = item;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		newValues.put("item", item);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
