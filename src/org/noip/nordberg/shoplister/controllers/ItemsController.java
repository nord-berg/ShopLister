package org.noip.nordberg.shoplister.controllers;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.asynctasks.queries.AddNewItemAsyncTask;
import org.noip.nordberg.shoplister.database.ContProvider;
import org.noip.nordberg.shoplister.database.DBHelper;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.widget.EditText;

public class ItemsController {

	/**
	 * @param activity
	 */
	public static void deleteListPrompt(final Activity activity) {
		// Get user confirmation they really want to delete
		AlertDialog.Builder deleteListPrompt = new AlertDialog.Builder(AppRef.activity);
		deleteListPrompt.setTitle(AppRef.context.getString(R.string.delete_confirmation_header))
		.setMessage(AppRef.context.getString(R.string.delete_confirmation));
		deleteListPrompt.setPositiveButton((AppRef.context.getString(R.string.positive_button)), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Delete all items belonging to list
				Uri uri = Uri.parse("content://org.noip.nordberg.shoplister/items");
				String selection = DBHelper.KEY_LIST + "=?";
				String[] selectionArgs = new String[]{Statics.currentListName};
				AppRef.context.getContentResolver().delete(uri, selection, selectionArgs);
				// Recreate activity to update spinner
				activity.recreate(); 
			}
		});
		deleteListPrompt.setNegativeButton((AppRef.context.getString(R.string.negative_button)), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Nothing needed here
			}
		});
		deleteListPrompt.show();
	}

	/**
	 * @param listOld
	 * @param activity
	 */
	public static void renameListPrompt(final String listOld, final Activity activity) {
		AlertDialog.Builder renameListPrompt = new AlertDialog.Builder(AppRef.activity);
		// Set it up
		renameListPrompt.setTitle(AppRef.context.getString(R.string.rename_list_prompt_title) + listOld)
		.setMessage(AppRef.context.getString(R.string.rename_list_prompt_message));
		// Add an EditText to get input
		final EditText input = new EditText(activity);
		input.setSingleLine(true);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		renameListPrompt.setView(input);
		input.setText(listOld);
		input.setSelectAllOnFocus(true);
	
		// Setup buttons
		renameListPrompt.setPositiveButton(AppRef.context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String inputValue = input.getText().toString().trim();
				String listNew = inputValue;
				// Enter values into database:
				// Setup the new list name values
				ContentValues newValues = new ContentValues();
				newValues.put("list", listNew);
				// Setup the query
				String selection = DBHelper.KEY_LIST + "=?";
				String[] selectionArgs = new String[]{listOld};
				// Run the query
				AppRef.context.getContentResolver().update(ContProvider.CONTENT_URI, newValues, selection, selectionArgs);
				// Add to sharedPreferences
				Statics.prefEditor.putString("current_list_name", listNew);
				Statics.prefEditor.commit();
				// Hide keyboard
				MiscUtils.hideKeyboard(input); 
				// Recreate activity to update spinner
				activity.recreate();
			}
		});
		renameListPrompt.setNegativeButton(AppRef.context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MiscUtils.hideKeyboard(input); 
			}
		});
		AlertDialog ad = renameListPrompt.create();
		MiscUtils.showKeyboardInAlertDialog(ad);
		ad.show();
	}

	/**
	 * @param showCancelButton
	 */
	public static void createNewListPrompt(boolean showCancelButton, final Activity activity) {
		AlertDialog.Builder newListPrompt = new AlertDialog.Builder(activity);
		// Set it up
		newListPrompt.setTitle(AppRef.context.getString(R.string.create_new_list_prompt_title))
		.setMessage(AppRef.context.getString(R.string.create_new_list_prompt_message));
		// Add an EditText to get input
		final EditText input = new EditText(activity);
		input.setSingleLine(true);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		newListPrompt.setView(input);
		// Setup buttons
		newListPrompt.setPositiveButton(AppRef.context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String inputValue = input.getText().toString().trim();
				if(inputValue.isEmpty()) inputValue = "List";
				Statics.currentListName = inputValue;
				// Add to sharedPreferences
				Statics.prefEditor.putString("current_list_name", Statics.currentListName);
				Statics.prefEditor.commit();
				// Hide keyboard
				MiscUtils.hideKeyboard(input); 
				// Add blank item
				addBlankItem(Statics.currentListName, activity);
			}
		});
		if(showCancelButton){
			newListPrompt.setNegativeButton(AppRef.context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MiscUtils.hideKeyboard(input); 
				}
			});
		}
		AlertDialog ad = newListPrompt.create();
		MiscUtils.showKeyboardInAlertDialog(ad);
		ad.show();
	}
	
	/**
	 * Creates a blank item which will not be seen by the user.  When the navigation
	 * spinner builds, it runs a query searching for all unique list names.  This blank
	 * item serves only to appear in that query (otherwise "empty" lists wouldn't show up in the spinner)
	 * @param list
	 */
	public static void addBlankItem(String list, final Activity activity) {
		new AddNewItemAsyncTask("", list, 0){

			@Override
			protected void onPostExecute(Void result) {
				// Recreate activity to update spinner
				activity.recreate();  
			}
		}.execute();
	}
}
