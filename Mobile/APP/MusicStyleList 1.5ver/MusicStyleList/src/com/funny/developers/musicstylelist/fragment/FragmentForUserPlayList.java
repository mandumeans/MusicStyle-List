package com.funny.developers.musicstylelist.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.UserPlayListAdapter;
import com.funny.developers.musicstylelist.adapter.UserPlayListAdapter.OnAdapterButtonClickListener;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.dao.ExtraDao;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.JoinedUserPlayListModel;
import com.funny.developers.musicstylelist.naviutil.NavigationUtils;

public class FragmentForUserPlayList extends BaseFragment implements OnItemClickListener, OnAdapterButtonClickListener {

	private String requestQuery = null;
	private int requestType = Define.TOTAL_SEARCH;
	
	ListView playListView = null;
	TextView emptyListView = null;

	UserPlayListAdapter playListAdapter = null;

	ExtraDao extraDao = null;
	ArrayList<BaseModel> playList = null;

	public FragmentForUserPlayList() {
		super();
	}
	
	public FragmentForUserPlayList(Context context) {
		super(context);
	}

	@Override
	public void settingView() {
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);

		playListView = (ListView) getView().findViewById(R.id.play_list_view);
		playListView.setEmptyView(emptyListView);
		playListView.setOnItemClickListener(this);

		setList();
	}
	
	public void setList() {
		extraDao = new ExtraDao(getActivity());
		playList = extraDao.getPlayListData();
		extraDao.close();

		if(playList != null) {
			if(playListView.getAdapter() == null){
				playListAdapter = new UserPlayListAdapter(getActivity());
				playListAdapter.setOnAdapterButtonClickListener(this);
				playListAdapter.setList(playList);
				playListView.setAdapter(playListAdapter);
			}
			else
			{
				playListAdapter.setList(playList);
			}
			
			playListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public int registerContentView() {
		return R.layout.fragment_for_user_play_list;
	}
	
	@Override
	public void setSearchQueryType(String query, int type) {
		requestQuery = query;
		requestType = type;
		playListAdapter.reset();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		JoinedUserPlayListModel item = (JoinedUserPlayListModel)playList.get(position);
		UserPlayListDao userPlayListDao = new UserPlayListDao(getActivity());
		String whereClause = "where " + UserPlayListDao.FOLDER_NO + " = " + item.no;
		ArrayList<BaseModel> trackList = userPlayListDao.getData(whereClause);
		
		NavigationUtils.setUserPlayDetail(getActivity(), trackList);
	}

	@Override
	public void onAdapterButtonClick() {
		setList();
		Toast.makeText(getActivity(), "deleted", 0).show();
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
	public void onRequestResult(Object arg0, int arg1) {}
}
