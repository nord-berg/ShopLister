package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;
import org.noip.nordberg.shoplister.database.ContProvider;

import android.net.Uri;

public class IncQuantityByOneAsyncTask extends QueryHelperBaseAsyncTask {

	private int rowId;
	
	/**
	 * @param rowId
	 */
	public IncQuantityByOneAsyncTask(int rowId) {
		super(rowId);
		this.rowId = rowId;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		uri = Uri.parse(ContProvider.CONTENT_URI + "/increment_quantity/" + rowId);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
