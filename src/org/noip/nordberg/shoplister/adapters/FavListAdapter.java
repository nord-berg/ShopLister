package org.noip.nordberg.shoplister.adapters;

import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.FAVOURITE;
import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.ITEM;
import static org.noip.nordberg.shoplister.constants.FavListCursorConstants.QUANTITY;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.listeners.FavOffButtonOnClick;
import org.noip.nordberg.shoplister.listeners.FavRowItemTextOnClick;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class FavListAdapter extends BasicAbstractListAdapter implements ListAdapter, SectionIndexer, CursorProvider {

	private Cursor cursor;
	private View view;
	private ImageView favOffButton;
	private TextView item;
	private LayoutInflater layoutInflater;
	private String[] sections;
	private Map<String, Integer> alphaIndex;
	private SparseIntArray sectionAsIntegerForEveryPosition;

	public FavListAdapter(){
	}

	@Override
	public int getCount() {
		if (cursor == null) return 0;
		return cursor.getCount();
	}

	@Override
	public View getView(int position, View oldView, ViewGroup parent) { // "View" refers to entire row
		
		/// Recycle oldView if possible
		if (oldView != null) {  
			view = oldView;
		} else { 
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.list_row_favlist, null);
		}

		getViewsFromLayout();

		setupViews(position);

		setupOnClickListeners();
		
		return view;
	}

	private void getViewsFromLayout() {
		favOffButton = (ImageView) view.findViewById(R.id.favOffButton);
		item = (TextView) view.findViewById(R.id.favRowItemText);
	}
	
	private void setupViews(int position) {
		item.setTypeface(Statics.robotoSerifTypeFace);

		cursor.moveToPosition(position);
		item.setText(cursor.getString(ITEM));

		setupRowBackgroundColor();

		int cursorPosition = cursor.getPosition();

		item.setTag(cursorPosition);
		favOffButton.setTag(cursorPosition);
	}

	private void setupOnClickListeners() {
		FavRowItemTextOnClick favRowItemTextOnClick = new FavRowItemTextOnClick(this);
		FavOffButtonOnClick favOffButtonOnClick = new FavOffButtonOnClick(this);
		
		favOffButton.setOnClickListener(favOffButtonOnClick);
		item.setOnClickListener(favRowItemTextOnClick);
	}
	
	public void swapCursor(Cursor cursor) {
		this.cursor = cursor;
		if(cursor != null) setupSectionIndexer();
	}

	private void setupRowBackgroundColor() {
		Integer quantityValue = cursor.getInt(QUANTITY);  // find the item's quantity
		if(quantityValue.equals(0)) view.setBackgroundResource(0);
		else view.setBackgroundResource(R.color.fav_row_background_selected);
	}

	public boolean getFav(int rowId){
		boolean isFav = false;
		Integer favStatus = cursor.getInt(FAVOURITE);
		if (favStatus.equals(1)) isFav = true; 
		return isFav;
	}

	/**  Sets up some arrays and maps for use in getPositionForSection, etc
	 * 
	 */
	private void setupSectionIndexer(){
		alphaIndex = new TreeMap<String, Integer>();
		Map<Integer, String> sectionStringForEveryPosition = new HashMap<Integer, String>();
		sectionAsIntegerForEveryPosition = new SparseIntArray();
		
		int cursorSize = cursor.getCount();
		cursor.moveToFirst();
		for (int i = 0; i < cursorSize; i++) {
			String item = cursor.getString(ITEM);
			String ch = item.substring(0, 1);
			ch = ch.toUpperCase(AppRef.context.getResources().getConfiguration().locale);
			alphaIndex.put(ch, i);
			sectionStringForEveryPosition.put(i, ch);
			cursor.moveToNext();
		}
		
		sections = new String[alphaIndex.size()];
		alphaIndex.keySet().toArray(sections);
		List<String> sectionsAsList = Arrays.asList(sections);
		int sectionAsStringForEveryPositionSize = sectionStringForEveryPosition.size();
		
		for (int i = 0; i < sectionAsStringForEveryPositionSize; i++) {
			// Set change value for every position from alphabet to position.  ie. "C" becomes "3"
			sectionAsIntegerForEveryPosition.put(i, sectionsAsList.indexOf(sectionStringForEveryPosition.get(i)));
		}
	}
	
	@Override
	public boolean isEmpty() {
		if (cursor == null) return true;
		return cursor.getCount() == 0;
	}
	
	@Override
	public int getPositionForSection(int section) {
		// Prevent outofbounds if list is modified before arrays are refreshed
		if(section >= sections.length) section = (sections.length) - 1;
		return alphaIndex.get(sections[section]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return sectionAsIntegerForEveryPosition.get(position);
		
    }

	@Override
	public Object[] getSections() {
		Object[] returnMe3 = sections;
		return returnMe3;
	}
	
	@Override
	public Cursor getCursor() {
		return cursor;
	}

}
