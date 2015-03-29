package com.funny.developers.musicstylelist.view;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.model.SearchPlayListModel;
import com.funny.developers.musicstylelist.util.NavigationUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchPlayListView extends PullToRefreshView implements OnItemClickListener{

	public SearchPlayListView(Context context){
		super(context);
		mContext = context;
		SettingView(context);
	}
	
	public SearchPlayListView(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		SettingView(context);
	}
	
	public SearchPlayListView(Context context, AttributeSet attrs, int defStyle){
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
		
		SearchPlayListModel model = (SearchPlayListModel)getAdapter().getItem(position);
		NavigationUtils.goPlaylistDetail(mContext, model.id, model.type, model.title, model.size, model.thumbnail);
	}
}
