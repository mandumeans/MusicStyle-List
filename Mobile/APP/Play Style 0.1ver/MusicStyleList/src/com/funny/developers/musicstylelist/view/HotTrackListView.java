package com.funny.developers.musicstylelist.view;

import java.util.ArrayList;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.HotTrackListAdapter;
import com.funny.developers.musicstylelist.adapter.SearchListAdapter;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.mainactivity.MainActivity;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.NavigationUtils;
import com.funny.developers.musicstylelist.util.SettingUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class HotTrackListView extends PullToRefreshView implements OnItemClickListener{
	
	public HotTrackListView(Context context){
		super(context);
		mContext = context;
		SettingView(context);
	}

	public HotTrackListView(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		SettingView(context);
	}

	public HotTrackListView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		mContext = context;
		SettingView(context);
	}

	private void SettingView(Context context){
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		footerView = inflater.inflate(R.layout.layout_for_list_data_loading_footer, null);
		alertRefresh(false);
		addFooterView(footerView, null, false);

		setOnItemClickListener(this);
		setOnScrollListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if(view.equals(footerView)){
			return;
		}

		ArrayList<BaseModel> trackList = ((HotTrackListAdapter)((HeaderViewListAdapter)getAdapter()).getWrappedAdapter()).getList();
		SearchTrackListModel model = (SearchTrackListModel)trackList.get(position);

		if(model.onlyYoutube == Define.ONLY_YOUTUBE_TYPE){
			NavigationUtils.goYoutubePlayer(mContext, trackList, position);
		} else {
			if(model.trackType == Define.YOUTUBE_TRACK){
				if(SettingUtils.getUseYTPlayerType(mContext)){
					NavigationUtils.goYoutubePlayer(mContext, trackList, position);
				} else {
					MainActivity activity = (MainActivity) mContext;
					activity.setTrack(trackList, position);
				}
			} else {
				MainActivity activity = (MainActivity) mContext;
				activity.setTrack(trackList, position);
			}
		}
	}
}
