package com.funny.developers.musicstylelist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.funny.developers.musicstylelist.dao.UserFoldersDao;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;

public class UserPlayListDBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
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
	
	private static final String CREATE_TABLE_USER_FOLDERS = "create table "+ UserFoldersDao.TABLE_NAME +" (" +
			UserFoldersDao.NO + "		   integer primary key autoincrement," +
			UserFoldersDao.FOLDER_NAME + " text    not null)";
	
	
	private static final String CREATE_TABLE_USER_PLAY_LIST = "create table "+ UserPlayListDao.TABLE_NAME +" (" +
			UserPlayListDao.NO + "		    integer primary key autoincrement," +
			UserPlayListDao.ID + "          text    not null,"+
			UserPlayListDao.TITLE + "       text    not null,"+
			UserPlayListDao.THUMBNAIL + "   text    not null,"+
			UserPlayListDao.DURATION + "    text    not null,"+
			UserPlayListDao.TRACK_TYPE + "  integer,"+
			UserPlayListDao.UPLOADER + " text    not null,"+
			UserPlayListDao.FOLDER_NO + "   integer,"+
			"FOREIGN KEY(" + UserPlayListDao.FOLDER_NO + ") REFERENCES " +
			UserFoldersDao.TABLE_NAME + "(" + UserFoldersDao.NO + ") ON DELETE CASCADE)";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER_FOLDERS);
		db.execSQL(CREATE_TABLE_USER_PLAY_LIST);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if(!db.isReadOnly()){
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + UserFoldersDao.TABLE_NAME);
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
