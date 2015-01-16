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
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class CopyOfUserPlayListAdapter extends BaseListAdapter implements OnClickListener{ 
	
	private OnAdapterButtonClickListener listener = null;
	
	public interface OnAdapterButtonClickListener
	{
		public void onAdapterButtonClick();
	}
	
	public void setOnAdapterButtonClickListener(OnAdapterButtonClickListener listener)
	{
		this.listener= listener; 
	}
	
	public CopyOfUserPlayListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_user_play_list_data, null);
			holder = new ViewHolder();
			
			holder.mThumbnail = (ImageView)convertView.findViewById(R.id.list_thumb_image_view);
			holder.mTitle = (TextView)convertView.findViewById(R.id.list_title_text_view);
			holder.mViewCount = (TextView)convertView.findViewById(R.id.list_viewcount_text_view);
			holder.mUploaded = (TextView)convertView.findViewById(R.id.list_uploaded_text_view);
			holder.mDelButton = (Button)convertView.findViewById(R.id.list_del_playlist);
			holder.mDelButton.setFocusable(false);
			holder.mDelButton.setOnClickListener(this);
			
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
		holder.mDelButton.setTag(position);
		
		return convertView;
	}
	
	protected class ViewHolder{
		ImageView mThumbnail = null;
		TextView mTitle  = null;
		TextView mViewCount = null;
		TextView mUploaded = null;
		Button mDelButton = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_del_playlist:
			int position = (Integer) v.getTag();
			SearchTrackListModel item = (SearchTrackListModel)mList.get(position);
			
			UserPlayListDao dao = new UserPlayListDao(mContext);
			dao.delete(item);
			dao.close();
			listener.onAdapterButtonClick();
			break;
		}
		
	}
}
