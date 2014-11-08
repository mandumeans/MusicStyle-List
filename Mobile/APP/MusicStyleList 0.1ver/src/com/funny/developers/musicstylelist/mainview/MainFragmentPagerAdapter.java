package com.funny.developers.musicstylelist.mainview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.MainFragmentForNew;

public class MainFragmentPagerAdapter extends BaseFragmentPagerAdapter{

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	protected Fragment getFragmentView(int viewIndex) {
		
		Log.d("Error", "MainFragmentPagerAdapter getFragmentView");
		Fragment mFragment = null;
		
		switch(viewIndex){
		case 0 :
			mFragment = new MainFragmentForNew();
			break;
		
		case 1:
			mFragment = new MainFragmentForNew();
			break;
			
		default :
			break;
		}
		
		Log.d("Error", "MainFragmentPagerAdapter return");
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
