package org.noip.nordberg.shoplister.adapters;

import java.util.List;

import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class NavSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

	private int resource;
	private int textViewResourceId;
	private List<String> items;
	private TextView item;
	
	public NavSpinnerAdapter(Context context, int resource,
			int textViewResourceId, List<String> items) {
		super(context, resource, textViewResourceId, items);
		this.resource = resource;
		this.textViewResourceId = textViewResourceId;
		this.items = items;
	}
	
	// List views
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View outputView = inflater.inflate(resource, null);
		item = (TextView) outputView.findViewById(textViewResourceId);
		item.setTypeface(Statics.robotoSerifTypeFace);
		item.setPadding(
				MiscUtils.dpToPx(12),
				MiscUtils.dpToPx(12),
				MiscUtils.dpToPx(12),
				MiscUtils.dpToPx(12));
		item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		item.setText(items.get(position)); 
		// Make "Add New List" footer italics
		if (position == (items.size() -1)) item.setTypeface(Statics.robotoSerifTypeFace, Typeface.ITALIC);
		return outputView;
	}
	
	// Heading view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View outputView = inflater.inflate(resource, null);
		item = (TextView) outputView.findViewById(textViewResourceId);
		item.setTypeface(Statics.robotoSerifBoldTypeFace);
		item.setText(items.get(position)); 
		return outputView;
	}

}
