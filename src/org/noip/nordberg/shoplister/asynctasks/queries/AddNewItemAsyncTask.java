package org.noip.nordberg.shoplister.asynctasks.queries;

import org.noip.nordberg.shoplister.asynctasks.QueryHelperBaseAsyncTask;
import org.noip.nordberg.shoplister.database.ContProvider;
import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.dragndrop.DragNDropUtils;
import org.noip.nordberg.shoplister.utilities.AppRef;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class AddNewItemAsyncTask extends QueryHelperBaseAsyncTask {

	private String item;
	private String list;
	private int quantity;
	
	/**
	 * @param item
	 * @param list
	 * @param quantity
	 */
	public AddNewItemAsyncTask(String item, String list, int quantity) {
		super(0); // rowId not needed
		this.item = item;
		this.list = list;
		this.quantity = quantity;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		int rowOfConflict = detectConflict(item, list);
		if(rowOfConflict == -1) {  // No conflict exists. Create a new item
			newValues.put("item", item);
			newValues.put("quantity", quantity);
			newValues.put("list", list);
			newValues.put("favourite", 0);
			newValues.put("checked", 0);
			newValues.put("total", 0);
			newValues.put("extras", "");
			newValues.put("sort_rank", 0); // this will be changed in a sec
			Uri newItemUri = contentResolver.insert(ContProvider.CONTENT_URI, newValues);

			// Set sort_rank to rowId (find rowId of our newly created item from newItemUri)
			String newItemUriString = newItemUri.toString();
			int slashIndex = newItemUriString.indexOf("/");
			String rowIdString = newItemUriString.substring(slashIndex + 1);
			int rowId = Integer.parseInt(rowIdString);
			setSortRank(rowId, rowId);
			return null;
		}
		else {
			// Conflict exists.  Modify existing entry so it looks like a new entry
			newValues.put("quantity", 1);
			uri = Uri.parse(ContProvider.CONTENT_URI + "/" + rowOfConflict);
		}
		
		contentResolver.update(uri, newValues, null, null);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		DragNDropUtils.cleanListMapping();
		super.onPostExecute(result);
	}
	
	private int detectConflict(String itemName, String listName){
		Uri uri3 = Uri.parse(ContProvider.CONTENT_URI + "");
		String[] projection = {DBHelper.KEY_ROWID,DBHelper.KEY_ITEM,DBHelper.KEY_LIST};
		String selection = DBHelper.KEY_ITEM + "=? AND " + DBHelper.KEY_LIST + "=?";
		String[] selectionArgs = {itemName, listName};
		ContentResolver contentResolver3 = AppRef.context.getContentResolver();
		Cursor c = contentResolver3.query(uri3, projection, selection, selectionArgs, null);
		if (c.getCount() == 0) return -1;  // no conflicts exist
		c.moveToFirst();
		return c.getInt(0);  // return the conflict row
	}

	private void setSortRank(int rowId, int sortRank){
		newValues.clear();
		newValues.put("sort_rank", sortRank);
		Uri uri2 = Uri.parse(ContProvider.CONTENT_URI + "/" + rowId);
		ContentResolver contentResolver2 = AppRef.context.getContentResolver();
		contentResolver2.update(uri2, newValues, null, null);
	}
}
