package com.funny.developers.musicstylelist.basepageradapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;

abstract public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

	abstract protected Fragment getFragmentView(int viewIndex);

	abstract protected int getFragmentViewCount();

	abstract protected String getFragmentViewTitle(int viewIndex);

	protected SearchViewListener searchViewListener;
	protected ArrayList<BaseFragment> fragmentList;

	//Search Listener
	public void setSearchListener(SearchViewListener searchViewListener){
		
		if(fragmentList == null){
			return;
		}
		
		this.searchViewListener = searchViewListener;
		this.searchViewListener.setFragmentList(fragmentList);
	}

	public BaseFragmentPagerAdapter(FragmentManager fm) {
		super(fm);

		fragmentList = new ArrayList<BaseFragment>();
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

	public void clearAll(FragmentManager fm)
	{
		for(int i=0; i < fragmentList.size(); i++)
			fm.beginTransaction().remove(fragmentList.get(i)).commit();
		fragmentList.clear();
	}
}
