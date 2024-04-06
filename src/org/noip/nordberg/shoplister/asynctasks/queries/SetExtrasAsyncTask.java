package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;

public class SetExtrasAsyncTask extends QueryHelperBaseAsyncTask {

	private String extras;
	
	/**
	 * @param rowId
	 * @param extras
	 */
	public SetExtrasAsyncTask(int rowId, String extras) {
		super(rowId);
		this.extras = extras;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		newValues.put("extras", extras);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
