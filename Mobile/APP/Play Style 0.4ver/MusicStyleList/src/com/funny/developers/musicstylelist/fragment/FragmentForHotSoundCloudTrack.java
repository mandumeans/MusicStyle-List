package com.funny.developers.musicstylelist.fragment;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.HotTrackListAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchListParser;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.util.SettingUtils;
import com.funny.developers.musicstylelist.view.HotTrackListView;
import com.funny.developers.musicstylelist.view.PullToRefreshView.OnPullToRefresh;

public class FragmentForHotSoundCloudTrack extends BaseFragment implements OnPullToRefresh{

	private HotTrackListView hotTrackListView;
	private HotTrackListAdapter hotTrackListAdapter;
	private SearchListParser listParser;
	
	public FragmentForHotSoundCloudTrack(){
		super();
	}
	
	public FragmentForHotSoundCloudTrack(Context context){
		super(context);
	}
	
	@Override
	public void settingView(View v) {
		hotTrackListView = (HotTrackListView)v.findViewById(R.id.hot_track_list_view);
		hotTrackListView.setRefreshCallback(this);
		
		hotTrackListAdapter = new HotTrackListAdapter(getActivity());
		listParser = new SearchListParser();
		
		onRequestStart(getActivity(), this, true);
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_hot_track;
	}
	
	@Override
	public void setSearchQueryType(String query, int type) {}

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
		return ParameterUrl.SoundCloudHotTrack(SettingUtils.getSoundCloudFavoriteType(getActivity()), listParser.getPageToken());
	}

	@Override
	public String onRequestGetUrl() {
		return RequestUrl.HotSoundCloudTrackSearchUrl();
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			listParser.setJsonObject((JSONObject)result);
			listParser.parseJsonData();
			
			if(!listParser.getList().isEmpty()){
				if(hotTrackListView.getAdapter() == null){
					hotTrackListAdapter.setList(listParser.getList());
					hotTrackListView.setAdapter(hotTrackListAdapter);
				} else {
					hotTrackListAdapter.notifyDataSetChanged();
				}
			}
		}
		
		hotTrackListView.alertRefresh(false);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		hotTrackListView.alertRefresh(true);
		onRequestStart(getActivity(), this, false);
	}
}
