package com.funny.developers.musicstylelist.fragment.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.FragmentForPlayList;
import com.funny.developers.musicstylelist.fragment.FragmentForSearchTrack;

public class SearchFragmentPagerAdapter extends BaseFragmentPagerAdapter{

	private FragmentForSearchTrack fragmentForSearchTrack;
	private FragmentForPlayList fragmentForPlayList;
	
	private Context mContext;
	
	public SearchFragmentPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
		
		fragmentForSearchTrack = new FragmentForSearchTrack();
		fragmentForPlayList = new FragmentForPlayList();
		
		fragmentList.add(fragmentForSearchTrack);
		fragmentList.add(fragmentForPlayList);
	}

	@Override
	protected Fragment getFragmentView(int viewIndex) {
		Fragment mFragment = null;
		
		switch(viewIndex){
		case 0 :
			mFragment = fragmentForSearchTrack;
			break;
		
		case 1:
			mFragment = fragmentForPlayList;
			break;
		
		default :
			break;
		}
		
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
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_search_track);
			break;
		
		case 1:
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_search_playlist);
			break;
			
		default :
			mFragmentViewTitle = "";
			break;
		}
		
		return mFragmentViewTitle;
	}
}
