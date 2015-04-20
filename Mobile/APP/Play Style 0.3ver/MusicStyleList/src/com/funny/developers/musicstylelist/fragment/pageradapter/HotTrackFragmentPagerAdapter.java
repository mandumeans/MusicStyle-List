package com.funny.developers.musicstylelist.fragment.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.FragmentForHotSoundCloudTrack;
import com.funny.developers.musicstylelist.fragment.FragmentForHotYoutubeTrack;

public class HotTrackFragmentPagerAdapter extends BaseFragmentPagerAdapter{
	
	private FragmentForHotYoutubeTrack fragmentForHotYoutubeTrack;
	private FragmentForHotSoundCloudTrack fragmentForHotSoundCloudTrack;
	
	private Context mContext;
	
	public HotTrackFragmentPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		
		mContext = context;
		
		fragmentForHotYoutubeTrack = new FragmentForHotYoutubeTrack();
		fragmentForHotSoundCloudTrack = new FragmentForHotSoundCloudTrack();
		
		fragmentList.add(fragmentForHotYoutubeTrack);
		fragmentList.add(fragmentForHotSoundCloudTrack);
	}
	
	@Override
	protected Fragment getFragmentView(int viewIndex) {
		Fragment mFragment = null;
		
		switch(viewIndex){
		case 0 :
			mFragment = fragmentForHotYoutubeTrack;
			break;
		
		case 1:
			mFragment = fragmentForHotSoundCloudTrack;
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
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_hot_youtube_track);
			break;
		
		case 1:
			mFragmentViewTitle = mContext.getString(R.string.fragment_title_hot_soundcloud_track);
			break;
			
		default :
			mFragmentViewTitle = "";
			break;
		}
		
		return mFragmentViewTitle;
	}
}
