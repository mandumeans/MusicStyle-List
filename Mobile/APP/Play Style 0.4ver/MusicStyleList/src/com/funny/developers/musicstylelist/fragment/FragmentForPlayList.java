package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.SearchPlayListAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchPlayListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.view.PullToRefreshView.OnPullToRefresh;
import com.funny.developers.musicstylelist.view.SearchPlayListView;

public class FragmentForPlayList extends BaseFragment implements OnPullToRefresh{

	private String requestQuery;
	private int requestType;
	
	private SearchPlayListView playListView;
	private SearchPlayListAdapter playListAdapter;
	private SearchPlayListParser listParser;
	
	private TextView emptyListView;
	
	public FragmentForPlayList(){
		super();
	}
	
	public FragmentForPlayList(Context context) {
		super(context);
	}
	
	@Override
	public void settingView(View v) {
		
		emptyListView = (TextView) v.findViewById(R.id.empty_list_view);
		
		playListView = (SearchPlayListView) v.findViewById(R.id.play_list_view);
		playListView.setEmptyView(emptyListView);
		playListView.setRefreshCallback(this);
		
		playListAdapter = new SearchPlayListAdapter(getActivity());
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
		
		playListAdapter.reset();
		
		listParser.setPageToken("");
		
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
		if(requestType == Define.YOUTUBE_SEARCH){
			return ParameterUrl.SearchYoutubeTrack(requestQuery, listParser.getPageToken());
			
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return ParameterUrl.SearchSoundCloudTrack(requestQuery, listParser.getPageToken());
		}
		
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		if(requestType == Define.YOUTUBE_SEARCH){
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
				if(playListView.getAdapter() == null){
					playListAdapter.setList(listParser.getList());
					playListView.setAdapter(playListAdapter);
				} else {
					playListAdapter.notifyDataSetChanged();
				}
			}
		}
		
		playListView.alertRefresh(false);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		playListView.alertRefresh(true);
		onRequestStart(getActivity(), this, false);
	}
}
