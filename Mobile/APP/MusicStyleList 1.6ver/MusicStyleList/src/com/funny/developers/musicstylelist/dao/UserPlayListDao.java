package com.funny.developers.musicstylelist.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.funny.developers.musicstylelist.database.UserPlayListDBHelper;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;

public class UserPlayListDao {
	public static final String TABLE_NAME = "tb_user_play_list";
	public static final String NO = "_no";
	public static final String ID = "_id";
	public static final String TITLE = "_title";
	public static final String THUMBNAIL = "_thumbnail";
	public static final String UPLOAD_DATE = "_uploaddate";
	public static final String DURATION = "_duration";
	public static final String VIEW_COUNT = "_viewCount";
	public static final String TRACK_TYPE = "_trackType";
	public static final String FOLDER_NO = "_folderNo";

	private UserPlayListDBHelper db = null;

	public UserPlayListDao(Context context) {
		db = UserPlayListDBHelper.getInstance(context);
	}

	public void insert(SearchTrackListModel model) {
		ContentValues values = new ContentValues();

		values.put(ID, model.id);
		values.put(TITLE, model.title);
		values.put(THUMBNAIL, model.thumbnail);
		values.put(UPLOAD_DATE, model.uploaded);
		values.put(DURATION, model.duration);
		values.put(VIEW_COUNT, model.viewCount);
		values.put(TRACK_TYPE, model.trackType);
		values.put(FOLDER_NO, model.folderNo);

		long rowId = db.insert(TABLE_NAME, values);

		if(rowId < 0)
			Log.e("kim" , "fail at insert db");
	}

	public void update(SearchTrackListModel model) {
		ContentValues values = new ContentValues();

		values.put(ID, model.id);
		values.put(TITLE, model.title);
		values.put(THUMBNAIL, model.thumbnail);
		values.put(UPLOAD_DATE, model.uploaded);
		values.put(DURATION, model.duration);
		values.put(VIEW_COUNT, model.viewCount);
		values.put(TRACK_TYPE, model.trackType);
		values.put(FOLDER_NO, model.folderNo);
		
		db.update(TABLE_NAME, values, NO + "=" + model.no);
	}

	public void delete(SearchTrackListModel model) {
		db.delete(TABLE_NAME, NO + "=" + model.no);
	}

	public ArrayList<BaseModel> getData(String whereClause) {
		Cursor cursor = null;
		ArrayList<BaseModel> playListData = new ArrayList<BaseModel>();

		String sql = null;
		
		if(whereClause == null)
			sql = "select * from " + TABLE_NAME + " order by 1";
		else
			sql = "select * from " + TABLE_NAME + " " +whereClause +" order by 1";

		try {
			cursor = db.getData(sql);

			if (cursor.moveToFirst()) 
			{
				do {
					SearchTrackListModel model = new SearchTrackListModel();
					model.no = cursor.getInt(0);
					model.id = cursor.getString(1);
					model.title = cursor.getString(2);
					model.thumbnail = cursor.getString(3);
					model.uploaded = cursor.getString(4);
					model.duration = cursor.getInt(5);
					model.viewCount = cursor.getInt(6);
					model.trackType = cursor.getInt(7);
					model.folderNo = cursor.getInt(8);
					
					playListData.add(model);
				} while (cursor.moveToNext());
			}
		} catch(SQLException se) {
			se.getStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}

		return playListData;
	}

	public void close() {
		db.close();
	}
	
}