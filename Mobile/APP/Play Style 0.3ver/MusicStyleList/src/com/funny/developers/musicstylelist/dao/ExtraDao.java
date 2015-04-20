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
		 select _id, _title, _thumbnail, duration, _trackType
		 from tb_user_play_list
		 */
		
		String sql = "select " + UserPlayListDao.ID + ", " + UserPlayListDao.TITLE + ", " + UserPlayListDao.THUMBNAIL +
					", " + UserPlayListDao.DURATION + ", " + UserPlayListDao.TRACK_TYPE + ", " + UserPlayListDao.NO +
					", " + UserPlayListDao.UPLOADER +
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
					model.duration = cursor.getString(3);
					model.trackType = cursor.getInt(4);
					model.no = cursor.getInt(5);
					model.uploader = cursor.getString(6);

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
	
	public ArrayList<BaseModel> getTrackData(String query, int type) {
		Cursor cursor = null;
		ArrayList<BaseModel> listData = new ArrayList<BaseModel>();
		
		/*
		 select _id, _title, _thumbnail, duration, _trackType
		 from tb_user_play_list
		 */
		
		//String whereClause = "where " + UserPlayListDao.FOLDER_NO + " = " + item.no;
		
		String sql = "select " + UserPlayListDao.ID + ", " + UserPlayListDao.TITLE + ", " + UserPlayListDao.THUMBNAIL +
					", " + UserPlayListDao.DURATION + ", " + UserPlayListDao.TRACK_TYPE + ", " + UserPlayListDao.NO +
					", " + UserPlayListDao.UPLOADER +
					" from " + UserPlayListDao.TABLE_NAME;
		
		String where = null;
		
		switch(type){
		case 1:
			where = "";
			break;
		
		case 2:
			where = " where " + UserPlayListDao.TITLE + " like '%" + query + "%'";
			break;
			
		case 3:
			where = " where " + UserPlayListDao.UPLOADER + " like '%" + query + "%'";
			break;
		}
		
		String finalsql = sql + where;
		
		try {
			cursor = db.getData(finalsql);

			if (cursor.moveToFirst()) 
			{
				do {
					SearchTrackListModel model = new SearchTrackListModel();
					model.id = cursor.getString(0);
					model.title = cursor.getString(1);
					model.thumbnail = cursor.getString(2);
					model.duration = cursor.getString(3);
					model.trackType = cursor.getInt(4);
					model.no = cursor.getInt(5);
					model.uploader = cursor.getString(6);

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
		 from tb_user_folders folder LEFT OUTER JOIN tb_user_play_list list
		 ON folder._no = list._folderNo
		 group by folder._no
		 */
		
		String sql = "select folder." + UserFoldersDao.NO + ", folder." + UserFoldersDao.FOLDER_NAME +
				     ", group_concat(list." + UserPlayListDao.THUMBNAIL + "), count(list." + UserPlayListDao.ID  + ") " +
				     "from " + UserFoldersDao.TABLE_NAME + " folder LEFT OUTER JOIN " + UserPlayListDao.TABLE_NAME + " list " +
				     "ON folder." + UserFoldersDao.NO + " = list." +UserPlayListDao.FOLDER_NO + " " + 
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
					if(tempThumb != null){
						int idx = tempThumb.indexOf(",");
						if(idx > 0)
							tempThumb = tempThumb.substring(0, idx);
					}
					model.thumbNail = tempThumb;
					model.cntSongs = cursor.getInt(3);

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
