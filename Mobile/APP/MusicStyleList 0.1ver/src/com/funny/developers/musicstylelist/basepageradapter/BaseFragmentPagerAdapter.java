package com.funny.developers.musicstylelist.basepageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

abstract public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
	
	//각 View index에 대한 Fragment를 리턴한다.
	abstract protected Fragment getFragmentView(int viewIndex);
	
	//Fragment 개수를 리턴한다.
	abstract protected int getFragmentViewCount();
	
	//Fragment title을 리턴한다.
	abstract protected String getFragmentViewTitle(int viewIndex);
	
	public BaseFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int viewIndex) {
		Log.d("Error", "BaseFragmentPagerAdapter getItem");
		return getFragmentView(viewIndex);
	}

	@Override
	public int getCount() {
		Log.d("Error", "BaseFragmentPagerAdapter getCount");
		return getFragmentViewCount();
	}
	
	@Override
	public CharSequence getPageTitle(int viewIndex){
		Log.d("Error", "BaseFragmentPagerAdapter getPageTitle");
		return getFragmentViewTitle(viewIndex);
	}

}
