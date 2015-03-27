package com.funny.developers.musicstylelist.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;

import com.funny.developers.musicstylelist.definition.Define;

public class DigitUtils {
	
	private static StringBuilder mFormatBuilder;
	private static Formatter mFormatter;
	
	public static String stringForTime(int timeMs, int trackType) {
		mFormatter = new Formatter();
		mFormatBuilder = new StringBuilder();
		
		int tempTime = 0;
		
		if(trackType == Define.YOUTUBE_TRACK){
			tempTime = timeMs;
		} else {
			tempTime = timeMs / 1000;
		}
		
		int seconds = tempTime % 60;
		int minutes = (tempTime / 60) % 60;
		int hours   = tempTime / 3600;
		
		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}
	
	public static String changeImageUrl(String imageUrl, String oldDimension, String newDimension){
		String tempImageUrl;
		tempImageUrl = imageUrl.replace(oldDimension, newDimension);
		return tempImageUrl;
	}
	
	public static String urlParameter(String parameter){
		try {
			return URLEncoder.encode(parameter ,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}