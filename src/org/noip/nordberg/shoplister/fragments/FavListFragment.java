package org.noip.nordberg.shoplister.fragments;


import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.adapters.FavListAdapter;
import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FavListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	public static ListView favListView;
	public static FavListAdapter adapter;
	public static FavListFragment favListFragmentRef;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Make a static reference to this instance
        favListFragmentRef = this;
		
		View viewHolder = inflater.inflate(R.layout.favlistfragment_layout, container, false);
		
		favListView = (ListView) viewHolder.findViewById(R.id.favListView);
		
		// Setup the list adapter for favListView
		adapter = new FavListAdapter();
		favListView.setFastScrollEnabled(!Statics.sortByTotal);
		favListView.setAdapter(adapter);

		// Setup the loader
		getLoaderManager().initLoader(0,null,this);
		
		return viewHolder;
	}	

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		// Prepare query strings
		Uri uri = Uri.parse("content://org.noip.nordberg.shoplister/items");
		String[] projection = {DBHelper.KEY_ROWID,DBHelper.KEY_ITEM,DBHelper.KEY_QUANTITY,
				DBHelper.KEY_LIST,DBHelper.KEY_FAVOURITE,DBHelper.KEY_CHECKED,
				DBHelper.KEY_TOTAL, DBHelper.KEY_EXTRAS};
		String selection = DBHelper.KEY_FAVOURITE + "=1 AND " + DBHelper.KEY_LIST + "=?";
		String[] selectionArgs = {Statics.currentListName};
		String sortOrder = DBHelper.KEY_ITEM + " COLLATE NOCASE";
		if (Statics.sortByTotal) sortOrder = DBHelper.KEY_TOTAL + " DESC"  + "," + DBHelper.KEY_ITEM;
		// Run the query
		return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
		favListView.invalidateViews();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		adapter.swapCursor(null);
	}
}
