package com.funny.developers.musicstylelist.requesturl;

import com.funny.developers.musicstylelist.definition.Define;

public class RequestUrl {

	public static String BaseApiUrl(){
		return Define.BASE_API_URL;
	}
	
	public static String SearchYoutubeTrackUrl(){ 
		return Define.BASE_API_URL + "YoutubeKeywordSearch";
	}
	
	public static String SearchSoundCloudTrackUrl(){
		return Define.BASE_API_URL + "SoundcloudKeywordSearch";
	}

}
