package com.funny.developers.musicstylelist.parser;

import org.json.JSONArray;
import org.json.JSONException;

import com.funny.developers.musicstylelist.model.SearchTrackListModel;

import android.util.Log;

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
				listModel.uploaded = array.getJSONObject(i).optString("uploaded");
				listModel.viewCount = Integer.parseInt(array.getJSONObject(i).optString("viewCount"));
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
			parseJsonListData(mJsonObject.getJSONArray("list"));
		} catch (JSONException e) {
			Log.e("Error", e.getMessage());
		}
	}

}
