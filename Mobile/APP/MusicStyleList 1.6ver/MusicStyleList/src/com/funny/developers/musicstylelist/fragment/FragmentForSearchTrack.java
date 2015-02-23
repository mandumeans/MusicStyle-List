package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.PullToRefreshView.onPullToRefresh;
import com.funny.developers.musicstylelist.view.SearchTrackListView;

public class FragmentForSearchTrack extends BaseFragment implements onPullToRefresh{

	private String requestQuery = null;
	private int requestType = Define.TOTAL_SEARCH;
	
	SearchTrackListView searchTrackListView = null;
	SearchListAdapter searchListAdapter = null;
	SearchListParser listParser = null;
	
	TextView emptyListView = null;
	
	public FragmentForSearchTrack(){
		super();
	}
	
	public FragmentForSearchTrack(Context context) {
		super(context);
	}
	
	@Override
	public void settingView() {
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);
		
		searchTrackListView = (SearchTrackListView)getView().findViewById(R.id.search_track_list_view);
		searchTrackListView.setEmptyView(emptyListView);
		searchTrackListView.setRefreshCallback(this);
		
		searchListAdapter = new SearchListAdapter(getActivity());
		listParser = new SearchListParser();
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_search_track;
	}

	@Override
	public void setSearchQueryType(String query, int type) {
		requestQuery = query;
		requestType = type;
		indexOrganizer(Define.INDEX_INITIALIZE_COMMEND);
		
		searchListAdapter.reset();
		
		onRequestStart(getActivity(), this, true);
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
			return ParameterUrl.SearchYoutubeTrack(requestQuery, search_index);
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return ParameterUrl.SearchSoundCloudTrack(requestQuery, search_index);
		}
		
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		
		if(requestType == Define.TOTAL_SEARCH){
			return null;
			
		} else if(requestType == Define.YOUTUBE_SEARCH){
			return RequestUrl.SearchYoutubeTrackUrl();
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
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
				if(searchTrackListView.getAdapter() == null){
					searchListAdapter.setList(listParser.getList());
					searchTrackListView.setAdapter(searchListAdapter);
				} else {
					searchListAdapter.notifyDataSetChanged();
				}
			}
		}
		
		searchTrackListView.alertRefresh(false);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		searchTrackListView.alertRefresh(true);
		indexOrganizer(Define.INDEX_PLUS_COMMEND);
		onRequestStart(getActivity(), this, false);
	}

}
