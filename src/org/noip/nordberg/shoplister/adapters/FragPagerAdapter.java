package org.noip.nordberg.shoplister.adapters;


import java.util.List;
import java.util.Locale;

import org.noip.nordberg.shoplister.R;
import org.noip.nordberg.shoplister.utilities.AppRef;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragPagerAdapter extends FragmentPagerAdapter {
	
	private final List<Fragment> fragments;
	private static final String[] TITLES = new String[] {
		AppRef.context.getString(R.string.fragment_title_favourites),
		AppRef.context.getString(R.string.fragment_title_currentlist)};
	
	public FragPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}
	
	@Override
	public int getCount() {
		return this.fragments.size();
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position % TITLES.length].toUpperCase(Locale.getDefault());
    }
	
	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}
	
}
