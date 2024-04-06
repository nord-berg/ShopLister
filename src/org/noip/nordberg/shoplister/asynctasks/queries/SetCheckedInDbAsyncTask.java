package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;

public class SetCheckedInDbAsyncTask extends QueryHelperBaseAsyncTask {

	private Boolean checked;
	
	/**
	 * @param rowId
	 * @param checked
	 */
	public SetCheckedInDbAsyncTask(int rowId, Boolean checked) {
		super(rowId);
		this.checked = checked;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		if(checked == true) newValues.put("checked", 1);
		else newValues.put("checked", 0);
		contentResolver.update(uri, newValues, null, null);
		return null;
	}

}
