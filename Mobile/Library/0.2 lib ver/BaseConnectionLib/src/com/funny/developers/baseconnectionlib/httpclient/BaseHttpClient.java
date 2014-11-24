package com.funny.developers.baseconnectionlib.httpclient;

import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class BaseHttpClient {
	
	static private BaseHttpClient instanceClass = null;
	private final String HTTP_PARAMS_CHARSET = "UTF-8";
	private final boolean HTTP_PARAMS_CONTUNUE = false;
	private final int HTTP_PARAMS_TIMEOUT = 15000;
	
	private BaseHttpClient(){}
	
	static public BaseHttpClient getInstance(){
		
		if(instanceClass == null){
			instanceClass = new BaseHttpClient();
			return instanceClass;
		} else if(instanceClass != null){	
			return instanceClass;
		}
		
		return null;
	}

	public HttpResponse doGet(String url, String params){
		try
		{
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP_PARAMS_CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, HTTP_PARAMS_CONTUNUE);
			HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_PARAMS_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, HTTP_PARAMS_TIMEOUT);

			if(params != null)
			{
				url = url + "?" + params;
			}
			
			HttpGet getMethod = new HttpGet(url);
			getMethod.setParams(httpParams);

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(getMethod);

			int status = response.getStatusLine().getStatusCode();

			if ( status != HttpStatus.SC_OK ){
				Log.e("Error", "HTTP GET Connection Error");
				return null;
			}
			
			return response;
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	public HttpResponse doPost(String url, String params){
		try
		{
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP_PARAMS_CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, HTTP_PARAMS_CONTUNUE);
			HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_PARAMS_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, HTTP_PARAMS_TIMEOUT);

			HttpPost postMethod = new HttpPost(url);
			postMethod.setParams(httpParams);
			DefaultHttpClient client = new DefaultHttpClient();

			if(params != null && params.length() > 0){
				ArrayList<NameValuePair> postArrayList = new ArrayList<NameValuePair>();
				getPostParameter(postArrayList, params);
				postMethod.setEntity(new UrlEncodedFormEntity(postArrayList));
			}

			HttpResponse response = client.execute(postMethod);
			int status = response.getStatusLine().getStatusCode();

			if ( status != HttpStatus.SC_OK ) {
				Log.e("Error", "HTTP POST Connection Error");
				return null;
			}

			return response;
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	private void getPostParameter(ArrayList<NameValuePair> postArrayList, String parameter){

		String[] textKeyValue;
		
		try{
			textKeyValue = parameter.split("&");

			for(int i = 0; i < textKeyValue.length; i++){
				String[] keyValue = textKeyValue[i].split("=");
				
				if(keyValue == null || keyValue.length <= 1)
					continue;
				
				postArrayList.add(new BasicNameValuePair(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8")));
			}
		} catch (Exception e){
			Log.d("Error", e.toString());
		}
	}
	
	public boolean checkConnectivity(Context context){
		
		ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info;
		
		// 와이파이 설정 확인
		info = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(info.isAvailable()){
			return true;
		} else {
			info = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if(info.isAvailable()){
				return true;
			}
		}
		
		return false;
	}
}
