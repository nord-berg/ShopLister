package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;

public class SetQuantityAsyncTask extends QueryHelperBaseAsyncTask {

	private int quantity;
	
	/**
	 * @param rowId
	 * @param quantity
	 */
	public SetQuantityAsyncTask(int rowId, int quantity) {
		super(rowId);
		this.quantity = quantity;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		newValues.put("quantity", quantity);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
