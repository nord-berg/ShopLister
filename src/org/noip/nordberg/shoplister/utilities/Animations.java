package org.noip.nordberg.shoplister.utilities;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.listeners.SlideOutRightAnimListener;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

public class Animations {

	public static final ScaleAnimation SCALE_ANIM;
	public static final Animation SLIDE_OUT_RIGHT_ANIM;
	public static final int SLIDE_OUT_DURATION = 250;

	static {

		// Scale Animation
		SCALE_ANIM = new ScaleAnimation(
				0f, 1.0f, 1.0f, 1.0f, 
				Animation.RELATIVE_TO_SELF, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.0f);
		SCALE_ANIM.setDuration(300);
		SCALE_ANIM.setInterpolator(new DecelerateInterpolator());
		
		// Slide out animation
		SLIDE_OUT_RIGHT_ANIM = AnimationUtils.loadAnimation(AppRef.context, R.anim.rowslideouttoright);
		SLIDE_OUT_RIGHT_ANIM.setDuration(SLIDE_OUT_DURATION);
		SLIDE_OUT_RIGHT_ANIM.setInterpolator(new AccelerateInterpolator());
		SLIDE_OUT_RIGHT_ANIM.setFillAfter(false);
		SLIDE_OUT_RIGHT_ANIM.setAnimationListener(new SlideOutRightAnimListener());
		
	}

}
