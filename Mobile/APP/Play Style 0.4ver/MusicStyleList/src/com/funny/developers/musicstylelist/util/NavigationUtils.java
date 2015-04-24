package com.funny.developers.musicstylelist.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.funny.developers.musicstylelist.activity.PlayListDetailActivity;
import com.funny.developers.musicstylelist.activity.SettingsActivity;
import com.funny.developers.musicstylelist.activity.UserPlayListDetailActivity;
import com.funny.developers.musicstylelist.activity.PlayerViewActivity;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.BaseModel;

public class NavigationUtils {
	
	public static void goPlaylistDetail(Context context, String requestId, int requestType, String playlistTitle, int playlistTrackNum, String playlistThumnailUrl){
		
		Intent intent = new Intent(context, PlayListDetailActivity.class);
		intent.putExtra(Define.PLAYLISTDETAIL_REQUEST_ID, requestId);
		intent.putExtra(Define.PLAYLISTDETAIL_REQUEST_TYPE, requestType);
		intent.putExtra(Define.PLAYLISTDETAIL_TRACK_LIST_TITLE, playlistTitle);
		intent.putExtra(Define.PLAYLISTDETAIL_THUMNAIL_URL, playlistThumnailUrl);
		intent.putExtra(Define.PLAYLISTDETAIL_TRACK_LIST_NUM, playlistTrackNum);
		context.startActivity(intent);
	}
	
	public static void goUserPlayDetail(Context context, int folderNo, int requestType, String playlistTitle, String playlistThumnailUrl, int playlistTrackNum){
		
		Intent intent = new Intent(context, UserPlayListDetailActivity.class);
		intent.putExtra(Define.USER_PLAYLISTDETAIL_FOLDER_NO, folderNo);
		intent.putExtra(Define.USER_PLAYLISTDETAIL_REQUEST_TYPE, requestType);
		intent.putExtra(Define.USER_PLAYLISTDETAIL_TRACK_LIST_TITLE, playlistTitle);
		intent.putExtra(Define.USER_PLAYLISTDETAIL_THUMNAIL_URL, playlistThumnailUrl);
		intent.putExtra(Define.USER_PLAYLISTDETAIL_TRACK_LIST_NUM, playlistTrackNum);
		context.startActivity(intent);
	}
	
	public static void goPlayerActivity(Context context, ArrayList<BaseModel> trackList, int playTrackNum){
		
		Intent intent = new Intent(context, PlayerViewActivity.class);
		intent.putExtra(Define.PLAYER_TRACK_LIST_INTENT, trackList);
		intent.putExtra(Define.PLAYER_TRACK_LIST_POSITION, playTrackNum);
		context.startActivity(intent);
	}
	
	public static void goSettings(Context context) {
		
		Intent intent = new Intent(context, SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
