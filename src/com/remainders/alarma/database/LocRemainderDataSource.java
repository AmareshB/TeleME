package com.remainders.alarma.database;


import java.util.ArrayList;
import java.util.List;

import com.remainders.alarma.locremainder.LocRemainderPendingIntent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocRemainderDataSource {

	// Database fields
	  private SQLiteDatabase database;
	  private LocRemainderSQLiteHelper dbHelper;
	  private String[] allColumns = { LocRemainderSQLiteHelper.COLUMN_ID, LocRemainderSQLiteHelper.COLUMN_LAT,LocRemainderSQLiteHelper.COLUMN_LON,LocRemainderSQLiteHelper.COLUMN_MESSAGE,LocRemainderSQLiteHelper.COLUMN_PLACE,LocRemainderSQLiteHelper.NOTIFIED };
	  
	  public LocRemainderDataSource(Context context){
		  dbHelper = new LocRemainderSQLiteHelper(context, "pendingintents.db",null, 2);
	  }
	  
	  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }
	  
	  public void close() {
		    dbHelper.close();
		  }
	  
	  public LocRemainderPendingIntent createPendingIntents(int id,Double lat,Double lon, String message,String place,int notified ){
		  ContentValues values = new ContentValues();
		  values.put(LocRemainderSQLiteHelper.COLUMN_ID, id);
		  values.put(LocRemainderSQLiteHelper.COLUMN_LAT, lat);
		  values.put(LocRemainderSQLiteHelper.COLUMN_LON, lon);
		  values.put(LocRemainderSQLiteHelper.COLUMN_MESSAGE, message);
		  values.put(LocRemainderSQLiteHelper.COLUMN_PLACE, place);
		  values.put(LocRemainderSQLiteHelper.NOTIFIED, notified);
		  
		  database.insert(LocRemainderSQLiteHelper.TABLE_PENDINGINTENT, null,
			        values);
		  Cursor cursor = database.query(LocRemainderSQLiteHelper.TABLE_PENDINGINTENT,
			        allColumns, LocRemainderSQLiteHelper.COLUMN_ID + " = " + id, null,
			        null, null, null);
		  
		  cursor.moveToFirst();
		  LocRemainderPendingIntent newPendingIntent = cursorToPendingIntent(cursor);
		  cursor.close();
		  return newPendingIntent;
	  }
	  public int updateNotification(int notify,long rowId){
		  //open();
		  Log.v("Notified = ", ""+notify);
		  ContentValues cv = new ContentValues();
		  cv.put(LocRemainderSQLiteHelper.NOTIFIED, notify);
		  /*database.rawQuery("UPDATE "+ LocRemainderSQLiteHelper.TABLE_PENDINGINTENT +
		             " SET " + LocRemainderSQLiteHelper.NOTIFIED + " = " + notify +
		             " WHERE " + LocRemainderSQLiteHelper.COLUMN_ID + " = " + rowId,
		             null);
		  */int suc = database.update(LocRemainderSQLiteHelper.TABLE_PENDINGINTENT, cv, LocRemainderSQLiteHelper.COLUMN_ID + " = " + rowId, null);
		  return suc;
		  
	  }
	  public int getNotified(long id){
		  Cursor cursor = database.rawQuery("Select '"+LocRemainderSQLiteHelper.NOTIFIED+"'  from '"+LocRemainderSQLiteHelper.TABLE_PENDINGINTENT+"' where '"+ LocRemainderSQLiteHelper.COLUMN_ID +"' = '"+ id + "';", null);
		  cursor.moveToFirst();
		  int notified = cursor.getInt(1);
		  cursor.close();
		  return notified;
	  }
	  public List<LocRemainderPendingIntent> getAllPendingIntents() {
		  List<LocRemainderPendingIntent> pendingIntents = new ArrayList<LocRemainderPendingIntent>();
		  Cursor cursor = database.query(LocRemainderSQLiteHelper.TABLE_PENDINGINTENT,
			        allColumns, null, null, null, null, null);
		  
		  cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	LocRemainderPendingIntent pendingIntent = cursorToPendingIntent(cursor);
		    	pendingIntents.add(pendingIntent);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return pendingIntents;
	  }
	  
	  private LocRemainderPendingIntent cursorToPendingIntent(Cursor cursor) {
		  LocRemainderPendingIntent pendingIntent = new LocRemainderPendingIntent();
		  pendingIntent.setId(cursor.getLong(0));
		  pendingIntent.setLat(cursor.getDouble(1));
		  pendingIntent.setLon(cursor.getDouble(2));
		  pendingIntent.setMessage(cursor.getString(3));
		  pendingIntent.setPlace(cursor.getString(4));
		  pendingIntent.setNotified(cursor.getInt(5));
		  return pendingIntent;
		  }
	  
	  public boolean deletePendingIntent(int id) 
		{
		 
		      return database.delete(LocRemainderSQLiteHelper.TABLE_PENDINGINTENT, LocRemainderSQLiteHelper.COLUMN_ID + "=" + id, null) > 0;
		  
		}
	  
}
