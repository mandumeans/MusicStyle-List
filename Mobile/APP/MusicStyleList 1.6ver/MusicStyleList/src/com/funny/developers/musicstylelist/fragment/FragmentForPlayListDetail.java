package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.PullToRefreshView.onPullToRefresh;
import com.funny.developers.musicstylelist.view.SearchTrackListView;

public class FragmentForPlayListDetail extends BaseFragment implements onPullToRefresh{
	
	SearchTrackListView playListDetailView = null;
	SearchListAdapter playListDetailAdapter = null;
	SearchListParser playListDetailParser = null;
	
	TextView emptyListView = null;
	
	private String requestId = null;
	private int requestType = Define.TOTAL_SEARCH;
	
	public FragmentForPlayListDetail(){
		super();
	}
	
	public FragmentForPlayListDetail(Context context) {
		super(context);
	}
	
	public void setInfoId(String requestId, int requestType){
		this.requestId = requestId;
		this.requestType = requestType;
		
		indexOrganizer(Define.INDEX_INITIALIZE_COMMEND);
		
		onRequestStart(mContext, this, true);
	}
	
	@Override
	public void settingView() {
		
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);
		
		playListDetailView = (SearchTrackListView) getView().findViewById(R.id.search_track_list_view);
		playListDetailView.setEmptyView(emptyListView);
		playListDetailView.setRefreshCallback(this);
		
		playListDetailAdapter = new SearchListAdapter(mContext);
		playListDetailParser = new SearchListParser();
	}

	@Override
	public int registerContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_for_play_list_detail;
	}

	@Override
	public int getRequestType() {
		// TODO Auto-generated method stub
		return BaseConsts.REQUEST_TYPE_GET;
	}

	@Override
	public int getResultType() {
		// TODO Auto-generated method stub
		return BaseConsts.RESULT_TYPE_JSON;
	}

	@Override
	public String onRequestGetParameter() {
		
		return ParameterUrl.SearchPlayListDetail(requestId, search_index);
	}

	@Override
	public String onRequestGetUrl() {
		
		if(requestType == Define.YOUTUBE_SEARCH){
			return RequestUrl.SearchYoutubePlayListDetailUrl();
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return RequestUrl.SearchSoundCloudPlayListDetailUrl();
		}
		
		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			playListDetailParser.setJsonObject((JSONObject)result);
			playListDetailParser.parseJsonData();
			
			if(!playListDetailParser.getList().isEmpty()){
				
				if(playListDetailView.getAdapter() == null){
					playListDetailAdapter.setList(playListDetailParser.getList());
					playListDetailView.setAdapter(playListDetailAdapter);
				} else {
					playListDetailAdapter.notifyDataSetChanged();
				}
			}
		}
		
		playListDetailView.alertRefresh(false);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void setSearchQueryType(String query, int type) {}

	@Override
	public void onRefresh() {
		playListDetailView.alertRefresh(true);
		indexOrganizer(Define.INDEX_PLUS_COMMEND);
		onRequestStart(mContext, this, false);
	}

}
