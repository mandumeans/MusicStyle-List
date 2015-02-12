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
import com.funny.developers.musicstylelist.dao.UserFoldersDao;
import com.funny.developers.musicstylelist.model.JoinedUserPlayListModel;
import com.funny.developers.musicstylelist.model.UserFoldersModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class UserPlayListAdapter extends BaseListAdapter implements OnClickListener{ 

	private OnAdapterButtonClickListener listener = null;

	public interface OnAdapterButtonClickListener
	{
		public void onAdapterButtonClick();
	}

	public void setOnAdapterButtonClickListener(OnAdapterButtonClickListener listener)
	{
		this.listener= listener; 
	}

	public UserPlayListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_user_play_list_data, null);
			holder = new ViewHolder();

			holder.mImgThumb = (ImageView) convertView.findViewById(R.id.img_thumb);
			holder.mTextFolderTitle = (TextView)convertView.findViewById(R.id.text_folder_title);
			holder.mTextSongsCnt = (TextView)convertView.findViewById(R.id.text_songs_cnt);
			holder.mTextPlayTime = (TextView)convertView.findViewById(R.id.text_play_time);
			holder.mBtnDelFolder = (Button) convertView.findViewById(R.id.btn_del_folder);
			holder.mBtnDelFolder.setFocusable(false);
			holder.mBtnDelFolder.setOnClickListener(this);

			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		JoinedUserPlayListModel item = (JoinedUserPlayListModel)mList.get(position);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.no_image)
		.showImageForEmptyUri(R.drawable.no_image)
		.build();
		
		imageLoader.displayImage(item.thumbNail, holder.mImgThumb, options);
		
		holder.mTextFolderTitle.setText(item.folderName);
		holder.mTextSongsCnt.setText(String.valueOf(item.cntSongs));
		holder.mTextPlayTime.setText("playtime");
		holder.mBtnDelFolder.setTag(item.no);

		return convertView;
	}

	protected class ViewHolder{
		ImageView mImgThumb  = null;
		TextView mTextFolderTitle = null;
		TextView mTextSongsCnt = null;
		TextView mTextPlayTime = null;
		Button mBtnDelFolder = null;
	}

	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btn_del_folder) {
			UserFoldersModel model = new UserFoldersModel();
			model.no = (Integer) v.getTag();

			UserFoldersDao dao = new UserFoldersDao(mContext);
			dao.delete(model); // cascade로 지우기, 삭제후 새로고침
			dao.close();
			listener.onAdapterButtonClick();
		}

	}
}
