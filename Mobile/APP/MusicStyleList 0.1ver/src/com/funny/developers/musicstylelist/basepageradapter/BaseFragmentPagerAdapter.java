package com.funny.developers.musicstylelist.basepageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

abstract public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
	
	//�� View index�� ���� Fragment�� �����Ѵ�.
	abstract protected Fragment getFragmentView(int viewIndex);
	
	//Fragment ������ �����Ѵ�.
	abstract protected int getFragmentViewCount();
	
	//Fragment title�� �����Ѵ�.
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
