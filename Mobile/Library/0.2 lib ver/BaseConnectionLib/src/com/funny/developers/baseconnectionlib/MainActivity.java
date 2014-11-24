package com.funny.developers.baseconnectionlib;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask;
import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask.OnRequestInterFace;
import com.funny.developers.baseconnectionlib.consts.BaseConsts;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.os.Bundle;

public class MainActivity extends FragmentActivity implements OnRequestInterFace{

	private BaseAsyncTask connectVender;
	
	private String getJsonUrl = "http://api.soundcloud.com/playlists/405726.json";
	private String getXmlUrl = "http://api.soundcloud.com/playlists/405726";
	private String getParameter = "client_id=a2b4d87e3bac428d8467d6ea343d49ae";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		connectVender = new BaseAsyncTask(this, this);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		connectVender.execute();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	public int getRequestType() {
		return BaseConsts.REQUEST_TYPE_GET;
	}

	@Override
	public int getResultType() {
		return BaseConsts.RESULT_TYPE_XML;
	}

	@Override
	public String onRequestGetUrl() {
		return getXmlUrl;
	}

	@Override
	public String onRequestGetParameter() {
		// TODO Auto-generated method stub
		return getParameter;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			JSONObject resultJson = (JSONObject) result;
			
			Log.d("resultJson", resultJson.toString());
		} else {
			
			Document resultXml = (Document)result;
			resultXml.getDocumentElement().normalize();
			
			Log.d("resultXml", resultXml.getDocumentElement().getNodeName());
			
			NodeList nList = resultXml.getElementsByTagName(resultXml.getDocumentElement().getNodeName());
			
			for (int i = 0; i < nList.getLength(); i++) {
				
				Node nNode = nList.item(i);
				
				Log.d("resultXml", nNode.getNodeName());
			}
		}
	}

	@Override
	public void onRequestError(int errorType) {
		onRequestCancle();
	}

	@Override
	public void onRequestCancle() {
		connectVender.cancel(true);
		connectVender = null;
	}	
}
