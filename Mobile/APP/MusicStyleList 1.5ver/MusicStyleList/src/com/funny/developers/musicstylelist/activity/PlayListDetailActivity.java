package com.funny.developers.musicstylelist.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.widget.TextView;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.baseactivity.BaseActivity;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.SearchTrackListView;

public class PlayListDetailActivity extends BaseActivity{

	SearchTrackListView playListDetailView = null;
	SearchListAdapter playListDetailAdapter = null;
	SearchListParser playListDetailParser = null;
	
	TextView emptyListView = null;
	
	private String requestId = null;
	private int requestType = 0;
	
	private void getExtras(){
		Intent intent = getIntent();
		requestId = intent.getStringExtra("id");
		requestType = intent.getIntExtra("type", 0);
	}
	
	@Override
	protected void settingView() {
		
		getExtras();
		
		emptyListView = (TextView)findViewById(R.id.empty_list_view);
		
		playListDetailView = (SearchTrackListView) findViewById(R.id.search_track_list_view);
		playListDetailView.setEmptyView(emptyListView);
		playListDetailAdapter = new SearchListAdapter(this);
		playListDetailParser = new SearchListParser();
		
		onRequestStart(this, this);
	}

	@Override
	protected int registerContentView() {
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
		
		return ParameterUrl.SearchPlayListDetail(requestId, 1);
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
				playListDetailAdapter.setList(playListDetailParser.getList());
				playListDetailView.setAdapter(playListDetailAdapter);
			}
		} else {
			
		}
	}
}
