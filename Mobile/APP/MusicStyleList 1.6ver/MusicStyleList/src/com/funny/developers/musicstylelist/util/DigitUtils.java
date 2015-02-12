package com.funny.developers.musicstylelist.util;

import java.text.DecimalFormat;
import java.util.Formatter;

import com.funny.developers.musicstylelist.definition.Define;

public class DigitUtils {
	
	private static DecimalFormat format = null;
	private static StringBuilder mFormatBuilder = null;
	private static Formatter mFormatter = null;
	
	public static String makeNumCommaString(int num) {
		format = new DecimalFormat("###,###");
        return format.format(num);    
    }
	
	public static String makeDate(String date){
		String tempDate = date.split(" ")[0];
		return tempDate.replace("/", "-");
	}
	
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
}
