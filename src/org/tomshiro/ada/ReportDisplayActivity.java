package org.tomshiro.ada;

import java.io.File;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.content.Intent;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReportDisplayActivity extends Activity { 
	
    static final String TAG="reportDisplayActivity";
    static final String ScreenSpecifierName = "Output.xml";
    public HashMap<String,Object> NameValsMap;
    String reportDir;
    AdaDB myDbHelper;
    QueryMachine TheQuery;
    ArrayList<View> viewsToAdd;
    NodeViewFactory Vf = null;
    ViewTypeParser VTypeParser=null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		setContentView(R.layout.activity_ada_report_display);
		Intent myIntent = getIntent();
		NameValsMap=(HashMap<String,Object>)myIntent.getSerializableExtra(ReportSetupActivity.QueryParametersName);
		reportDir = myIntent.getStringExtra(ReportSetupActivity.ReportDirName);
		myDbHelper = new AdaDB(this);
		TheQuery = new QueryMachine(reportDir,NameValsMap,myDbHelper);
		Vf = new NodeViewFactory(this,myDbHelper,this.getResources());
		VTypeParser = new ViewTypeParser();
		
		setupFields();
		/*
		 * Run a query and display results in logcat.
		ArrayList<String> queryResults = TheQuery.runQuery();
		Iterator<String> resultsIterator = queryResults.iterator();
		while (resultsIterator.hasNext())
			Log.d(TAG,"Query Results:"+resultsIterator.next());
		*/
		
	}
	/*
	 * Set up the contents of the label fields.
	 */
	public void setLabels(NodeList labelNodes)
	{
		
		Element theElement;
		ArrayList<View> retVal=null;
		int elemType = ViewTypeParser.LabelView;
		
		for(int ii = 0; ii<labelNodes.getLength(); ++ii)
		{
			theElement = (Element)labelNodes.item(ii);
			elemType = VTypeParser.getViewType(theElement.getAttribute("type"));
			switch(elemType)
			{
			case ViewTypeParser.LabelHeader:
				setTitle(theElement.getAttribute("text"));
				break;
			case ViewTypeParser.LabelSubHeader:
				do_subheader(theElement,R.id.ContentsHeader);
				break;
			case ViewTypeParser.InfoHeader:
				do_subheader(theElement,R.id.ColumnHeader);
			}
			
		}
	}
	
	private void do_subheader(Element theNodeElement,int viewID)
	{
		Log.d(TAG,"do_subheader");
		Object theObject=null;
		StrFormatLoop myFmtLooper=new StrFormatLoop(theNodeElement.getAttribute("text"));
		NodeList NodeListparmElements=theNodeElement.getElementsByTagName("Param");
		for(int ii=0;ii<NodeListparmElements.getLength();++ii)
		{
			theObject=Vf.parseParm(NodeListparmElements.item(ii),NameValsMap);
			myFmtLooper.feedValue(theObject);
		}
		TextView subHeaderWindow = (TextView)findViewById(viewID);
		subHeaderWindow.setText(myFmtLooper.getFormattedString());
		
	}

	void setupFields()
	{
		Log.d(TAG,"setupFields");
		File xmlFile = new File(reportDir+File.separator+ScreenSpecifierName);
		Log.d(TAG,"setupFields: xmlFile="+xmlFile.getAbsolutePath());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document domDoc = dBuilder.parse(xmlFile);
			domDoc.getDocumentElement().normalize();
	        Log.d(TAG,"setupFields root element: "+domDoc.getDocumentElement().getNodeName());			
			NodeList LList = domDoc.getElementsByTagName("Label");
			setLabels(LList);						
		}
		
		catch(Exception e)
		{
			Log.e(TAG,"setupFields,argh: "+e.toString());
			e.printStackTrace();
		}
		ArrayList<String> queryResults=TheQuery.runQuery();
		TextView queryDisplayView=(TextView)findViewById(R.id.reportContents);
		if(queryResults.size() < 1)
			queryDisplayView.setText(R.string.No_Data);
		else
		{
			for(int kk=0;kk<queryResults.size();++kk)
				queryDisplayView.append(queryResults.get(kk)+"\n");
				
		}
	}

	// Display has only one button, which is quit.
	public void Quit(View V)
	{
		finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_display, menu);
		return true;
	}

}
