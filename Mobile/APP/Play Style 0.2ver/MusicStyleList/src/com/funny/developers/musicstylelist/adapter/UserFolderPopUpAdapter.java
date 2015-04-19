package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.baseadapter.BaseListAdapter;
import com.funny.developers.musicstylelist.model.UserFoldersModel;

public class UserFolderPopUpAdapter extends BaseListAdapter { 
	
	public UserFolderPopUpAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_for_user_folder_popup_data, null);
			holder = new ViewHolder();
			
			holder.mTextFolder = (TextView)convertView.findViewById(R.id.text_folder);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		UserFoldersModel item = (UserFoldersModel)mList.get(position);
		
		holder.mTextFolder.setText(item.folderName);
		
		return convertView;
	}
	
	protected class ViewHolder{
		TextView mTextFolder = null;
	}
}
