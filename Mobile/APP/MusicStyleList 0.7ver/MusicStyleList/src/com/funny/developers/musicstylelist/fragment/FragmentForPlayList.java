package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.widget.TextView;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.adapter.PlayListAdapter;
import com.funny.developers.musicstylelist.baseactivity.R;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchPlayListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.SearchPlayListView;

public class FragmentForPlayList extends BaseFragment{

	private String requestQuery = null;
	private int requestType = 0;
	
	SearchPlayListView playListView = null;
	PlayListAdapter playListAdapter = null;
	SearchPlayListParser listParser = null;
	
	TextView emptyListView = null;
	
	@Override
	public void settingView() {
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);
		
		playListView = (SearchPlayListView) getView().findViewById(R.id.play_list_view);
		playListView.setEmptyView(emptyListView);
		playListAdapter = new PlayListAdapter(getActivity());
		listParser = new SearchPlayListParser();
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_play_list;
	}

	@Override
	public void setSearchQueryType(String query, int type) {
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
		
		if(requestType == Define.TOTAL_SEARCH){
			return ParameterUrl.SearchTotalTrack(requestQuery);
			
		} else if(requestType == Define.YOUTUBE_SEARCH){
			return ParameterUrl.SearchYoutubeTrack(requestQuery, 1);
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return ParameterUrl.SearchSoundCloudTrack(requestQuery, 0);
		}
		
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		
		if(requestType == Define.TOTAL_SEARCH){
			return null;
			
		} else if(requestType == Define.YOUTUBE_SEARCH){
			return RequestUrl.SearchYoutubePlayListUrl();
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return RequestUrl.SearchSoundCloudPlayListUrl();
		}
		
		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			listParser.setJsonObject((JSONObject)result);
			listParser.parseJsonData();
			
			if(!listParser.getList().isEmpty()){
				playListAdapter.setList(listParser.getList()); // µ•¿Ã≈Õ ª¿‘
				playListView.setAdapter(playListAdapter);
			}
		} else {
			
		}
		
	}
}
