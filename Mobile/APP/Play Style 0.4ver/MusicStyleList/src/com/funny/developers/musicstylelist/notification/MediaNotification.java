package com.funny.developers.musicstylelist.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.mainactivity.MainActivity;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayer;


public class MediaNotification {
	
	private Context mContext;
	
	private Intent resultIntent;
	private PendingIntent resultPendingIntent;
	
	public MediaNotification(){
		super();
	}
	
	public MediaNotification(Context context){
		super();
		
		mContext = context;
		
		resultIntent = new Intent(context, MainActivity.class);
		resultIntent.setAction(Intent.ACTION_MAIN);
		resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	public NotificationCompat.Builder getCustomNotification(String title, String uploader){
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setSmallIcon(R.drawable.icon_64);
		
		RemoteViews notiView = new RemoteViews(mContext.getPackageName(), R.layout.layout_for_notification);
		
		notiView.setImageViewResource(R.id.noti_thumnail, R.drawable.icon_64);
		notiView.setTextViewText(R.id.noti_track_title, title);
		notiView.setTextViewText(R.id.noti_track_uploader, uploader);
		notiView.setImageViewResource(R.id.noti_close_button, R.drawable.ic_action_cancel);
		notiView.setImageViewResource(R.id.noti_show_button, R.drawable.ic_action_slideshow);
		setListener(notiView);
		
		mBuilder.setContent(notiView);
		
		return mBuilder;
	}
	
	public void setListener(RemoteViews notiView){
		
		Intent closeIntent = new Intent(MediaPlayerFunctionDefine.NOTIFICATION_CLOSE_INTENT_ID).setClass(mContext, MusicStyleListMediaPlayer.class);
		PendingIntent closePendingIntent = PendingIntent.getService(mContext, 0, closeIntent, 0);
		
		Intent showIntent = new Intent(MediaPlayerFunctionDefine.NOTIFICATION_SHOW_INTENT_ID).setClass(mContext, MusicStyleListMediaPlayer.class);
		PendingIntent showPendingIntent = PendingIntent.getService(mContext, 0, showIntent, 0);
		
		notiView.setOnClickPendingIntent(R.id.noti_close_button, closePendingIntent);
		notiView.setOnClickPendingIntent(R.id.noti_show_button, showPendingIntent);
	}

}
