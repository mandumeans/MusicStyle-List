package com.funny.developers.musicstylelist.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.funny.developers.musicstylelist.model.BaseModel;

public abstract class BaseJsonParser {

	protected BaseModel mModel;
	protected String pageToken = "";
	protected ArrayList<BaseModel> mList;
	protected JSONObject mJsonObject;
	
	public abstract void parseJsonListData(JSONArray array);
	public abstract void parseJsonData();
	
	public BaseJsonParser(){
		mList = new ArrayList<BaseModel>();
	}
	
	public void setJsonObject(JSONObject jsonObject){
		mJsonObject = jsonObject;
	}
	
	public BaseModel getModel(){
		return mModel;
	}
	
	public ArrayList<BaseModel> getList(){
		return mList;
	}
	
	public String getPageToken(){
		return pageToken;
	}
	
	public void setPageToken(String token){
		pageToken = token;
	}
}
