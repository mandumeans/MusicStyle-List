package com.funny.developers.musicstylelist.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.UserPlayListAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.dao.ExtraDao;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.JoinedUserPlayListModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.NavigationUtils;

public class FragmentForUserPlayList extends BaseFragment implements OnItemClickListener {	
	private ListView playListView;
	private TextView emptyListView;

	private UserPlayListAdapter playListAdapter;

	private ExtraDao extraDao;
	private ArrayList<BaseModel> playList;
	
	private ContentObserver datasetChangeObserver;

	public FragmentForUserPlayList() {
		super();
	}
	
	public FragmentForUserPlayList(Context context) {
		super(context);
	}

	@Override
	public void settingView(View v) {
		emptyListView = (TextView) v.findViewById(R.id.empty_list_view);

		playListView = (ListView) v.findViewById(R.id.play_list_view);
		playListView.setEmptyView(emptyListView);
		playListView.setOnItemClickListener(this);
		
		datasetChangeObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				setList();
			}
		};
		
		getActivity().getContentResolver().registerContentObserver(NotifyUrlDefine.URI_NOTIFY_DATASET_CHANGE, 
				false, datasetChangeObserver);

		setList();
	}
	
	public void setList() {
		extraDao = new ExtraDao(getActivity());
		playList = extraDao.getPlayListData();
		extraDao.close();
		
		if(playList != null) {
			if(playListView.getAdapter() == null){
				playListAdapter = new UserPlayListAdapter(getActivity());
				playListAdapter.setList(playList);
				playListView.setAdapter(playListAdapter);
			} else {
				playListAdapter.setList(playList);
				playListAdapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public int registerContentView() {
		return R.layout.fragment_for_user_play_list;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		getActivity().getContentResolver().unregisterContentObserver(datasetChangeObserver);
	}
	
	@Override
	public void setSearchQueryType(String query, int type) {}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		JoinedUserPlayListModel item = (JoinedUserPlayListModel)playList.get(position);
		
		UserPlayListDao userPlayListDao = new UserPlayListDao(getActivity());
		String whereClause = "where " + UserPlayListDao.FOLDER_NO + " = " + item.no;
		ArrayList<BaseModel> trackList = userPlayListDao.getData(whereClause);
		
		if(trackList.size() == 0){
			return;
		}
		
		SearchTrackListModel trackModel = (SearchTrackListModel)trackList.get(0);
		userPlayListDao.close();
		
		NavigationUtils.goUserPlayDetail(getActivity(), item.no, trackModel.trackType, item.folderName, item.thumbNail, item.cntSongs);
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
