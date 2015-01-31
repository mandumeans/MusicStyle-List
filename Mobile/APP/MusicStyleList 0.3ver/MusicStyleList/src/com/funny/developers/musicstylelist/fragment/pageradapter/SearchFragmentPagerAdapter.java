package com.funny.developers.musicstylelist.fragment.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.FragmentForSearchTrack;

public class SearchFragmentPagerAdapter extends BaseFragmentPagerAdapter{

	public SearchFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	protected Fragment getFragmentView(int viewIndex) {
		
		Fragment mFragment = null;
		
		switch(viewIndex){
		case 0 :
			mFragment = new FragmentForSearchTrack();
			break;
		
		case 1:
			mFragment = new FragmentForSearchTrack();
			break;
			
		default :
			break;
		}
		
		searchViewListener.setNowFragment((BaseFragment) mFragment);
		return mFragment;
	}

	@Override
	protected int getFragmentViewCount() {
		return 2;
	}

	@Override
	protected String getFragmentViewTitle(int viewIndex) {
		
		String mFragmentViewTitle = null;
		
		switch(viewIndex){
		case 0:
			mFragmentViewTitle = "타이틀1";
			break;
		
		case 1:
			mFragmentViewTitle = "타이틀2";
			break;
			
		default :
			mFragmentViewTitle = "에러다 이자식아";
			break;
		}
		
		return mFragmentViewTitle;
	}
}
