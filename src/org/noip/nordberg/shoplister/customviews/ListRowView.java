package org.noip.nordberg.shoplister.customviews;

import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.CHECKED;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.EXTRAS;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.FAVOURITE;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.ITEM;
import static org.noip.nordberg.shoplister.constants.CurrentListCursorConstants.QUANTITY;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.database.CursorProvider;
import org.noip.nordberg.shoplister.listeners.CheckBoxOnClick;
import org.noip.nordberg.shoplister.listeners.ExtrasEditTextOnEditorAction;
import org.noip.nordberg.shoplister.listeners.ExtrasEditTextOnTouch;
import org.noip.nordberg.shoplister.listeners.ExtrasFillerOnTouch;
import org.noip.nordberg.shoplister.listeners.FavCheckBoxOnCheckedChanged;
import org.noip.nordberg.shoplister.listeners.IncQuantityButtonOnClick;
import org.noip.nordberg.shoplister.listeners.IncQuantityButtonOnLongClick;
import org.noip.nordberg.shoplister.listeners.ItemEditTextOnEditorAction;
import org.noip.nordberg.shoplister.listeners.ItemEditTextOnTouch;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ListRowView extends RelativeLayout {
	
	private Button incQuantityButton;
	private ToggleButton favToggleStar;
	private CheckBox checkBox;
	private Button extrasFiller;
	private TextView leftMarginFiller;
	private CursorProvider cursorProvider;
	private int position;
	private ImageView dragHandle;
	private static EditText extrasEditText;
	private static EditTextMultiLine itemEditText;

	public ListRowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setup(CursorProvider cursorProvider, int position){

		this.cursorProvider = cursorProvider;
		this.position = position;
		
		setupViewsFromLayout();

		setFonts();

		setupViews();

		setupStrikeThrough();

		setupFavToggle();

		setViewTags();

		setupListeners();

		setupExtrasExposed();
	}

	private void setupViews() {

		Cursor cursor = cursorProvider.getCursor();

		incQuantityButton.setLongClickable(true);

		// Some views will be set invisible etc. depending on preferences
		applyPrefsToViews();
		
		// Hide rows if they're half way through cleanup (after anim, before DB update).
		// Otherwise they slide out, then pop up in their original position before being removed
		if (Statics.rowsToHideAfterAnim.contains(position)) {
			this.setVisibility(View.GONE);
		}
		else {
			this.setVisibility(View.VISIBLE);
		}

		// Move cursor to position, then set text for views based on position
		cursor.moveToPosition(position);
		dragHandle.setVisibility(View.VISIBLE);  // not sure why this is here?
		itemEditText.setText(cursor.getString(ITEM));
		extrasEditText.setText(cursor.getString(EXTRAS));
		incQuantityButton.setText(cursor.getString(QUANTITY));

		// Set visibility of extras EditText (hide if empty)
		hideExtrasIfEmpty();

		// Only allow non-checked items to be edited
		lockEditingOnCheckedItems();
	}

	private void lockEditingOnCheckedItems() {
		if(cursorProvider.getCursor().getInt(CHECKED) == 1) {
			itemEditText.setFocusable(false);
			extrasEditText.setFocusable(false);
			extrasFiller.setEnabled(false);
			Statics.checkedRows.add(cursorProvider.getCursor().getPosition()); // used in drag n drop
		}
		else {
			itemEditText.setFocusableInTouchMode(true);
			extrasEditText.setFocusableInTouchMode(true);
			extrasFiller.setEnabled(true);
			Statics.checkedRows.remove(cursorProvider.getCursor().getPosition());
		}
	}

	private void hideExtrasIfEmpty() {
		if(cursorProvider.getCursor().getString(EXTRAS).compareTo("") == 0) {
			extrasEditText.setVisibility(View.GONE);
			extrasEditText.setMinEms(5); // Makes the view more noticeable when it first slides in
			extrasFiller.setEnabled(true);
		}
		else { 
			extrasEditText.setVisibility(View.VISIBLE);
			extrasEditText.setMinEms(3);
			extrasFiller.setEnabled(false);
		}
	}

	private void applyPrefsToViews() {
		// Set incQuantityButton visibility
		if (Statics.prefShowQuantity) incQuantityButton.setVisibility(View.VISIBLE);
		else incQuantityButton.setVisibility(View.GONE);

		// Show leftMarginFiller if dragHandle is on right (this was easier than modifying margins etc)
		if (Statics.prefDragHandleSide.equalsIgnoreCase("left")){
			leftMarginFiller.setVisibility(View.GONE);
		}
		if (Statics.prefDragHandleSide.equalsIgnoreCase("right")){
			leftMarginFiller.setVisibility(View.VISIBLE);
		}
	}

	private void setFonts() {
		itemEditText.setTypeface(Statics.robotoSerifTypeFace);
		extrasEditText.setTypeface(Statics.robotoSerifBoldTypeFace);
		incQuantityButton.setTypeface(Statics.robotoSerifTypeFace);
	}

	private void setupViewsFromLayout() {
		checkBox = (CheckBox) this.findViewById(R.id.checkBox);
		incQuantityButton = (Button) this.findViewById(R.id.incQuantityButton);
		favToggleStar = (ToggleButton) this.findViewById(R.id.favCheckBox);
		itemEditText = (EditTextMultiLine) this.findViewById(R.id.currentRowItemEditText);
		extrasEditText = (EditText) this.findViewById(R.id.extrasText);
		extrasFiller = (Button) this.findViewById(R.id.extrasFiller);
		leftMarginFiller = (TextView) this.findViewById(R.id.leftMarginFiller);
		if (Statics.prefDragHandleSide.equalsIgnoreCase("left")){
			dragHandle = (ImageView) this.findViewById(R.id.drag_handle_left);
		}
		if (Statics.prefDragHandleSide.equalsIgnoreCase("right")){
			dragHandle = (ImageView) this.findViewById(R.id.drag_handle_right);
		}
	}

	private void setupFavToggle() {
		int favStatus = cursorProvider.getCursor().getInt(FAVOURITE);
		if (favStatus == 1) favToggleStar.setChecked(true);
		else favToggleStar.setChecked(false);
	}

	/**
	 * View tags are set according to cursor position to help with queries
	 * @param cursorPosition
	 */
	private void setViewTags() {
		int cursorPosition = cursorProvider.getCursor().getPosition();

		incQuantityButton.setTag(cursorPosition);
		favToggleStar.setTag(cursorPosition);
		checkBox.setTag(cursorPosition);
		itemEditText.setTag(cursorPosition);
		extrasEditText.setTag(cursorPosition);
		extrasFiller.setTag(cursorPosition);
		dragHandle.setTag(cursorPosition);
	}

	/**
	 * Checks database for "checked" status, paints a 'strikethru' over checked text
	 */
	private void setupStrikeThrough() {
		int checkedStatus = cursorProvider.getCursor().getInt(CHECKED);
		if (checkedStatus == 1) {
			checkBox.setChecked(true);
			itemEditText.setPaintFlags(itemEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} 
		else {
			checkBox.setChecked(false);
			itemEditText.setPaintFlags(itemEditText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

	private void setupListeners() {
		incQuantityButton.setOnClickListener(new IncQuantityButtonOnClick(cursorProvider));
		checkBox.setOnClickListener(new CheckBoxOnClick(cursorProvider));
		extrasFiller.setOnTouchListener(new ExtrasFillerOnTouch());
		itemEditText.setOnTouchListener(new ItemEditTextOnTouch());
		extrasEditText.setOnTouchListener(new ExtrasEditTextOnTouch());
		incQuantityButton.setOnLongClickListener(new IncQuantityButtonOnLongClick(cursorProvider));
		itemEditText.setOnEditorActionListener(new ItemEditTextOnEditorAction(cursorProvider));
		extrasEditText.setOnEditorActionListener(new ExtrasEditTextOnEditorAction(cursorProvider));
		favToggleStar.setOnCheckedChangeListener(new FavCheckBoxOnCheckedChanged(cursorProvider));
	}

	/** A bit of a hack to close down any empty extrasEditText views. 
	 * Description: It's possible for the user to open the extrasEditText view and then
	 * click away without entering data, leaving it in an open, empty state.
	 * Using an onFocusChanged listener isn't possible because i'm using so many
	 * edittexts in a listview that focus is stolen often.  (Google don't recommend
	 * putting edittexts in listviews, probably for this reason)
	 */
	private void setupExtrasExposed() {
		Statics.extrasExposed = false;
		Statics.extrasExposedRow = -1;
	}
}
