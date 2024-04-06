package org.noip.nordberg.shoplister.utilities;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class AppRef extends Application {
	
	public static Activity activity;
	public static Context context;
	
	public void onCreate() {
		context = this;
	};


}
