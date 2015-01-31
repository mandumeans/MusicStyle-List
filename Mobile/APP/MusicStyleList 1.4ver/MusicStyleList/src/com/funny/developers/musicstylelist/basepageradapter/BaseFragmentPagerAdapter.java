package com.funny.developers.musicstylelist.basepageradapter;

import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

abstract public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
	
	//각 View index에 대한 Fragment를 리턴한다.
	abstract protected Fragment getFragmentView(int viewIndex);
	
	//Fragment 개수를 리턴한다.
	abstract protected int getFragmentViewCount();
	
	//Fragment title을 리턴한다.
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
