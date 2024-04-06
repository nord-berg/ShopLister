package org.noip.nordberg.shoplister.activities;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.constants.IntentActionConstants;
import org.noip.nordberg.shoplister.fragments.SettingsFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slideinfromright,R.anim.slideouttoleft);
		
		// Display settings fragment
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
		
		// Set AB icon to navigate up
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(IntentActionConstants.MAIN_ACTIVITY);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		// Force reloading of main activity
        Intent intent = new Intent(IntentActionConstants.MAIN_ACTIVITY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
		super.onBackPressed();
	}
	
	
}
