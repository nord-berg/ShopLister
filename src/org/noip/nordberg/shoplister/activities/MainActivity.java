package org.noip.nordberg.shoplister.activities;

import java.util.List;
import java.util.Vector;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.adapters.FragPagerAdapter;
import org.noip.nordberg.shoplister.adapters.NavSpinnerAdapter;
import org.noip.nordberg.shoplister.asynctasks.NavSpinnerBuilderAsyncTask;
import org.noip.nordberg.shoplister.asynctasks.ShareListAsyncTask;
import org.noip.nordberg.shoplister.constants.IntentActionConstants;
import org.noip.nordberg.shoplister.controllers.ItemsController;
import org.noip.nordberg.shoplister.fragments.CurrentListFragment;
import org.noip.nordberg.shoplister.fragments.FavListFragment;
import org.noip.nordberg.shoplister.utilities.AppRef;
import org.noip.nordberg.shoplister.utilities.InitUtils;
import org.noip.nordberg.shoplister.utilities.MiscUtils;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity implements OnNavigationListener {

	private PagerAdapter pagerAdapter;
	private ActionBar actionBar;
	private ViewPager pager;

	@Override
	protected void onResume() {
		super.onResume();
		AppRef.activity = this;
		pager.setCurrentItem(1,true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppRef.activity = null;
//		MiscUtils.hideKeyboard(getCurrentFocus());  // Causes crash
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Bugsense
//		BugSenseHandler.initAndStartSession(AppRef.context, "7b7e9a57");
		
		setContentView(R.layout.activity_main);

		overridePendingTransition(R.anim.slideinfromleft,R.anim.slideouttoright);
		
		InitUtils.setupPreferences(this);
		
		setupActionBar();
		
		InitUtils.setupTypeFaces();
		
		this.initialiseViewPaging();
		
		setupSpinner();
	}

	 private void setupActionBar() {
		 actionBar = getActionBar();
		 actionBar.setDisplayShowTitleEnabled(false);
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	 }

	 private void setupSpinner() {
		 new NavSpinnerBuilderAsyncTask(){
			 @Override
			 protected void onPostExecute(Void result) {
				 //  If no lists exist, prompt the user to create a new list
				 if(Statics.dropdownValues.isEmpty()) ItemsController.createNewListPrompt(false, MainActivity.this);

				 // Setup SpinnerAdapter
				 NavSpinnerAdapter adapter = new NavSpinnerAdapter(
						 actionBar.getThemedContext(),
						 android.R.layout.simple_spinner_item, android.R.id.text1, Statics.dropdownValues);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 // Setup the drop down list navigation in the action bar
				 actionBar.setListNavigationCallbacks(adapter, MainActivity.this);
				 // Set spinner position to MiscUtils.currentListName 
				 int position = Statics.dropdownValues.indexOf(Statics.currentListName);
				 actionBar.setSelectedNavigationItem(position);
				 super.onPostExecute(result);
			 }
		 }.execute();
	 }

	 private void initialiseViewPaging() {
		 // Setup the Fragments Pager Adapter
		 List<Fragment> fragments = new Vector<Fragment>();
		 fragments.add(Fragment.instantiate(this, FavListFragment.class.getName()));
		 fragments.add(Fragment.instantiate(this, CurrentListFragment.class.getName()));
		 pagerAdapter = new FragPagerAdapter(super.getSupportFragmentManager(), fragments);

		 // Setup the View Pager
		 pager = (ViewPager) super.findViewById(R.id.pager);
		 pager.setAdapter(pagerAdapter);
		 // Bind the title indicator to the adapter
		 TabPageIndicator titleIndicator = (TabPageIndicator)findViewById(R.id.tabs);
		 titleIndicator.setViewPager(pager);
		 pager.setCurrentItem(1,true);  // Set default fragment to "List"
	 }

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.activity_main, menu);
		 return true;
	 }

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch(item.getItemId()) {
		 case R.id.delete_list:
			 ItemsController.deleteListPrompt(this);
			 break;
		 case R.id.clean_list_button:
			 // If clicked from favorite list fragment, swap to current list (and don't clean)
			 if(pager.getCurrentItem() == 0) {
				 pager.setCurrentItem(1,true);
			 }
			 else CurrentListFragment.adapter.initCleanList();
			 break;
		 case R.id.share:
			 new ShareListAsyncTask().execute();
			 break;
		 case R.id.rename_list:
			 ItemsController.renameListPrompt(Statics.currentListName, this);
			 break;
		 case R.id.menu_settings:
			 Intent intent = new Intent(IntentActionConstants.SETTINGS_ACTIVITY);
			 startActivity(intent);
			 break;
		 }
		 return super.onOptionsItemSelected(item);
	 }

	 @Override
	 public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		 // Check if footer was clicked
		 Integer footerPosition = Statics.dropdownValues.size() - 1;
		 if(footerPosition.equals(itemPosition)) {
			 actionBar.setSelectedNavigationItem(Statics.dropdownValues.indexOf(Statics.currentListName));
			 if(Statics.dropdownValues.size() == 1) ItemsController.createNewListPrompt(false, this);
			 else ItemsController.createNewListPrompt(true, this);
			 return true;
		 }
		 else{
			 Statics.currentListName = Statics.dropdownValues.get(itemPosition);
			 // Add to sharedPreferences
			 Statics.prefEditor.putString("current_list_name", Statics.currentListName);
			 Statics.prefEditor.commit();
			 // Show the "list view" after changing lists
			 pager.setCurrentItem(1,true);  
			 // A new list has been selected, which means loaders have new 
			 // SelectionArgs. Restart loaders to pick up the change. 
			 MiscUtils.restartLoaderManager(CurrentListFragment.currentListFragmentRef);
			 MiscUtils.restartLoaderManager(FavListFragment.favListFragmentRef);
			 return true;
		 }
	 }
}