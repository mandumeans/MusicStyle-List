package com.funny.developers.musicstylelist.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.developers.musicstylelist.adapter.UserPlayListAdapter;
import com.funny.developers.musicstylelist.adapter.UserPlayListAdapter.OnAdapterButtonClickListener;
import com.funny.developers.musicstylelist.baseactivity.R;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.naviutil.NavigationUtils;

public class FragmentForUserPlayList extends Fragment implements OnItemClickListener, OnAdapterButtonClickListener {

	ListView playListView = null;
	TextView emptyListView = null;

	UserPlayListAdapter playListAdapter = null;

	UserPlayListDao userPlayListDao = null;
	ArrayList<BaseModel> playList = null;

	private Context context = null;

	public FragmentForUserPlayList() {}

	public FragmentForUserPlayList(Context context) {
		this.context = context;
	}

	public void settingView() {
		emptyListView = (TextView)getView().findViewById(R.id.empty_list_view);

		playListView = (ListView) getView().findViewById(R.id.play_list_view);
		playListView.setEmptyView(emptyListView);

		setPlayList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(registerContentView(), container, false);
	}

	public int registerContentView() {
		return R.layout.fragment_for_user_play_list;
	}

	@Override
	public void onStart() {
		super.onStart();
		settingView();
	}

	public void setPlayList() {
		userPlayListDao = new UserPlayListDao(getActivity());
		playList = userPlayListDao.getData();
		userPlayListDao.close();

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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NavigationUtils.setMediaPlayer(getActivity(), playList, position);

	}

	@Override
	public void onAdapterButtonClick() {
		setPlayList();
		Toast.makeText(getActivity(), "deleted", 0).show();
	}
}
