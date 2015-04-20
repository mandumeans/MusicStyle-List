package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.model.SearchPlayListModel;
import com.funny.developers.musicstylelist.util.ImageUtils;

public class SearchPlayListAdapter extends BaseListAdapter{ 
	
	public SearchPlayListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_play_list_data, parent, false);
			holder = new ViewHolder();
			
			holder.mThumbnail = (ImageView)convertView.findViewById(R.id.list_thumb_image_view);
			holder.mTitle = (TextView)convertView.findViewById(R.id.list_title_text_view);
			holder.mSize = (TextView)convertView.findViewById(R.id.list_size_text_view);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SearchPlayListModel item = (SearchPlayListModel)mList.get(position);
		
		ImageUtils.displayUrlImage(holder.mThumbnail, item.thumbnail, R.drawable.no_image);
		
		holder.mTitle.setText(item.title);
		holder.mSize.setText(item.size + "");
		
		return convertView;
	}
	
	protected class ViewHolder{
		ImageView mThumbnail = null;
		TextView mTitle  = null;
		TextView mSize = null;
	}
}
