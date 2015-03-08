package com.funny.developers.musicstylelist.parameterurl;

import com.funny.developers.musicstylelist.util.DigitUtils;


public class ParameterUrl {
	
	public static String SearchYoutubeTrack(String query, int index){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(DigitUtils.urlParameter(query));
		parameter.append("&index=");
		parameter.append(index);
		
		return parameter.toString();
	}
	
	public static String SearchSoundCloudTrack(String query, int index){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(DigitUtils.urlParameter(query));
		parameter.append("&index=");
		parameter.append(index);
		
		return parameter.toString();
	}
	
	//playlist detail search
	public static String SearchPlayListDetail(String id, int index){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("id=");
		parameter.append(id);
		parameter.append("&index=");
		parameter.append(index);
		
		return parameter.toString();
	}
}
