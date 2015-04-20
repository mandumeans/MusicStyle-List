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
import com.funny.developers.musicstylelist.adapter.UserPlayListTrackAdapter;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import com.funny.developers.musicstylelist.dao.ExtraDao;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.NavigationUtils;

public class FragmentForUserTrackList extends BaseFragment implements OnItemClickListener{
	
	private ListView userTrackListView;
	private UserPlayListTrackAdapter userTrackListAdapter;

	private ExtraDao extraDao;
	private ArrayList<BaseModel> trackList;

	private ContentObserver datasetChangeObserver;

	private TextView emptyListView;

	public FragmentForUserTrackList(){
		super();
	}

	public FragmentForUserTrackList(Context context) {
		super(context);
	}

	@Override
	public void settingView(View v) {

		emptyListView = (TextView) v.findViewById(R.id.empty_list_view);

		userTrackListView = (ListView) v.findViewById(R.id.play_track_list_view);
		userTrackListView.setEmptyView(emptyListView);

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
		trackList = extraDao.getTrackData();
		extraDao.close();

		if(trackList != null) {			
			if(userTrackListView.getAdapter() == null){
				userTrackListAdapter = new UserPlayListTrackAdapter(getActivity());
				userTrackListAdapter.setList(trackList);
				userTrackListView.setAdapter(userTrackListAdapter);
				userTrackListView.setOnItemClickListener(this);
			} else {
				userTrackListAdapter.setList(trackList);
				userTrackListAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_user_play_track_list;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		getActivity().getContentResolver().unregisterContentObserver(datasetChangeObserver);
	}

	@Override
	public void setSearchQueryType(String query, int type) {
		userTrackListAdapter.reset();

		extraDao = new ExtraDao(getActivity());
		trackList = extraDao.getTrackData(query, type);
		extraDao.close();

		if(trackList != null) {
			userTrackListAdapter.setList(trackList);
			userTrackListAdapter.notifyDataSetChanged();
		}
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		ArrayList<BaseModel> trackList = userTrackListAdapter.getList();
		SearchTrackListModel model = (SearchTrackListModel)trackList.get(position);

		NavigationUtils.goPlayerActivity(getActivity(), trackList, position);		
	}
}
