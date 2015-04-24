package com.funny.developers.musicstylelist.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.SearchPlayListDetailAdapter;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.NavigationUtils;

public class SearchPlayListDetailView extends PullToRefreshView implements OnItemClickListener{

	public SearchPlayListDetailView(Context context){
		super(context);
		mContext = context;
		SettingView(context);
	}

	public SearchPlayListDetailView(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		SettingView(context);
	}

	public SearchPlayListDetailView(Context context, AttributeSet attrs, int defStyle){
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
		} else if(position == 0) {
			return;
		}

		ArrayList<BaseModel> trackList =  ((SearchPlayListDetailAdapter)((HeaderViewListAdapter)getAdapter()).getWrappedAdapter()).getList();
		SearchTrackListModel model = (SearchTrackListModel)trackList.get(position - 1);

		NavigationUtils.goPlayerActivity(mContext, trackList, position - 1);
	}
}
