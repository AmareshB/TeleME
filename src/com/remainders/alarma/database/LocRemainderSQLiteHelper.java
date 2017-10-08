package com.remainders.alarma.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocRemainderSQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_PENDINGINTENT = "pendingmapintents";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_LAT = "lat";
  public static final String COLUMN_LON= "lon";
  public static final String COLUMN_MESSAGE = "message";
  public static final String COLUMN_PLACE = "place";
  public static final String NOTIFIED = "notified";
  private static final String DATABASE_NAME = "pendingmapintents.db";
  private static final int DATABASE_VERSION = 1;
  
//Database creation sql statement
 private static final String DATABASE_CREATE = "create table " + TABLE_PENDINGINTENT + "(" + COLUMN_ID + " integer primary key , " + COLUMN_LAT +" real," + COLUMN_LON +" real,  "  + COLUMN_MESSAGE + " String, " + COLUMN_PLACE + " String, " + NOTIFIED + " integer " + ");";
	
	public LocRemainderSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		
		arg0.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDINGINTENT);
	    onCreate(db);
	}
	
	

}
