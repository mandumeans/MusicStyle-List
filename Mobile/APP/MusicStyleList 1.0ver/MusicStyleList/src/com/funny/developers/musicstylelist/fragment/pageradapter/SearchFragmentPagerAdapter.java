package com.funny.developers.musicstylelist.fragment.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.FragmentForPlayList;
import com.funny.developers.musicstylelist.fragment.FragmentForSearchTrack;
import com.funny.developers.musicstylelist.fragment.FragmentForUserPlayList;

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
			mFragment = new FragmentForPlayList();
			break;
		
		case 2:
			mFragment = new FragmentForUserPlayList();
			
		default :
			break;
		}
		
		return mFragment;
	}

	@Override
	protected int getFragmentViewCount() {
		return 3;
	}

	@Override
	protected String getFragmentViewTitle(int viewIndex) {
		
		String mFragmentViewTitle = null;
		
		switch(viewIndex){
		case 0:
			mFragmentViewTitle = "Track";
			break;
		
		case 1:
			mFragmentViewTitle = "Playlist";
			break;
			
		case 2:
			mFragmentViewTitle = "Mylist";
			break;
			
		default :
			mFragmentViewTitle = "Error";
			break;
		}
		
		return mFragmentViewTitle;
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		
		if(object instanceof BaseFragment){
			searchViewListener.setNowFragment((BaseFragment) object);
		}
	}
}