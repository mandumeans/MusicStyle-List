package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.DigitUtils;

public class BackgroundPlayerListAdapter extends BaseListAdapter{
	
	public BackgroundPlayerListAdapter(Context context){
		super(context);
	}
	
	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_background_player_list_data, null);
			holder = new ViewHolder();
			
			holder.mTitle = (TextView)convertView.findViewById(R.id.list_title_text_view);
			holder.mDuration = (TextView)convertView.findViewById(R.id.list_duration_text_view);
			holder.mUploader = (TextView)convertView.findViewById(R.id.list_uploader_text_view);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		SearchTrackListModel item = (SearchTrackListModel)mList.get(position);
		
		holder.mTitle.setText(item.title);
		holder.mTitle.setSelected(true);
		
		holder.mUploader.setText(item.uploader);
		holder.mUploader.setSelected(true);
		
		holder.mDuration.setText(DigitUtils.stringForTime(item.duration, item.trackType));
		
		return convertView;
	}
	
	protected class ViewHolder{
		TextView mTitle  = null;
		TextView mDuration = null;
		TextView mUploader = null;
	}

}
