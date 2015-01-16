package com.funny.developers.musicstylelist.asynctask;

import android.os.AsyncTask;

import com.keyes.youtube.YouTubeUtility;

public class GetYoutubeUrl extends AsyncTask<String, Void, String>{

	public interface onRequestCallBack{
		public void getYoutubeUrl(String youtubeUrl);
	}
	
	onRequestCallBack mOnRequestCallback = null;
	
	public void setOnRequestCallBack(onRequestCallBack mOnRequestCallback){
		this.mOnRequestCallback = mOnRequestCallback;
	}
	
	@Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }
	@Override
	protected String doInBackground(String... url) {

		try {
			return YouTubeUtility.calculateYouTubeUrl(url[1], false, url[0]);
		}  catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String transformedUrl) {
		mOnRequestCallback.getYoutubeUrl(transformedUrl);
	}

}
