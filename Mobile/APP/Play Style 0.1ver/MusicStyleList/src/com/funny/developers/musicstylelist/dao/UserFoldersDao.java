package com.funny.developers.musicstylelist.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.Toast;

import com.funny.developers.musicstylelist.database.UserPlayListDBHelper;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.UserFoldersModel;

public class UserFoldersDao {
	public static final String TABLE_NAME = "tb_user_folders";
	public static final String NO = "_no";
	public static final String FOLDER_NAME = "_folderName";

	private UserPlayListDBHelper db = null;
	private Context context = null;
	
	public UserFoldersDao(Context context) {
		db = UserPlayListDBHelper.getInstance(context);
		this.context = context;
	}

	public void insert(UserFoldersModel model) {
		ContentValues values = new ContentValues();

		values.put(FOLDER_NAME, model.folderName);

		long rowId = db.insert(TABLE_NAME, values);

		if(rowId < 0)
			Toast.makeText(context, "폴더명을 입력하세요.", Toast.LENGTH_SHORT).show();
	}

	public void update(UserFoldersModel model) {
		ContentValues values = new ContentValues();

		values.put(FOLDER_NAME, model.folderName);

		db.update(TABLE_NAME, values, NO + "=" + model.no);
	}

	public void delete(UserFoldersModel model) {
		db.delete(TABLE_NAME, NO + "=" + model.no);
		context.getContentResolver().notifyChange(NotifyUrlDefine.URI_NOTIFY_DATASET_CHANGE, null);
	}

	public ArrayList<BaseModel> getData() {
		Cursor cursor = null;
		ArrayList<BaseModel> listData = new ArrayList<BaseModel>();

		String sql = "select * from " + TABLE_NAME + " order by 1";

		try {
			cursor = db.getData(sql);

			if (cursor.moveToFirst()) 
			{
				do {
					UserFoldersModel model = new UserFoldersModel();
					model.no = cursor.getInt(0);
					model.folderName = cursor.getString(1);

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
