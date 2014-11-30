package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.util.Log;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.baseactivity.R;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.baseparser.SearchListParser;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.SearchTrackListView;

public class FragmentForSearchTrack extends BaseFragment{

	//connection variable
	private final int TOTAL_SEARCH = 0;
	private final int YOUTUBE_SEARCH = 1;
	private final int SOUND_CLOUD_SEARCH = 2;
	
	private String requestQuery = null;
	private int requestType = 0;
	
	SearchTrackListView searchTrackListView = null;
	SearchListAdapter searchListAdapter = null;
	SearchListParser listParser = null;
	
	@Override
	public void settingView() {
		
		searchTrackListView = (SearchTrackListView)getView().findViewById(R.id.search_track_list_view);
		searchListAdapter = new SearchListAdapter(getActivity());
		listParser = new SearchListParser();
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_search_track;
	}

	@Override
	public void setSearchQueryType(String query, int type) {
		Log.d("Error", query + " " + type);
		
		requestQuery = query;
		requestType = type;
		
		onRequestStart(getActivity(), this);
	}

	@Override
	public int getRequestType() {
		return BaseConsts.REQUEST_TYPE_GET;
	}

	@Override
	public int getResultType() {
		return BaseConsts.RESULT_TYPE_JSON;
	}

	@Override
	public String onRequestGetParameter() {
		
		Log.d("Error", requestQuery + " is requestQuery");
		
		if(requestType == TOTAL_SEARCH){
			return ParameterUrl.SearchTotalTrack(requestQuery);
			
		} else if(requestType == YOUTUBE_SEARCH){
			return ParameterUrl.SearchYoutubeTrack(requestQuery, 1);
			
		} else if(requestType == SOUND_CLOUD_SEARCH){
			return ParameterUrl.SearchSoundCloudTrack(requestQuery, 0);
		}
		
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		
		if(requestType == TOTAL_SEARCH){
			return null;
			
		} else if(requestType == YOUTUBE_SEARCH){
			return RequestUrl.SearchYoutubeTrackUrl();
			
		} else if(requestType == SOUND_CLOUD_SEARCH){
			return RequestUrl.SearchSoundCloudTrackUrl();
		}
		
		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			
			listParser.setJsonObject((JSONObject)result);
			listParser.parseJsonData();
			
			if(!listParser.getList().isEmpty()){
				
				Log.d("Error", "is not empty");
				
				searchListAdapter.setList(listParser.getList());
				searchTrackListView.setAdapter(searchListAdapter);
			}
		} else {
			
		}
	}

}
