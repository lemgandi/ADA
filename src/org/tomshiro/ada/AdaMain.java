package org.tomshiro.ada;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

public class AdaMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ada_main);
	}
	public void startInput(View view)
	{
		Intent myIntent = new Intent(this,InputActivity.class);
		startActivity(myIntent);
	}
	public void startReport(View view)
	{
		Intent myIntent = new Intent(this,ReportActivity.class);
		startActivity(myIntent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ada_main, menu);
		return true;
	}
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.menu_settings:
    		break;
    	case R.id.menu_exit:
    		this.finish();
    		break;
    	}
    	return true;
    }
 
}
