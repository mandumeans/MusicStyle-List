package com.funny.developers.musicstylelist.database;

import com.funny.developers.musicstylelist.dao.UserPlayListDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class UserPlayListDBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PlayStyle.db";
	
	private static SQLiteDatabase db = null;
	
	private Context mContext = null;
	
	public UserPlayListDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
	}
	
	public static UserPlayListDBHelper mInstance = null;

	public static UserPlayListDBHelper getInstance(Context context) {
		if (mInstance == null) {
			synchronized (UserPlayListDBHelper.class) {
				if (mInstance == null) {
					mInstance = new UserPlayListDBHelper(context);
					
					try {
						db = mInstance.getWritableDatabase();
					} catch(SQLiteException se) {
						se.getStackTrace();
					}
				}
			}
		}
		return mInstance;
	}

	public void close()	{
		if(mInstance != null) {
			db.close();
			mInstance = null;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTable = "create table "+ UserPlayListDao.TABLE_NAME +" (" +
				UserPlayListDao.NO + "		   integer primary key autoincrement," +
				UserPlayListDao.ID + "         text    not null,"+
				UserPlayListDao.TITLE + "      text    not null,"+
				UserPlayListDao.THUMBNAIL + "  text    not null,"+
				UserPlayListDao.VIEW_COUNT + " integer default 0,"+
				UserPlayListDao.TRACK_TYPE + " integer )";
		db.execSQL(createTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + UserPlayListDao.TABLE_NAME);
		onCreate(db);
	}
	
	public Cursor getData(String table, String[] columns) {
		return db.query(table, columns, null, null, null, null, null);
	}
	
	public Cursor getData(String sql) {
		return db.rawQuery(sql, null);
	}
	
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}
	
	public long update(String table, ContentValues values, String whereClause) {
		return db.update(table, values, whereClause, null);
	}
	
	public long delete(String table, String whereClause) {
		return db.delete(table, whereClause, null);
	}
	
}
