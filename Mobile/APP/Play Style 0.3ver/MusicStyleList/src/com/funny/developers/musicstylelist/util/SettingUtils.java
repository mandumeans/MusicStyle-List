package com.funny.developers.musicstylelist.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;

public class SettingUtils {

	public static void setMediaplayerRepeatType(Context context, int repeatType){
		SharedPreferences prefs = context.getSharedPreferences(Define.PREFERENCE_MEDIAPLAYER_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(Define.PREFERENCE_MEDIAPLAYER_REPEAT_TYPE, repeatType);
		editor.commit();
	}
	
	public static void setMediaplayerShuffleType(Context context, int shuffleType){
		SharedPreferences prefs = context.getSharedPreferences(Define.PREFERENCE_MEDIAPLAYER_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(Define.PREFERENCE_MEDIAPLAYER_SHUFFLE_TYPE, shuffleType);
		editor.commit();
	}
	
	public static int getMediaplayerRepeatType(Context context){
		SharedPreferences prefs = context.getSharedPreferences(Define.PREFERENCE_MEDIAPLAYER_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(Define.PREFERENCE_MEDIAPLAYER_REPEAT_TYPE, MediaPlayerFunctionDefine.REPEAT_NO);
	}
	
	public static int getMediaplayerShuffleType(Context context){
		SharedPreferences prefs = context.getSharedPreferences(Define.PREFERENCE_MEDIAPLAYER_FILE_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(Define.PREFERENCE_MEDIAPLAYER_SHUFFLE_TYPE, MediaPlayerFunctionDefine.SHUFFLE_OFF);
	}
	
	public static boolean getWiFilock(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("pref_only_wifi", false);
	}
	
	public static String getSoundCloudFavoriteType(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("pref_prefer_type", "");
	}
	
	public static String getYoutubeFavoriteType(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("pref_youtube_prefer_type", "");
	}
}
