package com.funny.developers.musicstylelist.parameterurl;

import com.funny.developers.musicstylelist.util.DigitUtils;


public class ParameterUrl {

	public static String SearchYoutubeTrack(String query, String pageToken){
		StringBuilder parameter = new StringBuilder();

		parameter.append("keyword=");
		parameter.append(DigitUtils.urlParameter(query));
		parameter.append("&pageToken=");
		parameter.append(pageToken);

		return parameter.toString();
	}

	public static String SearchSoundCloudTrack(String query, String index){
		StringBuilder parameter = new StringBuilder();

		parameter.append("keyword=");
		parameter.append(DigitUtils.urlParameter(query));
		parameter.append("&index=");
		
		if(index.length() > 0){
			parameter.append(index);
		} else {
			parameter.append(1);
		}

		return parameter.toString();
	}

	//playlist detail search
	public static String SearchYoutubePlayListDetail(String id, String pageToken){
		StringBuilder parameter = new StringBuilder();

		parameter.append("playlistId=");
		parameter.append(id);
		parameter.append("&pageToken=");
		parameter.append(pageToken);

		return parameter.toString();
	}
	
	public static String SearchSoundCloudPlayListDetail(String id, String index){
		StringBuilder parameter = new StringBuilder();

		parameter.append("id=");
		parameter.append(id);
		parameter.append("&index=");
		
		if(index.length() > 0){
			parameter.append(index);
		} else {
			parameter.append(1);
		}

		return parameter.toString();
	}

	//hottrack
	public static String YoutubeHotTrack(String category, String pageToken){
		StringBuilder parameter = new StringBuilder();

		parameter.append("videoCategory=");
		parameter.append(DigitUtils.urlParameter(category));
		parameter.append("&pageToken=");
		parameter.append(pageToken);

		return parameter.toString();
	}
	
	public static String SoundCloudHotTrack(String category, String index){
		StringBuilder parameter = new StringBuilder();

		parameter.append("category=");
		parameter.append(DigitUtils.urlParameter(category));
		parameter.append("&index=");
	
		if(index.length() > 0){
			parameter.append(index);
		} else {
			parameter.append(1);
		}

		return parameter.toString();
	}
}
