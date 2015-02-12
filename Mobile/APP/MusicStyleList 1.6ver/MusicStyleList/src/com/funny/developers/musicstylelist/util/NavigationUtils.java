package com.funny.developers.musicstylelist.util;

import java.util.ArrayList;

import android.content.Context;

import com.funny.developers.musicstylelist.mainactivity.MainFragmentActivity;
import com.funny.developers.musicstylelist.model.BaseModel;

public class NavigationUtils {
	
	private static MainFragmentActivity activity = null;
	
	public static void setPlaylistDetail(Context context, String requestId, int requestType){
		activity = (MainFragmentActivity) context;
		activity.settingPlayListDetailView(requestId, requestType);
	}
	
	public static void setUserPlayDetail(Context context, ArrayList<BaseModel> trackList){
		activity = (MainFragmentActivity) context;
		activity.settingUserPlayListDetailView(trackList);
	}
	
	public static void setMediaPlayer(Context context, ArrayList<BaseModel> trackList, int playTrackNum){
		activity = (MainFragmentActivity) context;
		activity.settingMediaPlayerView(trackList, playTrackNum);
	}
}
