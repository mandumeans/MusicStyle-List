package com.funny.developers.musicstylelist.naviutil;

import com.funny.developers.musicstylelist.activity.PlayListDetailActivity;

import android.content.Context;
import android.content.Intent;

public class NavigationUtils {
	
	public static void goPlaylistDetail(Context context, String id, int type){
		Intent intent = new Intent(context,PlayListDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("type", type);
		
		context.startActivity(intent);
	}
}
