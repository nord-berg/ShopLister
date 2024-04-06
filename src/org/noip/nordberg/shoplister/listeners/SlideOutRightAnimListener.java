package org.noip.nordberg.shoplister.listeners;

import org.noip.nordberg.shoplister.asynctasks.CleanupListAsyncTask;
import org.noip.nordberg.shoplister.utilities.Statics;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class SlideOutRightAnimListener implements AnimationListener{

	@Override
	public void onAnimationEnd(Animation animation) {
//		Log.i("nord", "Animation finished");
		Statics.currentListView.invalidateViews();
		new CleanupListAsyncTask().execute();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
//		Log.i("nord", "Animation started");
		
	}
	
}
