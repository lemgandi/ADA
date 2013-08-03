package org.tomshiro.ada;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.*;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import android.os.Environment;
import java.io.*;
import java.util.zip.*;
import android.util.Log;
import android.app.AlertDialog;
import android.view.View;

public class ReportActivity extends Activity 
{
	static final String TAG="ReportActivity";
    static final String ReportDirPath="org.tomshiro.ada/reports";
    static final String DefaultRptZipFile = "defaultreports.zip";
    static final String ReportName="ReportName";
    String[] reportNamesList;
    
    AlertDialog.Builder troubleDialogBuilder;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		setContentView(R.layout.activity_ada_report);
		
		if(null == savedInstanceState)
		{
			this.createDefaultReports();
		}
		this.troubleDialogBuilder=new AlertDialog.Builder(this);
		
		this.load_fields();
	}
	/*
	 * Find where to load reports
	 */
	private File getMyDir()
	{
		Log.d(TAG,"getMyDir");
		File sdCardDir=Environment.getExternalStorageDirectory();
	   	
		File retVal = new File(sdCardDir.getAbsolutePath() + File.separator + ReportDirPath);
		Log.d(TAG,"getMyDir: retval=["+retVal.toString()+"]");
		return retVal;
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		Log.d(TAG,"onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
	this.getMenuInflater().inflate(R.menu.activity_ada_report, menu);
		return true;
	}
	/*
	 * Unzip default report files from assets if necessary (first time run).
	 */
	private void createDefaultReports()
	{
		Log.d(TAG,"createDefaultReports");
		File reportDir=this.getMyDir();
	
		if (false == reportDir.exists())
		{
			boolean retVal = reportDir.mkdirs();
			Log.d(TAG,"createDefaultReports: reportDir..mkdirs():"+retVal);
			try {
				ZipInputStream zippedRpts = new ZipInputStream(this.getAssets().open(DefaultRptZipFile));
				ZipEntry ze = null;
				while(null != (ze = zippedRpts.getNextEntry()))
				{
				if(ze.isDirectory())
				{
					File f = new File(this.getMyDir().getAbsolutePath() + File.separator + ze.getName());
					f.mkdirs();
				}
				else
				{
					FileOutputStream outStrm = new FileOutputStream(this.getMyDir()+File.separator+ze.getName());
					for(int c=zippedRpts.read();c != -1; c=zippedRpts.read())
					{
						outStrm.write(c);
					}
					zippedRpts.closeEntry();
					outStrm.close();
				}
				}
			}
			catch(IOException e)
			{
					Log.e(TAG,"createDefaultReports IOException");
					e.printStackTrace();
			}
			
		}
		
		
	}
	private void loadSpinnerFromDisk(int spinnerID)
	{
		Log.d(TAG,"loadSpinnerFromDisk");
		Spinner theSpinner=(Spinner)findViewById(spinnerID);
		ArrayList<String> parmsList = this.getReportNames();
		this.reportNamesList = parmsList.toArray(new String[0]);
		
		ArrayAdapter<String> mySpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
				this.reportNamesList);
		theSpinner.setAdapter(mySpinnerAdapter);
	}

	private ArrayList<String> getReportNames()
	{
		Log.d(TAG,"getReportNames");
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add("");
	    File f = this.getMyDir();
	    File fList[] = f.listFiles();
	    for(int ii=0; ii<fList.length; ii++)
	    {
	    	retVal.add(fList[ii].getName());
	    }
		return retVal;
		
	}

/*
 * Load spinner, other fields if necessary
 */
private void load_fields()
{
	Log.d(TAG,"load_fields");
	this.loadSpinnerFromDisk(R.id.report_type_spinner);
}

/*
 * User has chosen a report.
 */
public void onCommitClick(View v)
{
	int status=0;
	
	Spinner reportChoiceSpinner = (Spinner)this.findViewById(R.id.report_type_spinner);
	int rptId = reportChoiceSpinner.getSelectedItemPosition();
	if(0 == rptId)
	{
		troubleDialogBuilder.setTitle(R.string.NoReportTitle);
		troubleDialogBuilder.setMessage(R.string.NoReportMessage);
		AlertDialog noReportAlert = troubleDialogBuilder.create();
		noReportAlert.show();
		status = -1;
	}
	if(0 == status)
	{
		Intent reportIntent = new Intent(this,ReportSetupActivity.class);
		String rptFile=new String(this.getMyDir().getPath()+File.separator+this.reportNamesList[rptId]);
		reportIntent.putExtra(ReportName,rptFile);
		startActivity(reportIntent);
	}
}

} // ReportActivity.java