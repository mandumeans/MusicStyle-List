package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.dialog.UserFoldersPopUp;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class SearchListAdapter extends BaseListAdapter implements OnClickListener{ 
	
	public SearchListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_search_track_list_data, null);
			holder = new ViewHolder();
			
			holder.mThumbnail = (ImageView)convertView.findViewById(R.id.list_thumb_image_view);
			holder.mTitle = (TextView)convertView.findViewById(R.id.list_title_text_view);
			holder.mViewCount = (TextView)convertView.findViewById(R.id.list_viewcount_text_view);
			holder.mUploaded = (TextView)convertView.findViewById(R.id.list_uploaded_text_view);
			holder.mAddButton = (Button)convertView.findViewById(R.id.list_add_playlist);
			holder.mAddButton.setFocusable(false);
			holder.mAddButton.setOnClickListener(this);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SearchTrackListModel item = (SearchTrackListModel)mList.get(position);
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.playstyle_icon)
			.showImageForEmptyUri(R.drawable.playstyle_icon)
			.build();
		imageLoader.displayImage(item.thumbnail, holder.mThumbnail, options);
		
		holder.mTitle.setText(item.title);
		holder.mViewCount.setText(item.viewCount + "");
		holder.mUploaded.setText(item.uploaded);
		holder.mAddButton.setTag(position);
		
		return convertView;
	}
	
	protected class ViewHolder{
		ImageView mThumbnail = null;
		TextView mTitle  = null;
		TextView mViewCount = null;
		TextView mUploaded = null;
		Button mAddButton = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_add_playlist:
			int position = (Integer) v.getTag();
			SearchTrackListModel item = (SearchTrackListModel)mList.get(position);
			
			UserFoldersPopUp dialog = new UserFoldersPopUp(mContext);
			dialog.setModel(item);
			dialog.show();
			
//			UserPlayListDao dao = new UserPlayListDao(mContext);
//			dao.insert(item);
//			dao.close();
//			Toast.makeText(mContext, "added", 0).show();
			break;
		}
		
	}
}
