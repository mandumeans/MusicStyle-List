package com.funny.developers.musicstylelist.asynctask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.YoutubeVideoFormat;
import com.funny.developers.musicstylelist.model.YoutubeVideoStream;

public class GetYoutubeVideoUrl extends AsyncTask<String, Void, String>{

	private final String HTTP_PARAMS_CHARSET = "UTF-8";
	private final boolean HTTP_PARAMS_CONTUNUE = false;
	private final int HTTP_PARAMS_TIMEOUT = 30000;
	
	private String connectType;

	public interface OnYoutubeRTSPRequestCallBack{
		public void getYoutubeUrl(String youtubeUrl, String connectType);
	}

	OnYoutubeRTSPRequestCallBack mYoutubeRTSPRequestCallBack = null;

	public void setOnRequestCallBack(OnYoutubeRTSPRequestCallBack mYoutubeRTSPRequestCallBack){
		this.mYoutubeRTSPRequestCallBack = mYoutubeRTSPRequestCallBack;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... url) {
		try {
			connectType = url[2];
			
			if(url[2].equalsIgnoreCase(Define.YOUTUBE_NOT_RTSP_URL)){
				return calculateYouTubeUrl(url[1], false, url[0]);
			} else {
				return calculateYouTubeUrlRTPS(url[0]);
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String transformedUrl) {
		
		if(!isCancelled()){
			mYoutubeRTSPRequestCallBack.getYoutubeUrl(transformedUrl, connectType);
		}
	}

	public String calculateYouTubeUrl(String pYouTubeFmtQuality, boolean pFallback,
			String pYouTubeVideoId) throws IOException,
			ClientProtocolException, UnsupportedEncodingException {

		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP_PARAMS_CHARSET);
		HttpProtocolParams.setUseExpectContinue(httpParams, HTTP_PARAMS_CONTUNUE);
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_PARAMS_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_PARAMS_TIMEOUT);

		String lUriStr = null;
		HttpClient lClient = new DefaultHttpClient();

		HttpGet lGetMethod = new HttpGet(Define.YOUTUBE_VIDEO_INFORMATION_URL + pYouTubeVideoId);
		lGetMethod.setParams(httpParams);
		
		HttpResponse lResp = null;
		lResp = lClient.execute(lGetMethod);

		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		String lInfoStr = null;

		lResp.getEntity().writeTo(lBOS);
		lInfoStr = new String(lBOS.toString("UTF-8"));

		String[] lArgs=lInfoStr.split("&");
		Map<String,String> lArgMap = new HashMap<String, String>();
		for(int i=0; i<lArgs.length; i++){
			String[] lArgValStrArr = lArgs[i].split("=");
			if(lArgValStrArr != null){
				if(lArgValStrArr.length >= 2){
					lArgMap.put(lArgValStrArr[0], URLDecoder.decode(lArgValStrArr[1]));
				}
			}
		}

		//Find out the URI string from the parameters

		//Populate the list of formats for the video
		String lFmtList = URLDecoder.decode(lArgMap.get("fmt_list"));
		ArrayList<YoutubeVideoFormat> lFormats = new ArrayList<YoutubeVideoFormat>();
		if(null != lFmtList){
			String lFormatStrs[] = lFmtList.split(",");

			for(String lFormatStr : lFormatStrs){
				YoutubeVideoFormat lFormat = new YoutubeVideoFormat(lFormatStr);
				lFormats.add(lFormat);
			}
		}

		//Populate the list of streams for the video
		String lStreamList = lArgMap.get("url_encoded_fmt_stream_map");
		if(null != lStreamList){
			String lStreamStrs[] = lStreamList.split(",");
			ArrayList<YoutubeVideoStream> lStreams = new ArrayList<YoutubeVideoStream>();
			for(String lStreamStr : lStreamStrs){
				YoutubeVideoStream lStream = new YoutubeVideoStream(lStreamStr);
				lStreams.add(lStream);
			}	

			//Search for the given format in the list of video formats
			// if it is there, select the corresponding stream
			// otherwise if fallback is requested, check for next lower format
			int lFormatId = Integer.parseInt(pYouTubeFmtQuality);

			YoutubeVideoFormat lSearchFormat = new YoutubeVideoFormat(lFormatId);
			while(!lFormats.contains(lSearchFormat) && pFallback ){
				int lOldId = lSearchFormat.getId();
				int lNewId = getSupportedFallbackId(lOldId);

				if(lOldId == lNewId){
					break;
				}
				lSearchFormat = new YoutubeVideoFormat(lNewId);
			}

			int lIndex = lFormats.indexOf(lSearchFormat);
			if(lIndex >= 0){
				YoutubeVideoStream lSearchStream = lStreams.get(lIndex);
				lUriStr = lSearchStream.getUrl();
			}

		}		
		//Return the URI string. It may be null if the format (or a fallback format if enabled)
		// is not found in the list of formats for the video
		return lUriStr;
	}

	public static int getSupportedFallbackId(int pOldId){
		final int lSupportedFormatIds[] = {13,  //3GPP (MPEG-4 encoded) Low quality 
				17,  //3GPP (MPEG-4 encoded) Medium quality 
				18,  //MP4  (H.264 encoded) Normal quality
				22,  //MP4  (H.264 encoded) High quality
				37   //MP4  (H.264 encoded) High quality
		};
		int lFallbackId = pOldId;
		for(int i = lSupportedFormatIds.length - 1; i >= 0; i--){
			if(pOldId == lSupportedFormatIds[i] && i > 0){
				lFallbackId = lSupportedFormatIds[i-1];
			}			
		}
		return lFallbackId;
	}

	public String calculateYouTubeUrlRTPS(String pYouTubeVideoId) throws IOException {

		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP_PARAMS_CHARSET);
		HttpProtocolParams.setUseExpectContinue(httpParams, HTTP_PARAMS_CONTUNUE);
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_PARAMS_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_PARAMS_TIMEOUT);

		HttpClient lClient = new DefaultHttpClient();

		HttpGet lGetMethod = new HttpGet(Define.YOUTUBE_VIDEO_INFORMATION_RTSP_URL + pYouTubeVideoId + Define.YOUTUBE_VIDEO_INFORMATION_RTSP_URL_JSON);
		lGetMethod.setParams(httpParams);

		HttpResponse lResp = null;
		lResp = lClient.execute(lGetMethod);
		
		ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
		lResp.getEntity().writeTo(lBOS);
		String response = new String(lBOS.toString("UTF-8"));
		
		String youtubeVideoRSPTRUL = null;

		try {
			
			JSONObject jsonObject = new JSONObject(response);
			JSONObject entryjsonObject = jsonObject.getJSONObject("entry");
			JSONObject mediaGroupjsonObject = entryjsonObject.getJSONObject("media$group");
			JSONObject mediaContentjsonObject = (JSONObject) mediaGroupjsonObject.getJSONArray("media$content").get(0);
			
			youtubeVideoRSPTRUL = mediaContentjsonObject.getString("url");

		} catch(Exception e){
			e.printStackTrace();
		} finally{
		}
		
		if(youtubeVideoRSPTRUL == null){
			youtubeVideoRSPTRUL = Define.YOUTUBE_URL_NOT_FIND;
		}

		return youtubeVideoRSPTRUL;
	}

}
