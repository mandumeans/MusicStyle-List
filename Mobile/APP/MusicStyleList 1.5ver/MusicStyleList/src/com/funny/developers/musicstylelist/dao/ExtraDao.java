package com.funny.developers.musicstylelist.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.funny.developers.musicstylelist.database.UserPlayListDBHelper;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.JoinedUserPlayListModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;

public class ExtraDao {
	private UserPlayListDBHelper db = null;
	
	public ExtraDao(Context context) {
		db = UserPlayListDBHelper.getInstance(context);
	}
	
	public ArrayList<BaseModel> getTrackData() {
		Cursor cursor = null;
		ArrayList<BaseModel> listData = new ArrayList<BaseModel>();
		
		/*
		 select _id, _title, _thumbnail, _viewCount, _trackType
		 from tb_user_play_list
		 */
		
		String sql = "select " + UserPlayListDao.ID + ", " + UserPlayListDao.TITLE + ", " + UserPlayListDao.THUMBNAIL +
					", " + UserPlayListDao.VIEW_COUNT + ", " + UserPlayListDao.TRACK_TYPE +
					" from " + UserPlayListDao.TABLE_NAME;
		
		try {
			cursor = db.getData(sql);

			if (cursor.moveToFirst()) 
			{
				do {
					SearchTrackListModel model = new SearchTrackListModel();
					model.id = cursor.getString(0);
					model.title = cursor.getString(1);
					model.thumbnail = cursor.getString(2);
					model.viewCount = cursor.getInt(3);
					model.trackType = cursor.getInt(4);

					listData.add(model);
				} while (cursor.moveToNext());
			}
		} catch(SQLException se) {
			se.getStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}
		
		return listData;
	}


	public ArrayList<BaseModel> getPlayListData() {
		Cursor cursor = null;
		ArrayList<BaseModel> listData = new ArrayList<BaseModel>();

		/*
		 select  folder._no, folder._folderName, group_concat(list._thumbnail), count(list._id)
		 from tb_user_folders folder, tb_user_play_list list
		 where folder._no = list._folderNo
		 group by folder._no
		 */
		
		String sql = "select folder." + UserFoldersDao.NO + ", folder." + UserFoldersDao.FOLDER_NAME +
				     ", group_concat(list." + UserPlayListDao.THUMBNAIL + "), count(list." + UserPlayListDao.ID + ") " +
				     "from " + UserFoldersDao.TABLE_NAME + " folder, " + UserPlayListDao.TABLE_NAME + " list " +
				     "where folder." + UserFoldersDao.NO + " = list." +UserPlayListDao.FOLDER_NO + " " + 
				     "group by folder." + UserFoldersDao.NO;
		
		try {
			cursor = db.getData(sql);

			if (cursor.moveToFirst()) 
			{
				do {
					JoinedUserPlayListModel model = new JoinedUserPlayListModel();
					model.no = cursor.getInt(0);
					model.folderName = cursor.getString(1);
					String tempThumb = cursor.getString(2);
					int idx = tempThumb.indexOf(",");
					if(idx > 0)
						tempThumb = tempThumb.substring(0, idx);
					model.thumbNail = tempThumb;
					model.cntSongs = cursor.getInt(3);
//					model.totalPlayTime = cursor.getLong(4);

					listData.add(model);
				} while (cursor.moveToNext());
			}
		} catch(SQLException se) {
			se.getStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}

		return listData;
	}

	public void close() {
		db.close();
	}
}
