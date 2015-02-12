package com.funny.developers.musicstylelist.basepageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;

abstract public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {

	abstract protected Fragment getFragmentView(int viewIndex);

	abstract protected int getFragmentViewCount();

	abstract protected String getFragmentViewTitle(int viewIndex);

	protected SearchViewListener searchViewListener = null;

	//Search Listener
	public void setSearchListener(SearchViewListener searchViewListener){
		this.searchViewListener = searchViewListener;
	}

	public BaseFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int viewIndex) {
		return getFragmentView(viewIndex);
	}

	@Override
	public int getCount() {
		return getFragmentViewCount();
	}

	@Override
	public CharSequence getPageTitle(int viewIndex){
		return getFragmentViewTitle(viewIndex);
	}
}
