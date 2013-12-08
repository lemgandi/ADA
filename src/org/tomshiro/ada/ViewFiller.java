package org.tomshiro.ada;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;


public class ViewFiller 
{
	
	static final int FENCERLIST = 0;
	static final int ACTIONLIST = 1;
	static final String[] selectSql = { 
		"select fencername from Fencer order by fencername",
		"select actionname from Action_type order by seq"
	};
	static final String TAG = "ViewFiller";
	
	AdaDB myDbHelper = null;
	Context myContext=null;
	
	
	private void loadSpinner(Spinner theSpinner,String selectSql)
	{
		Log.d(TAG,"loadSpinner");
		
			
		ArrayList<String> parmsList = myDbHelper.runListQuery(selectSql);							
		ArrayAdapter<String> mySpinnerAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_spinner_dropdown_item,
				parmsList.toArray(new String[0]));
		theSpinner.setAdapter(mySpinnerAdapter);
	}
	
	private void handleSpinner(Spinner v,int fillType)
	{
		
		loadSpinner(v,selectSql[fillType]);
	}
	private void handleComboBox(ComboBox v,int fillType)
	{
		v.setChoiceList(selectSql[fillType],myDbHelper);		
	}
	
	// Main entry point
	public View fill_view(View v,int fillType, AdaDB Helper,Context theContext)
	{
			myDbHelper = Helper;
			myContext = theContext;
			
			if(Spinner.class == v.getClass())
				handleSpinner((Spinner)v,fillType);
			else if (ComboBox.class == v.getClass())
				handleComboBox((ComboBox)v,fillType);
			
			return v;
	}
	
	public View fill_id_view(View v,String tableName,AdaDB Helper,Context theContext)
	{
	   if(null == myDbHelper)	
		   myDbHelper = Helper;
	   if(null == theContext)
		   myContext = theContext;
	   
	   String table_id_select="select _id from "+tableName+" order by _id";
	   
	   loadSpinner((Spinner)v,table_id_select);
	   
	   return v;
	}

}
