package com.funny.developers.musicstylelist.fragment.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.FragmentForUserPlayList;
import com.funny.developers.musicstylelist.fragment.FragmentForUserTrackList;

public class PlayListFragmentPagerAdapter extends BaseFragmentPagerAdapter {
	
	private FragmentForUserTrackList fragmentForUserTrackList;
	private FragmentForUserPlayList fragmentForUserPlayList;
	
	private Context mContext;
	
	public PlayListFragmentPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
		
		fragmentForUserTrackList = new FragmentForUserTrackList();
		fragmentForUserPlayList = new FragmentForUserPlayList();
		
		fragmentList.add(fragmentForUserTrackList);
		fragmentList.add(fragmentForUserPlayList);
	}
	
	@Override
	protected Fragment getFragmentView(int viewIndex) {
		Fragment mFragment = null;
		
		switch(viewIndex){
		case 0 :
			mFragment = fragmentForUserTrackList;
			break;
			
		case 1 :
			mFragment = fragmentForUserPlayList;
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
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_user_track);
			break;
			
		case 1:
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_user_playlist);
			break;
			
		default :
			mFragmentViewTitle = "";
			break;
		}
		
		return mFragmentViewTitle;
	}
}
