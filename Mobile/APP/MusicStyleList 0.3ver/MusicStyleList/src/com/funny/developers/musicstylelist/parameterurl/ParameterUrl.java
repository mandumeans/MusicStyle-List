package com.funny.developers.musicstylelist.parameterurl;

public class ParameterUrl {
	
	public static String SearchYoutubeTrack(String query){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		
		return parameter.toString();
	}
	
	public static String SearchSoundCloudTrack(String query){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		
		return parameter.toString();
	}
	
	public static String SearchTotalTrack(String query){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		
		return parameter.toString();
	}

}
