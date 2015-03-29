package com.funny.developers.musicstylelist.definition;

import android.net.Uri;

public class NotifyUrlDefine {
	
	public static final Uri URI_READY_MEDIAPLAYER = Uri.parse("content://com.funny.developers.mediaplayer.ready.for.play");
	public static final Uri URI_LOAD_MEDIAPLAYER = Uri.parse("content://com.funny.developers.mediaplayer.load.for.play");
	public static final Uri URI_PLAY_MEDIAPLAYER = Uri.parse("content://com.funny.developers.mediaplayer.play");
	public static final Uri URI_EMPTY_MEDIAPLAYER = Uri.parse("content://com.funny.developers.mediaplayer.empty");
	
	public static final Uri URI_NOTIFY_DATASET_CHANGE = Uri.parse("content://com.funny.developers.dataset.change");
}
