package com.remainders.alarma.database;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


public class dbforsmart {
public static final String row_id = "row_id";
public static final String row_name = "row_name";
public static final String row_contactno = "row_contactno";
public static final String row_message = "row_message";

private static final String database_name = "smartcall";
public static final String database_table = "smartcalltab";
private static final int database_version = 1;


private DbHelper ourhelper;
private final Context ourcontext;
private SQLiteDatabase ourdatabase;

private static class DbHelper extends SQLiteOpenHelper
{

	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, database_name, null, database_version);
		// TODO Auto-generated constructor stub
	}

	

	public DbHelper(Context ourcontext) {
		// TODO Auto-generated constructor stub
	super(ourcontext,database_name,null,database_version);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	db.execSQL("CREATE TABLE " + database_table + "( " + row_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + row_name + " TEXT , "+ row_contactno +
			" TEXT , " + row_message+ " TEXT );"
		);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+ database_table);
		onCreate(db);
	}
	
}

public dbforsmart(Context ourcontext)
{
	this.ourcontext =ourcontext;
	

}

public dbforsmart open()
{
	ourhelper = new DbHelper(ourcontext);
	ourdatabase = ourhelper.getReadableDatabase();
	return this;
	
}

public void close()
{
	ourhelper.close();
}

public long createentry(String name,String number,String message)
{
	ContentValues cv = new ContentValues();
	cv.put(row_name, name);
	cv.put(row_contactno, number);
	cv.put(row_message, message);
	return ourdatabase.insert(database_table, null, cv);
	}



@SuppressLint("NewApi")
public ArrayList<String> getdata()
{
	String [] columns = new String[]{row_id,row_name,row_contactno,row_message};
	String q = "select * from '"+ database_table +"'";
	Cursor c = ourdatabase.rawQuery(q, null);
    //Cursor c = ourdatabase.query(false, database_table, columns, null, null, null, null, null, null, null);
    ArrayList<String> data = new ArrayList<String>();
    String result="";
    
    //int irow = c.getColumnIndex(row_id);
    int iname = c.getColumnIndex(row_name);
    //int inumber = c.getColumnIndex(row_contactno);
    int imessage = c.getColumnIndex(row_message);

    for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
    {
    	result = c.getString(iname) +"###"+ c.getString(imessage);
    	data.add(result.toString());
    }
  
   
   return data;

}

public String getNumber(String contact){
	
	String [] columns = new String[]{row_id,row_name,row_contactno,row_message};
	String q = "select * from '"+ database_table +"'";
	Cursor c = ourdatabase.rawQuery(q, null);
    //Cursor c = ourdatabase.query(false, database_table, columns, null, null, null, null, null, null, null);
    ArrayList<String> data = new ArrayList<String>();
    String result="";
    String name;
    int iname = c.getColumnIndex(row_name);
    int inumber = c.getColumnIndex(row_contactno);
    int imessage = c.getColumnIndex(row_message);
    for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
    {
    	name = c.getString(iname);
    	if(contact.equals(name)){
    	result = c.getString(inumber);
    	break;
    	}
    }
    
    return result;

}
public void deleteEntry(String delcon) {
	// TODO Auto-generated method stub
	System.out.println(delcon);
	if(delcon != null)
	{
	 try
	    {
	        ourdatabase.delete(database_table, "row_name = ?", new String[] { delcon });
	        
	    }
	    catch(Exception e)
	    {
	        Log.e("Error deleting", "error in deleteEntry");
	    }
	    finally
	    {
	        ourdatabase.close();
	    }
	}
	else
	{
		
	}
	
}

public void deleteEntryNumber(String delcon) {
	// TODO Auto-generated method stub
	System.out.println(delcon);
	if(delcon != null)
	{
	 try
	    {
	        ourdatabase.delete(database_table, "row_contactno = ?", new String[] { delcon });
	        
	    }
	    catch(Exception e)
	    {
	        Log.e("Error deleting", "error in deleteEntry");
	    }
	    finally
	    {
	        ourdatabase.close();
	    }
	}
	else
	{
		
	}
	
}
public String getmessage(String number) {
	// TODO Auto-generated method stub
	try{
	String msg = new String();
	String num = number;
	String [] columns = new String[]{row_name,row_contactno,row_message};
	String whereClause = row_contactno + " = ?";
	String[] whereArgs = new String[]{num};
	Cursor v = ourdatabase.query(database_table, columns, whereClause, whereArgs, null, null, null);
	int count = v.getCount();
	    if(v.moveToFirst())
        { 
		int id = v.getColumnIndex(row_message);
		msg = v.getString(id);
		//Toast.makeText(ourcontext, msg.toString() + " haha", Toast.LENGTH_LONG).show();
        }
		v.close();
		ourdatabase.close(); 
		if(count == 0)
		{
		return "";
		}
		else
		{
			return msg;
		}
	}
	catch(IllegalArgumentException e){
		return "";
	}
}


}