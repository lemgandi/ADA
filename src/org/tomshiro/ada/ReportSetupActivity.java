package org.tomshiro.ada;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.util.Log;
import android.content.*;
import java.io.*;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AbsSpinner;
import android.widget.Adapter;
import android.widget.TimePicker;
import android.content.Intent;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReportSetupActivity extends Activity implements OnClickListener {
	
    static final String TAG="ReportSetupActivity";
    static final String QueryParametersName="QueryParams";
    AlertDialog.Builder troubleDialogBuilder;
    static final String[] needed_filenames = { "Input.xml","Output.xml","Query.xml"};
    static final String ScreenSpecifierName="Input.xml";
    static final String ReportDirName="ReportsDirectory";
    ArrayList<View> viewsToAdd;
    
    AdaDB myDbHelper;
    
    String reportDir;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		setContentView(R.layout.activity_report_setup);

		Intent myIntent=getIntent();
		reportDir=myIntent.getStringExtra(ReportActivity.ReportName);
		Log.d(TAG,"onCreate reportDir="+reportDir);
		troubleDialogBuilder=new AlertDialog.Builder(this);
		myDbHelper = new AdaDB(this);
		this.load_fields();
	}
	public void onPause()
	{
		super.onPause();
		Log.d(TAG,"onPause");
	}
	
	public void onResume()
	{
		super.onResume();
		Log.d(TAG,"onResume");
//		this.load_fields();
	}
	
	private View getInputViewFromLayout(ViewGroup r)
	{
		Log.d(TAG,"getInputViewFromLayout: r="+r);
		View retVal=null;
		Integer vType;
		int ii = 0;
		
		while (ii<r.getChildCount())
		{
			retVal = r.getChildAt(ii);
			vType= (Integer)retVal.getTag(R.id.ViewTypeTag);
			Log.d(TAG,"getInputViewFromLayout: vType="+vType+" ii="+ii);
			if(vType.intValue() != ViewTypeParser.LabelView )
				break;
			++ii;
		}	
		
		return retVal;
	}
	private Boolean getBooleanContents(View v)
	{
		Log.d(TAG,"getCheckboxContents");
		Boolean retVal = false;
		RadioButton theCB = (RadioButton)v;
		
		retVal = Boolean.valueOf(theCB.isChecked());
		
		return retVal;
	}
	
	private String getSpinnerContents(View v)
	{
		String retVal = null;
		
		Adapter myAdapter = ((AbsSpinner)v).getAdapter();
		int whichItem=((Spinner)v).getSelectedItemPosition();
		retVal=(String)myAdapter.getItem(whichItem);
		
		return retVal;
	}
	private HashMap<String,Object> getFieldContents(View v)
	{
		Log.d(TAG,"getFieldContents");
		HashMap<String,Object> retVal= new HashMap<String,Object>(1);
		Object inptValue=null;
		
		View inputView = getInputViewFromLayout((ViewGroup)v);
		Integer theType=(Integer)inputView.getTag(R.id.ViewTypeTag);
		switch(theType.intValue())
		{			
		case ViewTypeParser.InputBooleanView:
			// sqlite does not have booleans; it has integer 1 and integer 0.
			if (true == getBooleanContents(inputView))
				inptValue = Integer.valueOf(1);
			else
				inptValue = Integer.valueOf(0);
			break;
		case ViewTypeParser.InputIntegerView:			
			inptValue = Integer.parseInt(getSpinnerContents(inputView));
		case ViewTypeParser.InputFencerView:
		case ViewTypeParser.InputActionView:
		case ViewTypeParser.InputIdView:
			inptValue = getSpinnerContents(inputView);
			break;
		case ViewTypeParser.InputIntervalView:
			inptValue = getIntervalContents(inputView);
			break;
		case ViewTypeParser.InputTextView:
			inptValue = getTextViewContents(inputView);
			break;
		}
		String valueName=(String)inputView.getTag(R.id.ViewNameTag);
		retVal.put(valueName,inptValue);
		return retVal;
	}
	private Object getTextViewContents(View theView)
	{
		String retVal=null;
		Boolean should_quote=(Boolean)theView.getTag(R.id.should_be_quoted);
		
		retVal=((EditText)theView).getText().toString();
		if(true == should_quote)
		{
			retVal = "'"+retVal+"'";
		}
		return retVal;
	}
	/*
	 * Intervals are always stored as numbers of seconds.
	 */
	private Object getIntervalContents(View theView)
	{
		Long retVal= null;		
		retVal=((IntervalView)theView).getTotalSecs();
		
		return retVal;
		
	}
	private void runReport()
	{
		Log.d(TAG,"runReport");		
		Log.d(TAG,"Number of Fields:"+ viewsToAdd.size());
		int status = 0;
		
		HashMap<String,Object> oneNameValMap;
		HashMap<String,Object> allNameValsMap = new HashMap<String,Object>(viewsToAdd.size());
		
		
	    for(int ii=0;ii<viewsToAdd.size()-1;++ii)
	    {
	    	oneNameValMap=getFieldContents(viewsToAdd.get(ii));
	    	allNameValsMap.putAll(oneNameValMap);	    	
	    }
	    // Possibly put validation that all fields are entered here?
	    if(0 == status)
	    {
	    	Intent runReportIntent = new Intent(this,ReportDisplayActivity.class);
	    	runReportIntent.putExtra(QueryParametersName,allNameValsMap);
	    	runReportIntent.putExtra(ReportDirName,reportDir);
	    	startActivity(runReportIntent);
	    }
	    /*
	     * View contents sent to next activity in logger.
	    Set<String> valNameKeySet;
		Object valNameVal;
    	valNameKeySet = allNameValsMap.keySet();
    	Iterator<String> keySetIterator = valNameKeySet.iterator();
    	while(keySetIterator.hasNext())
    	{    	
    	   String valNameKey=keySetIterator.next();
    	   valNameVal = (Object)allNameValsMap.get(valNameKey);
    	   Log.d(TAG,"Name: "+valNameKey+" Value "+valNameVal.toString());
    	}
    	*/
	}
	public void onClick(View v)
	{
		Log.d(TAG,"onClick");
			  
	   switch(v.getId())
	   {
	   case R.id.SubmitButton:
		   this.runReport();
		   break;
	   default:  // R.id.ReportSetupQuitButton:
		   finish();
		   break;
	   }
	   
	}

	private void load_fields()
	{
		Log.d(TAG,"load_fields");
		int success = 0;
		if(false == check_dirfiles(reportDir))
		{
			this.troubleDialogBuilder.setTitle(R.string.BadRptDirTitle);
			this.troubleDialogBuilder.setMessage(R.string.BadRptDirMessage);
			AlertDialog badFilesAlert = this.troubleDialogBuilder.create();
			badFilesAlert.show();
			success = -1;
		}
		if(0 == success)		
			this.add_load_fields();		
	}
	/*
	 * Currently handled in NodeViewFactory.
    private void addSubmitQuit()
    {
    	Log.d(TAG,"addSubmitQuit");
       RelativeLayout rootView = (RelativeLayout)findViewById(R.id.SetupRootView);
       LayoutInflater myInflater =	LayoutInflater.from(this);
       View SQ = myInflater.inflate(R.layout.submit_quit,rootView,false);
       rootView.addView(SQ);
    }
    */
	private void add_load_fields()
	{
		Log.d(TAG,"add_load_fields");
		File xmlFile = new File(reportDir+File.separator+ScreenSpecifierName);
		Log.d(TAG,"add_load_fields: xmlFile="+xmlFile.getAbsolutePath());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		NodeViewFactory vf = new NodeViewFactory(this,myDbHelper,this.getResources());
		
		
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document domDoc = dBuilder.parse(xmlFile);
			domDoc.getDocumentElement().normalize();
	        Log.d(TAG,"add_load_fields root element: "+domDoc.getDocumentElement().getNodeName());		
			NodeList FList = domDoc.getElementsByTagName("InputField");
			NodeList LList = domDoc.getElementsByTagName("Label");
			
			vf.setLabels(LList,this);  // Currently just sets title; might do more some day.
			
			viewsToAdd = vf.makeViews(FList);
			RelativeLayout rootView = (RelativeLayout)findViewById(R.id.SetupRootView);
			for(int ii=0;ii<viewsToAdd.size();++ii)
				rootView.addView(viewsToAdd.get(ii));
			ViewGroup submitQuitFrame = (ViewGroup)viewsToAdd.get(viewsToAdd.size() - 1);
			
			Button theButton;
			for(int ii=0;ii<submitQuitFrame.getChildCount();++ii)
			{
				theButton = (Button)submitQuitFrame.getChildAt(ii);			
				theButton.setOnClickListener(this);			
			}
			
		}
		
		catch(Exception e)
		{
			Log.e(TAG,"add_load_fields"+e.toString());
			e.printStackTrace();
		}
	//	this.addSubmitQuit();
	}
	
	private boolean check_dirfiles(String theDir)
	{
		boolean retVal = true;
		File dirLooker = new File(theDir);
		File[] flist = dirLooker.listFiles();
		int found_files=0;
		
		for(int kk=0;kk<flist.length;++kk) {
			for(int jj=0;jj<needed_filenames.length;++jj)
			{
				if (flist[kk].getName().equals(needed_filenames[jj]))
					++found_files;
			}
		}
		if(found_files < needed_filenames.length)
			retVal = false;
		return retVal;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			// return true;
		// }
		// return super.onOptionsItemSelected(item);
		return true;
	}

}
