package com.funny.developers.musicstylelist.definition;

public class Define {

	//searchUrl
	public static final String BASE_API_URL = "http://rlaqnqn.dothome.co.kr/MusicListWS.asmx/";

	public static final String SEARCH_YOUTUBE_TRACK_SEARCH_API_URL = "YoutubeTrackSearch";
	public static final String SEARCH_SOUNDCLOUD_TRACK_SEARCH_API_URL = "SoundcloudTrackSearch";

	public static final String SEARCH_YOUTUBE_PLAYLIST_SEARCH_API_URL = "YoutubePlaylistSearch";
	public static final String SEARCH_SOUNDCLOUD_PLAYLIST_SEARCH_API_URL = "SoundcloudPlaylistSearch";

	public static final String SEARCH_YOUTUBE_PLAYLIST_DETAIL_SEARCH_API_URL = "YoutubePlaylistDetailSearch";
	public static final String SEARCH_SOUNDCLOUD_PLAYLIST_DETAIL_SEARCH_API_URL = "SoundCloudPlaylistDetailSearch";
	
	public static final String HOT_YOUTUBE_TRACK_SEARCH_API_URL = "YoutubeHotTrackSearch";
	public static final String HOT_SOUNDCLOUD_TRACK_SEARCH_API_URL = "SoundcloudHotTrackSearch";

	//youtube rtsp or video_token
	public static final String YOUTUBE_VIDEO_INFORMATION_URL = "http://www.youtube.com/get_video_info?&video_id=";
	public static final String YOUTUBE_VIDEO_INFORMATION_RTSP_URL = "http://gdata.youtube.com/feeds/mobile/videos/";
	public static final String YOUTUBE_VIDEO_INFORMATION_RTSP_URL_JSON = "?alt=json";
	
	//not token
	public static final String NOT_NEXT_TOKEN = "not_next_token";
	
	//youtube not uploader
	public static final String YOUTUBE_NOT_UPLOADER = "Youtube";

	//youtube image type
	public static final String YOUTUBE_THUMNAIL_DEFAULT = "default";
	public static final String YOUTUBE_THUMNAIL_HQDEFAULT = "hqdefault";

	//soundcloud track url
	public static final String SOUNDCLOUD_PLAY_URL = "https://api.soundcloud.com/tracks/";
	
	//soundcloud image type
	public static final String SOUNDCLOUD_THUMNAIL_LARGE = "large";
	public static final String SOUNDCLOUD_THUMNAIL_T300X300 = "t300x300";
	public static final String SOUNDCLOUD_THUMNAIL_CROP = "crop";
	public static final String SOUNDCLOUD_THUMNAIL_T500X500 = "t500x500";

	//search type
	public static final int YOUTUBE_SEARCH = 1;
	public static final int SOUND_CLOUD_SEARCH = 2;

	//track type
	public static final int YOUTUBE_TRACK = 1;
	public static final int SOUNDCLOUD_TRACK = 2;

	//Youtube key
	public static final String YOUTUBE_API_KEY = "AIzaSyDFblTWTo8YvdJT-RSh3YsRZugmLVg1vLk";

	//SoundCloud key
	public static final String SOUND_CLOUD_API_KEY = "a2b4d87e3bac428d8467d6ea343d49ae";

	//PlayListDetailActivity intent data filter
	public static final String PLAYLISTDETAIL_REQUEST_ID = "playlistdetail_request_id";
	public static final String PLAYLISTDETAIL_REQUEST_TYPE = "playlistdetail_request_type";
	public static final String PLAYLISTDETAIL_TRACK_LIST_TITLE = "playlistdetail_track_list_title";
	public static final String PLAYLISTDETAIL_TRACK_LIST_NUM = "playlistdetail_track_list_num";
	public static final String PLAYLISTDETAIL_THUMNAIL_URL = "playlistdetail_thumnail_url";

	//UserPlayListDetailActivity intent data filter
	public static final String USER_PLAYLISTDETAIL_REQUEST_TYPE = "user_playlistdetail_request_type";
	public static final String USER_PLAYLISTDETAIL_FOLDER_NO = "user_playlistdetail_folder_no";
	public static final String USER_PLAYLISTDETAIL_TRACK_LIST_TITLE = "user_playlistdetail_track_list_title";
	public static final String USER_PLAYLISTDETAIL_TRACK_LIST_NUM = "user_playlistdetail_track_list_num";
	public static final String USER_PLAYLISTDETAIL_THUMNAIL_URL = "user_playlistdetail_thumnail_url";

	//YoutubePlayerViewActivity intent data filter
	public static final String PLAYER_TRACK_LIST_INTENT = "player_track_list_intent";
	public static final String PLAYER_TRACK_LIST_POSITION = "player_track_list_position";

	//preference object name
	public static final String PREFERENCE_MEDIAPLAYER_REPEAT_TYPE = "preference_mediaplayer_repeat_type";
	public static final String PREFERENCE_MEDIAPLAYER_SHUFFLE_TYPE = "preference_mediaplayer_shuffle_type";

	//preference file name
	public static final String PREFERENCE_MEDIAPLAYER_FILE_NAME = "preference_mediaplayer_file";
	
	//handler what, delay_time
	public static final int HANDLER_WHAT = 0;
	public static final int HANDLER_DISPLAY_DELAY_TIME = 1000;
	public static final int HANDLER_MEDIAPLAYER_ERROR_DELAY_TIME = 10000;
}
