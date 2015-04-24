package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.dao.UserFoldersDao;
import com.funny.developers.musicstylelist.model.JoinedUserPlayListModel;
import com.funny.developers.musicstylelist.model.UserFoldersModel;
import com.funny.developers.musicstylelist.util.ImageUtils;

public class UserPlayListAdapter extends BaseListAdapter implements OnClickListener{ 

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
			holder.mBtnDelFolder = (Button) convertView.findViewById(R.id.btn_del_folder);
			holder.mBtnDelFolder.setFocusable(false);
			holder.mBtnDelFolder.setOnClickListener(this);

			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		JoinedUserPlayListModel item = (JoinedUserPlayListModel)mList.get(position);

		ImageUtils.displayUrlImage(holder.mImgThumb, item.thumbNail, R.drawable.no_image);

		holder.mTextFolderTitle.setText(item.folderName);
		holder.mTextSongsCnt.setText(String.valueOf(item.cntSongs));
		holder.mBtnDelFolder.setTag(position);

		return convertView;
	}

	protected class ViewHolder{
		ImageView mImgThumb  = null;
		TextView mTextFolderTitle = null;
		TextView mTextSongsCnt = null;
		Button mBtnDelFolder = null;
	}

	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.btn_del_folder) {
			int position = 0;
			UserFoldersModel model = new UserFoldersModel();
			position = (Integer) v.getTag();
			
			JoinedUserPlayListModel item = (JoinedUserPlayListModel)mList.get(position);
			
			model.no = item.no;
			model.folderName = item.folderName;

			UserFoldersDao dao = new UserFoldersDao(mContext);
			dao.delete(model);
			dao.close();
			
			Toast.makeText(mContext, mContext.getString(R.string.delete_folder) + model.folderName, 1000).show();
		}

	}
}
