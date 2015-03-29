package com.funny.developers.musicstylelist.model;

import java.util.HashMap;
import java.util.Map;

public class YoutubeVideoStream {
	protected String mUrl;

	public YoutubeVideoStream(String pStreamStr)
	{
		String[] lArgs = pStreamStr.split("&");
		Map<String, String> lArgMap = new HashMap();
		for (int i = 0; i < lArgs.length; i++)
		{
			String[] lArgValStrArr = lArgs[i].split("=");
			if ((lArgValStrArr != null) && 
					(lArgValStrArr.length >= 2)) {
				lArgMap.put(lArgValStrArr[0], lArgValStrArr[1]);
			}
		}
		this.mUrl = ((String)lArgMap.get("url"));
	}

	public String getUrl()
	{
		return this.mUrl;
	}
}
