package org.tomshiro.ada;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class QueryMachine {
	/*
	 * Read a query and its parameters from Query.xml and throw it at the database.
	 */
	
	static final String TAG="QueryMachine";
	static final String ScreenSpecifierName="Query.xml";
	String reportDirName;
	HashMap<String,Object> SqlParams;
	String MyQuery;
	String[] QueryParms;
	AdaDB myDBHelper;
	
	public QueryMachine(String reportName,HashMap<String,Object>reportParams,AdaDB theDB)
	{
		reportDirName = reportName;
		SqlParams = reportParams;
		readQueryParamsFromXml(reportDirName,ScreenSpecifierName);
		myDBHelper=theDB;
	}
	
	/*
	 * Read query and parameters from xml file.
	 */
	private void readQueryParamsFromXml(String dirname,String filename)
	{
		String retVal=null;
		Log.d(TAG,"readQueryParamsFromXml");
		File xmlFile = new File(reportDirName+File.separator+ScreenSpecifierName);
		Log.d(TAG,"readQueryParamsFromXML: xmlfile = "+xmlFile.getAbsolutePath());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document domDoc = dBuilder.parse(xmlFile);
			domDoc.getDocumentElement().normalize();
	        Log.d(TAG,"readQueryParamsFromXml root element: "+domDoc.getDocumentElement().getNodeName());					
			NodeList Plist = domDoc.getElementsByTagName("Param");
			Node qNode = domDoc.getElementsByTagName("Query").item(0); // Should be only one.
			Element qElement = (Element)qNode;
			MyQuery= qElement.getTextContent();
			Log.d(TAG,"readQueryParams: MyQuery = "+MyQuery);
			String[] parmArray = new String[Plist.getLength()];
			for(int ii=0;ii<Plist.getLength();++ii)
			{
				qElement=(Element)Plist.item(ii);
				parmArray[ii] = qElement.getAttribute("ref");
			}
			QueryParms = parmArray;
			for(int ii=0;ii<QueryParms.length;++ii)
				Log.d(TAG,"readQueryParamsFromXml: parm " + ii + " = " + QueryParms[ii]);
		}
		
		catch(Exception e)
		{
			Log.e(TAG,"readQueryParams: "+e.toString());
			e.printStackTrace();
		}
	}
	/*
	 * Run the query we have set up.
	 */
	public ArrayList<String> runQuery()
	{
		Log.d(TAG,"runQuery");
		if(null != QueryParms)
			Log.d(TAG,"runQuery QueryParms:"+QueryParms.toString());
		else
			Log.d(TAG,"runQuery QueryParms is NULL");
		
	    String[] sendParms = new String[QueryParms.length];
	    // Get parameter values from input by user by key, put them into 
	    // the parameters passed to the query.
	    for(int ii=0;ii<QueryParms.length;++ii)
	    {	   
	    	sendParms[ii] = SqlParams.get(QueryParms[ii]).toString();
	    	Log.d(TAG,"runQuery sendParms " + ii + " = " + sendParms[ii]);
	    }
		ArrayList<String> queryResults = myDBHelper.runReportQuery(MyQuery,sendParms);
		
		return queryResults;
	}
	
} // QueryMachine
