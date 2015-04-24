package com.funny.developers.musicstylelist.requesturl;

import com.funny.developers.musicstylelist.definition.Define;

public class RequestUrl {

	public static String BaseApiUrl(){
		return Define.BASE_API_URL;
	}
	
	public static String SearchYoutubeTrackUrl(){ 
		return Define.BASE_API_URL + Define.SEARCH_YOUTUBE_TRACK_SEARCH_API_URL;
	}
	
	public static String SearchSoundCloudTrackUrl(){
		return Define.BASE_API_URL + Define.SEARCH_SOUNDCLOUD_TRACK_SEARCH_API_URL;
	}
	
	public static String SearchYoutubePlayListUrl(){ 
		return Define.BASE_API_URL + Define.SEARCH_YOUTUBE_PLAYLIST_SEARCH_API_URL;
	}
	
	public static String SearchSoundCloudPlayListUrl(){
		return Define.BASE_API_URL + Define.SEARCH_SOUNDCLOUD_PLAYLIST_SEARCH_API_URL;
	}
	
	public static String SearchYoutubePlayListDetailUrl(){
		return Define.BASE_API_URL + Define.SEARCH_YOUTUBE_PLAYLIST_DETAIL_SEARCH_API_URL;
	}
	
	public static String SearchSoundCloudPlayListDetailUrl(){
		return Define.BASE_API_URL + Define.SEARCH_SOUNDCLOUD_PLAYLIST_DETAIL_SEARCH_API_URL;
	}
	
	public static String HotYoutubeTrackSearchUrl(){
		return Define.BASE_API_URL + Define.HOT_YOUTUBE_TRACK_SEARCH_API_URL;
	}
	
	public static String HotSoundCloudTrackSearchUrl(){
		return Define.BASE_API_URL + Define.HOT_SOUNDCLOUD_TRACK_SEARCH_API_URL;
	}

}
