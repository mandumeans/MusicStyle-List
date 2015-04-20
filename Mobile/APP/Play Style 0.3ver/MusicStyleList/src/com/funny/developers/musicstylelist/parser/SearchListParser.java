package com.funny.developers.musicstylelist.parser;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.funny.developers.musicstylelist.model.SearchTrackListModel;

public class SearchListParser extends BaseJsonParser{

	private SearchTrackListModel listModel = null;
	
	@Override
	public void parseJsonListData(JSONArray array) {
		
		if(array == null){
			return;
		}
		
		try {
			for(int i = 0; i < array.length(); i++){
				listModel = new SearchTrackListModel();
				
				listModel.id = array.getJSONObject(i).optString("id");
				listModel.thumbnail = array.getJSONObject(i).optString("thumbnail");
				listModel.title = array.getJSONObject(i).optString("title");
				listModel.uploader = array.getJSONObject(i).optString("uploader");
				listModel.duration = array.getJSONObject(i).optString("duration");
				listModel.trackType = array.getJSONObject(i).getInt("trackType");
				
				mList.add(listModel);
			}
		} catch (Exception e) {
			Log.e("Error", e.toString());
		}
	}

	@Override
	public void parseJsonData() {
		
		if(mJsonObject == null){
			return;
		}

		try {
			pageToken = mJsonObject.getString("nextToken");
			
			parseJsonListData(mJsonObject.getJSONArray("list"));
		} catch (JSONException e) {
			Log.e("Error", e.getMessage());
		}
	}

}
