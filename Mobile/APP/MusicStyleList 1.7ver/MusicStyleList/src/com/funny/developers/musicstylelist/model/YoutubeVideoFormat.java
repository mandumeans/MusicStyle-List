package com.funny.developers.musicstylelist.model;

public class YoutubeVideoFormat {

	protected int mId;

	public YoutubeVideoFormat(String pFormatString)
	{
		String[] lFormatVars = pFormatString.split("/");
		this.mId = Integer.parseInt(lFormatVars[0]);
	}

	public YoutubeVideoFormat(int pId)
	{
		this.mId = pId;
	}

	public int getId()
	{
		return this.mId;
	}
	
	public boolean equals(Object pObject)
	  {
	    if (!(pObject instanceof YoutubeVideoFormat)) {
	      return false;
	    }
	    return ((YoutubeVideoFormat)pObject).mId == this.mId;
	  }
}
