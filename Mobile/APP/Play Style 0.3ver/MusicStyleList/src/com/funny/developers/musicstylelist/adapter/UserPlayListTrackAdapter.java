package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.dialog.UserFoldersPopUp;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.util.DigitUtils;
import com.funny.developers.musicstylelist.util.ImageUtils;

public class UserPlayListTrackAdapter extends BaseListAdapter implements OnClickListener{ 
	
	public UserPlayListTrackAdapter(Context context) {
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
			holder.mDuration = (TextView)convertView.findViewById(R.id.list_duration_text_view);
			holder.mUploader = (TextView)convertView.findViewById(R.id.list_uploader_text_view);
			holder.mMenuButton = (Button)convertView.findViewById(R.id.list_add_playlist);
			holder.mMenuButton.setFocusable(false);
			holder.mMenuButton.setOnClickListener(this);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SearchTrackListModel item = (SearchTrackListModel)mList.get(position);
		
		ImageUtils.displayUrlImage(holder.mThumbnail, item.thumbnail, R.drawable.no_image);
		
		holder.mTitle.setText(item.title);
		holder.mDuration.setText(DigitUtils.stringForTime(item.duration, item.trackType));
		
		if(item.uploader.length() > 0){
			holder.mUploader.setText(item.uploader);
		} else {
			holder.mUploader.setText(Define.YOUTUBE_NOT_UPLOADER);
		}
		
		holder.mMenuButton.setBackgroundResource(R.drawable.selector_user_track_menu_button);
		holder.mMenuButton.setTag(position);
		
		return convertView;
	}
	
	protected class ViewHolder{
		ImageView mThumbnail = null;
		TextView mTitle  = null;
		TextView mDuration = null;
		TextView mUploader = null;
		Button mMenuButton = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_add_playlist:
			final int position = (Integer) v.getTag();
			PopupMenu popup = new PopupMenu(mContext, v);
	        popup.getMenuInflater().inflate(R.menu.user_play_list_track_popup, popup.getMenu());

	        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	            public boolean onMenuItemClick(MenuItem item) {
	            	int itemId = item.getItemId();
	            	SearchTrackListModel searchTrackListModelitem = (SearchTrackListModel)mList.get(position);
	            	
	            	switch (itemId) {
					case R.id.add:
						UserFoldersPopUp dialog = new UserFoldersPopUp(mContext);
						dialog.setModel(searchTrackListModelitem);
						dialog.show();
						break;
					case R.id.delete:
						UserPlayListDao dao = new UserPlayListDao(mContext);
						dao.delete(searchTrackListModelitem);
						dao.close();
						Toast.makeText(mContext, mContext.getString(R.string.delete_track) + searchTrackListModelitem.title, 1000).show();
						break;
					}
	                return true;
	            }
	        });

	        popup.show();
			break;
		}
		
	}
}
