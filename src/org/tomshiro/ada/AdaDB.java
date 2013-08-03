package org.tomshiro.ada;

import java.io.*;
import java.util.*;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor; 
import android.util.Log;
import android.content.ContentValues;


class AdaDB extends SQLiteOpenHelper {
	static final String DBNAME="ada.db";
	static final String SQLFILENAME="db_design.sql";
	static final int DBVERSION=1;
	static final String TAG="AdaDB";
	
	Context myContext;
	
	// Constructor
	public AdaDB(Context context) 
	{		
		super(new DatabaseContext(context),DBNAME,null,DBVERSION);
		this.myContext=new DatabaseContext(context);
		Log.d(TAG,"Created");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
	{
		try {
			copyDataBase(db);
	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeBout(int bout_ID)
	{
		Log.d(TAG,"closeBout");
		String boutUpdateSQL="update Bout set end_time = datetime('now') where " +
				"_id = ?";

		SQLiteDatabase db=this.getWritableDatabase();
		Integer[] boutIDValue = new Integer[1];
		boutIDValue[0] = bout_ID;
		try {
			db.execSQL(boutUpdateSQL,boutIDValue);
		}
		catch(Exception e) {
			Log.e(TAG,"Exception: ["+e.toString()+"]");
		}
		finally {
			db.close();
		}
	}
	
	/*
	 * Create a record in the Bout table. Return its _id.
	 */
	public int createBout(String fencerNameL,String fencerNameR)
	{
		Log.d(TAG,"createBout");
		int retVal = 0;
		Cursor recordFinderCursor=null;
		
		String boutRecordCreateSql="insert into Bout(lfencerfk,rfencerfk,start_time)" +
				"values( (select _id from Fencer where fencername = ?)," +
				"(select _id from Fencer where fencername = ?),datetime('now')) ";
		String boutRecordFindSql="select _id from Bout where " +
				"lfencerfk = (select _id from Fencer where fencername = ?) "+
				"and rfencerfk = (select _id from Fencer where fencername = ?) " +
				"and end_time is null order by start_time DESC limit 1";
			String[] values_array = new String[2];
			values_array[0]=fencerNameL;
			values_array[1]=fencerNameR;
			SQLiteDatabase db=this.getWritableDatabase();
			try {
				db.execSQL(boutRecordCreateSql,values_array);
				recordFinderCursor = db.rawQuery(boutRecordFindSql, values_array);
				recordFinderCursor.moveToFirst();
				retVal = recordFinderCursor.getInt(0);
			}
			catch(Exception e)
			{
				Log.e(TAG,"Exception: ["+e.toString()+"]");
			}
			finally {
				if (null != recordFinderCursor)
					recordFinderCursor.close();
				db.close();
			}
			Log.d(TAG,"createBout RetVal:"+retVal);
			
			return retVal;			
	}
	public void add_touch(int boutFk, boolean rTouch,boolean lTouch,int rActionSeq,int lActionSeq,int tSeq)
	{
		Log.d(TAG,"add_touch");
		SQLiteDatabase db;
		
		db=this.getWritableDatabase();
		
		String touchRecordCreateSql = "insert into Touch" +
				"(boutfk,ractionfk,lactionfk,right_scored,left_scored,touch_sequence,committed_time) "+
				"values(?," +
				"(select _id from Action_type where seq = ?)," +
				"(select _id from Action_type where seq = ?)," +
				"?,?,?,datetime('now'))";
		String[] insert_array = new String[6];
		insert_array[0] = Integer.toString(boutFk);
		insert_array[1] = Integer.toString(rActionSeq);
		insert_array[2] = Integer.toString(lActionSeq);
		insert_array[3] = (rTouch == false)? "0":"1";
		insert_array[4] = (lTouch == false)? "0":"1";
		insert_array[5] = Integer.toString(tSeq);
		try {
			db.execSQL(touchRecordCreateSql,insert_array);
		}
		catch(Exception e) {
			Log.e(TAG,"Exception: ["+e.toString()+"]");
		}
		finally {
			db.close();
		}
	}
	/*
	 *  Find a fencer by name in the Fencer table, return whether he is there.
	 */
	public void find_add_fencer(String fencername)
	{
		Log.d(TAG,"find_fencer");
		SQLiteDatabase db;
		
		
		db=this.getWritableDatabase();
		
		try {
			ContentValues fencerNameContainer = new ContentValues();
			fencerNameContainer.put("fencername", fencername);
			db.insertWithOnConflict("Fencer",null,fencerNameContainer,SQLiteDatabase.CONFLICT_IGNORE);
		}
		catch(Exception e)
		{
			Log.e(TAG,"Exception: ["+e.toString()+"]");
		}
		finally {
			db.close();
		}
	}
	
	private String makeRowFromCursor(Cursor theCursor)
	{
		String theRow = new String();
		if(theCursor.getColumnCount() > 0)
			theRow = theCursor.getString(0);
		for(int ii=1;ii<theCursor.getColumnCount();++ii)
			theRow = theRow + "|" + theCursor.getString(ii);
		
		return theRow;
	}
	/*
	 * Run a query to make a report.
	 */
	public ArrayList<String> runReportQuery(String querySql,String[] queryParameters)
	{
		Log.d(TAG,"runReportQuery");
		SQLiteDatabase db = null;
		ArrayList<String> retVal = new ArrayList<String>();
		Cursor theCursor = null;
		
		db=this.getReadableDatabase();
		try {
			theCursor = db.rawQuery(querySql,queryParameters);
			theCursor.moveToFirst();
			retVal.add("");
			if(0 < theCursor.getCount())
			{
				while (! theCursor.isLast())
				{
					retVal.add(makeRowFromCursor(theCursor));
					theCursor.move(1);
				}
				retVal.add(makeRowFromCursor(theCursor));
			}
			theCursor.close();
		}
		
		catch(Exception e)
		{
			Log.e(TAG,"Exception: ["+e.toString()+"]");
		}
		finally {
			if(theCursor != null)
				theCursor.close();
			if (db != null)
				db.close();
		}	
		return retVal;		
	}
	/*
	 * Run a query to fill a spinner
	 */
	public ArrayList<String> runListQuery(String querySql)
	{
		Log.d(TAG,"runListQuery");
		SQLiteDatabase db = null;
		ArrayList<String> retVal = new ArrayList<String>();
		Cursor theCursor = null;
		
		db=this.getReadableDatabase();
		try {
			theCursor = db.rawQuery(querySql,null);
			theCursor.moveToFirst();
			retVal.add("");
			if(0 < theCursor.getCount())
			{
				while (! theCursor.isLast())
				{
					retVal.add(theCursor.getString(0));
					theCursor.move(1);
				}
				retVal.add(theCursor.getString(0));
			}
			theCursor.close();
		}
		
		catch(Exception e)
		{
			Log.e(TAG,"Exception: ["+e.toString()+"]");
		}
		finally {
			if(theCursor != null)
				theCursor.close();
			if (db != null)
				db.close();
		}	
		return retVal;		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{		
		Log.d(TAG,"onCreate");
		if(! check_db_prepared(db))
		{
			try {
				copyDataBase(db);
			}
			catch(IOException e)
			{
				throw new Error("Error copying database: ["+e.toString()+"]");
			}
		}
	}
	
	private boolean check_db_prepared(SQLiteDatabase db)
	{
		boolean retVal=false;

		Cursor theCursor=db.rawQuery("select name from sqlite_master where name = 'Fencer'",null);
		if (theCursor != null)
		{
			if (0 < theCursor.getCount())
				retVal=true;
			theCursor.close();
		}	
		
		Log.d(TAG,"Retval = "+retVal);
		return retVal;
	}
	/*
	 * Create database from sql script shipped in assets.
	 */
	private void copyDataBase(SQLiteDatabase db) throws IOException
	{
	 Log.d(TAG,"copyDataBase");
	 
	 try {
		 DataInputStream myInputStream = new DataInputStream(myContext.getAssets().open(SQLFILENAME)); 
		 String sql_line;
		 ByteArrayOutputStream sqlStatement = new ByteArrayOutputStream();
		 while(null != (sql_line = myInputStream.readLine()))
		 {			 
			 if(sql_line.startsWith("--") || (0 == sql_line.length()))
				 continue;

			 sqlStatement.write(sql_line.getBytes(),0,sql_line.length());
			 Log.d(TAG,"copyDataBase: "+"["+sqlStatement.toString()+"]");	
			 sql_line = sql_line.trim();
			 if(true == sql_line.endsWith(";"))
			 {
				 sqlStatement.flush();				 
				 db.execSQL(sqlStatement.toString());
				 sqlStatement.reset();
			 }				 
		 }
	 }
	 catch(IOException e) 
	 {
		 Log.e(TAG,"IOException");
		 e.printStackTrace();
	 }
	}

}  //DBHelper


