package com.funny.developers.musicstylelist.asynctask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.os.AsyncTask;

public class CheckYoutubeVideoUrl extends AsyncTask<String, Integer, Long>{

	private final String HTTP_PARAMS_CHARSET = "UTF-8";
	private final boolean HTTP_PARAMS_CONTUNUE = false;
	private final int HTTP_PARAMS_TIMEOUT = 30000;

	private String youtubeUrl;

	public interface OnCheckYoutubeUrlRequestCallback{
		public void checkYoutubeUrl(String youtubeUrl, boolean check);
	}

	private OnCheckYoutubeUrlRequestCallback mCallback;

	public void setOnRequestCallBack(OnCheckYoutubeUrlRequestCallback callback){
		mCallback = callback;
	}

	@Override
	protected Long doInBackground(String... url) {
		youtubeUrl = url[0];

		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP_PARAMS_CHARSET);
		HttpProtocolParams.setUseExpectContinue(httpParams, HTTP_PARAMS_CONTUNUE);
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_PARAMS_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_PARAMS_TIMEOUT);

		HttpClient lClient = new DefaultHttpClient();

		HttpGet lGetMethod = new HttpGet(url[0]);
		lGetMethod.setParams(httpParams);

		try{
			HttpResponse lResp = null;
			lResp = lClient.execute(lGetMethod);

			return lResp.getEntity().getContentLength();

		} catch(Exception e) {
			e.printStackTrace();
		}

		return 0L;
	}

	@Override
	protected void onPostExecute(Long checkData) {
		
		if(!isCancelled()){
			if(checkData > 0){
				mCallback.checkYoutubeUrl(youtubeUrl, true);
			} else {
				mCallback.checkYoutubeUrl(youtubeUrl, false);
			}
		}
	}

}
