package com.funny.developers.musicstylelist.parameterurl;

public class ParameterUrl {
	
	public static String SearchYoutubeTrack(String query, int index){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		parameter.append("&index=");
		parameter.append(index);
		
		return parameter.toString();
	}
	
	public static String SearchSoundCloudTrack(String query, int index){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		parameter.append("&index=");
		parameter.append(index);
		
		return parameter.toString();
	}
	
	public static String SearchTotalTrack(String query){
		StringBuilder parameter = new StringBuilder();
		
		parameter.append("keyword=");
		parameter.append(query);
		
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
