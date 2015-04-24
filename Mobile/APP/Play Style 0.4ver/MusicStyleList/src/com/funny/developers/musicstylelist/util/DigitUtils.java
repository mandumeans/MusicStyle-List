package com.funny.developers.musicstylelist.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Formatter;

import com.funny.developers.musicstylelist.definition.Define;

public class DigitUtils {

	private static StringBuilder mFormatBuilder;
	private static Formatter mFormatter;

	public static String stringForTime(String time, int trackType) {
		mFormatter = new Formatter();
		mFormatBuilder = new StringBuilder();
		mFormatBuilder.setLength(0);
		
		if(trackType == Define.YOUTUBE_TRACK) {
			
			String tempTime = null;
			
			try{
				tempTime = time.split("PT")[1];
			} catch(Exception e) {
				return mFormatter.format("%02d:%02d", 0, 0).toString();
			}

			String hours = "0";
			String minutes = "0";
			String seconds = "0";

			if(tempTime.contains("H")){
				hours  = tempTime.split("H")[0];
				try{
					tempTime = tempTime.split("H")[1];
				} catch(Exception e) {
				}
			}

			if(tempTime.contains("M")){
				minutes  = tempTime.split("M")[0];
				try{
					tempTime = tempTime.split("M")[1];
				} catch(Exception e) {
				}
			}

			if(tempTime.contains("S")){
				seconds  = tempTime.split("S")[0];
			}

			int iseconds = Integer.parseInt(seconds);
			int iminutes = Integer.parseInt(minutes);
			int ihours   = Integer.parseInt(hours);

			if (ihours > 0) {
				return mFormatter.format("%d:%02d:%02d", ihours, iminutes, iseconds).toString();
			} else {
				return mFormatter.format("%02d:%02d", iminutes, iseconds).toString();
			}

		} else {
			int tempTime = 0;
			int timeS = Integer.parseInt(time);
			tempTime = timeS / 1000;

			int seconds = tempTime % 60;
			int minutes = (tempTime / 60) % 60;
			int hours   = tempTime / 3600;

			if (hours > 0) {
				return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
			} else {
				return mFormatter.format("%02d:%02d", minutes, seconds).toString();
			}
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