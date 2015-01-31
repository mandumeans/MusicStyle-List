package com.funny.developers.musicstylelist.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.BaseModel;

public class FragmentForUserPlayListDetail extends BaseFragment{

	private String requestQuery = null;
	private int requestType = Define.TOTAL_SEARCH;
	
	ListView userTrackListView = null;
	SearchListAdapter userTrackListAdapter = null;
	
	ArrayList<BaseModel> trackList = null;
	
	TextView emptyListView = null;
	
	public FragmentForUserPlayListDetail(){
		super();
	}
	
	public FragmentForUserPlayListDetail(Context context) {
		super(context);
	}
	
	@Override
	public void settingView() {
		
		Log.d("Error", "FragmentForUserPlayListDetail settingView()");
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);
		
		userTrackListView = (ListView)getView().findViewById(R.id.play_track_list_view);
		userTrackListView.setEmptyView(emptyListView);
		
		setList();
	}
	
	public void setTrackList(ArrayList<BaseModel> trackList){
		
		Log.d("Error", "FragmentForUserPlayListDetail setTrackList()");
		this.trackList = trackList;
	}
	
	public void setList() {
		if(trackList != null) {
			if(userTrackListView.getAdapter() == null){
				userTrackListAdapter = new SearchListAdapter(getActivity());
				//userTrackListAdapter.setOnAdapterButtonClickListener(this);
				userTrackListAdapter.setList(trackList);
				userTrackListView.setAdapter(userTrackListAdapter);
			}
			else
			{
				userTrackListAdapter.setList(trackList);
			}
			
			userTrackListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_user_play_track_list;
	}

	@Override
	public void setSearchQueryType(String query, int type) {
		requestQuery = query;
		requestType = type;
		userTrackListAdapter.reset();
	}

	@Override
	public int getRequestType() {
		return 0;
	}

	@Override
	public int getResultType() {
		return 0;
	}

	@Override
	public String onRequestGetParameter() {
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {}
}
