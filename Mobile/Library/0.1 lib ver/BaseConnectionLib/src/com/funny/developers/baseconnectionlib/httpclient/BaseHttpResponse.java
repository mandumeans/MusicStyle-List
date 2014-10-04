package com.funny.developers.baseconnectionlib.httpclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.util.Log;

public class BaseHttpResponse {
	
	private static BaseHttpResponse baseHttpResponseInstance = null;
	
	private BaseHttpResponse(){}
	
	public static BaseHttpResponse getInstance(){
		
		if(baseHttpResponseInstance == null){
			baseHttpResponseInstance = new BaseHttpResponse();
			return baseHttpResponseInstance; 
		} else if(baseHttpResponseInstance != null){
			return baseHttpResponseInstance;
		}
		
		return null;
	}
	
	public String convertResponseToString(HttpResponse response) {
		try	{
			
			String line = null;
			StringBuilder responseString = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			while((line = br.readLine()) != null) {
				responseString.append(line);
			}
			br.close();
			return responseString.toString();
			
		} catch (Exception e)
		{
			Log.e("Error", "convert error");
		}

		return null;
	}

	public JSONObject convertStringToJSONObject(String response) {
		try {
			
			JSONObject jsonObject = new JSONObject(response);
			return jsonObject;
			
		} catch (Exception e) {
			Log.e("Error", "convert error");
		}

		return null;
	}
	
	public Document convertStringToXml(String response) {
		try{
			
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			InputSource  inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(response));
			Document doc = docBuild.parse(inputSource);
			return doc;
			
		} catch (Exception e) {
			
		}
		
		return null;
	}

}
