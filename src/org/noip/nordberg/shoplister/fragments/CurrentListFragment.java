package org.noip.nordberg.shoplister.fragments;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.adapters.CurrentListAdapter;
import org.noip.nordberg.shoplister.asynctasks.queries.AddNewItemAsyncTask;
import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.dragndrop.DragNDropUtils;
import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class CurrentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static CurrentListAdapter adapter;
	public static EditText addItemEditText;
	public static EditText addItemEditTextFooter;
	public static TextView footerFiller;
	public static Button cleanupButton;
	public static CurrentListFragment currentListFragmentRef;
	

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			
			// Don't allow rearranging of checked items
			if (Statics.checkedRows.contains(from) || Statics.checkedRows.contains(to)){
				return;
			}
			
			// Populate dragNDropMap
			if (from != to) {
	            int cursorFrom = Statics.dragNDropMap.get(from, from);

	            if (from > to) {
	                for (int i = from; i > to; --i) {
	                    Statics.dragNDropMap.put(i, Statics.dragNDropMap.get(i - 1, i - 1));
	                }
	            } else {
	                for (int i = from; i < to; ++i) {
	                    Statics.dragNDropMap.put(i, Statics.dragNDropMap.get(i + 1, i + 1));
	                }
	            }
	            Statics.dragNDropMap.put(to, cursorFrom);
	        }
			
			// Force the views to refresh, using the dragNDropMap.  This ensures
			// the list shows it's post-drop order while the db is updating on a seperate thread
			Statics.currentListView.invalidateViews();
			
			// Now do the DB stuff (dragNDropMap is cleared after db update)
			DragNDropUtils.actionDrop(from, to, adapter);
		}
		
//		
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Make a static reference to this instance
		currentListFragmentRef = this;

		View viewHolder = inflater.inflate(R.layout.currentlistfragment_layout, container, false);

		Statics.currentListView = (DragSortListView) viewHolder.findViewById(R.id.currentListView);

		// Scroll to bottom of list when new item added
		Statics.currentListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL); 

		addFooter();

		// Setup the list adapter for currentListView
		adapter = new CurrentListAdapter();
		Statics.currentListView.setAdapter(adapter);

		// Setup listeners
		Statics.currentListView.setDropListener(onDrop);

		// Setup DragSortController
		DragSortController controller = new DragSortController(Statics.currentListView);
		if (Statics.prefDragHandleSide.equalsIgnoreCase("right")) controller.setDragHandleId(R.id.drag_handle_right);
		else controller.setDragHandleId(R.id.drag_handle_left);
		controller.setRemoveEnabled(false);
		controller.setSortEnabled(true);
		controller.setDragInitMode(1);
		controller.setBackgroundColor(Color.TRANSPARENT);
		Statics.currentListView.setFloatViewManager(controller);
		Statics.currentListView.setOnTouchListener(controller);
		Statics.currentListView.setDragEnabled(true);

		// Setup mListMapping
		Statics.dragNDropMap = new SparseIntArray();
		DragNDropUtils.cleanListMapping();
		
		// Setup the loader
		getLoaderManager().initLoader(0,null,this);

		return viewHolder;
	}	

	private void addFooter() {
		View footerView = ((LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.list_row_currentlistfooter_layout, null, false);

		Statics.currentListView.addFooterView(footerView);

		addItemEditTextFooter = (EditText) footerView.findViewById(R.id.addItemEditTextFooter);
		addItemEditTextFooter.setTypeface(Statics.robotoSerifTypeFace);
		footerFiller = (TextView) footerView.findViewById(R.id.footerFiller);

		// Set left margin depending on drag handle side
		if (Statics.prefDragHandleSide.equalsIgnoreCase("right")) footerFiller.setVisibility(View.GONE);
		else footerFiller.setVisibility(View.VISIBLE);

		//		EditorActionListener to enter data into DB
		addItemEditTextFooter.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, final KeyEvent event) {
				// Work around for the gayness of hardware keyboard making two events and 
				// this whole code running twice.  This code catches the 2nd event and kills it. 
				if(event != null && event.getAction() != KeyEvent.ACTION_DOWN) return true;
				String enteredText = new String(addItemEditTextFooter.getText().toString()).trim();
				if(enteredText.isEmpty()) {
					v.clearFocus();
					MiscUtils.hideKeyboard(v);  // need to force kb close here
					return false;
				}
				new AddNewItemAsyncTask(enteredText, Statics.currentListName, 1).execute();
				addItemEditTextFooter.setText("");
				return true;  // false will hide the keyboard, true leaves it open
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		// Prepare query strings
		Uri uri = Uri.parse("content://org.noip.nordberg.shoplister/items");
		String[] projection = {DBHelper.KEY_ROWID,DBHelper.KEY_ITEM,DBHelper.KEY_QUANTITY,
				DBHelper.KEY_LIST,DBHelper.KEY_FAVOURITE,DBHelper.KEY_CHECKED,
				DBHelper.KEY_TOTAL,DBHelper.KEY_EXTRAS,DBHelper.KEY_SORT_RANK};
		String selection = DBHelper.KEY_QUANTITY + ">0 AND " + DBHelper.KEY_LIST + "=?";
		String[] selectionArgs ={Statics.currentListName};
		String sortOrder =  DBHelper.KEY_CHECKED + "," + DBHelper.KEY_SORT_RANK;
		// Run the query
		return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		adapter.swapCursor(cursor);
		Statics.currentListView.invalidateViews();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		adapter.swapCursor(null);
	}
}
