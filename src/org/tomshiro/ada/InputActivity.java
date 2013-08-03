
package org.tomshiro.ada;

import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.MenuItem;



public class InputActivity extends Activity {
		static final String TAG= "InputActivity" ;
		static final int ClearableFields[] = { 
			 R.id.left_score_checkbox,
			 R.id.right_score_checkbox,
			 R.id.Left_action,
			 R.id.Right_action
		};
		static enum touch_scored { RIGHT,LEFT,BOTH,NONE};
		
		AlertDialog.Builder troubleDialogBuilder;
		AdaDB myDbHelper;
		int current_bout_id=0;
		int touch_sequence = 0; // sqlite3 does not have sequences. Bummer.
		static final String curBoutID="CurrentBoutID";
		static final String curTouchSeq="CurrentTouchSequence";
		static final String current_L = "currentLeftScore"; // Current left score, for resume (e.g. when phone is turned)
		static final String current_R = "currentRightScore";
		
		/*
		 * Load a touch-type spinner from database.
		 */
		
		/*
		 * Store off scores for redisplay.
		 */
		public void onSaveInstanceState(Bundle savedInstanceState)
		{
			super.onSaveInstanceState(savedInstanceState);
			CharSequence score_data;
			Integer current_score_num;
			TextView scoreMeter;
			
			scoreMeter=(TextView)findViewById(R.id.DisplayScoreLeft);
			score_data = scoreMeter.getText();
			current_score_num=Integer.parseInt(score_data.toString());
			savedInstanceState.putInt(current_L,current_score_num);
			scoreMeter=(TextView)findViewById(R.id.DisplayScoreRight);
			score_data = scoreMeter.getText();
			current_score_num = Integer.parseInt(score_data.toString());
			savedInstanceState.putInt(current_R,current_score_num);
			savedInstanceState.putInt(curBoutID,this.current_bout_id);
			savedInstanceState.putInt(curTouchSeq,this.touch_sequence);
		}
		/*
		 * Re-read choices from db; redisplay scores stored in onPause.
		 */
		public void onResume()
		{
			super.onResume();
			Log.d(TAG,"onResume");
			
			this.load_fields();
		
			
		}
		/*
		 * Load fields from database.
		 */
		private void load_fields()
		{
			ViewFiller myFiller = new ViewFiller();
			
			myFiller.fill_view(findViewById(R.id.Left_action), ViewFiller.ACTIONLIST,myDbHelper,this);
			myFiller.fill_view(findViewById(R.id.Right_action),ViewFiller.ACTIONLIST,myDbHelper,this);

			myFiller.fill_view(findViewById(R.id.Left_name),ViewFiller.FENCERLIST,myDbHelper,this);
			myFiller.fill_view(findViewById(R.id.Right_name),ViewFiller.FENCERLIST,myDbHelper,this);
		}
		/*
		 * Build dialog from xml 
		*/
		protected void onCreate(Bundle savedInstanceState) 
		{
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_ada_input);
			this.myDbHelper = new AdaDB(this);
			this.troubleDialogBuilder = new AlertDialog.Builder(this); 
			
			this.load_fields();
			this.reset_score(savedInstanceState);
			this.handleInternalGlobals(savedInstanceState);
		}
		/*
		 * Our globals should not go away if we resume.
		 */
		private void handleInternalGlobals(Bundle savedInstanceState)
		{
			if(null != savedInstanceState)
			{
				this.current_bout_id = savedInstanceState.getInt(curBoutID);
				this.touch_sequence = savedInstanceState.getInt(curTouchSeq);
			}
			else
			{
				// While entering a bout, these fields are locked. 
				View NameBox=findViewById(R.id.Left_name);
				NameBox.setEnabled(true);
				NameBox=findViewById(R.id.Right_name);
				NameBox.setEnabled(true);
		
				current_bout_id = 0;
				touch_sequence = 0;
			}
		}
		/*
		 * Finish a bout
		 */
		private void finish_bout()
		{
			myDbHelper.closeBout(current_bout_id);
		
		}
		private boolean start_new_bout()
		{
			boolean retVal = true;
			
			String fencerNameL = ((ComboBox)findViewById(R.id.Left_name)).getText();
			String fencerNameR = ((ComboBox)findViewById(R.id.Right_name)).getText();
			int LorR = 0;
			
			if((0 == fencerNameL.length()) && (0 == fencerNameR.length()) )
			{
				LorR=R.string.BothFencerMissingMessage;
				retVal = false;
			}
			if((true == retVal) && (0 == fencerNameL.length())) 
			{
				LorR=R.string.LeftFencerMissingMessage;
				retVal=false;
			}
			if((true == retVal) && (0 == fencerNameR.length()))
			{
				LorR=R.string.RightFencerMissingMessage;
				retVal=false;				
			}
			if(false == retVal)
			{
				troubleDialogBuilder.setTitle(R.string.NoFencerTitle);
				troubleDialogBuilder.setMessage(LorR);
				AlertDialog noFencerAlert=troubleDialogBuilder.create();
				noFencerAlert.show();
			}
			if(true == retVal)
			{				
				myDbHelper.find_add_fencer(fencerNameL);			
				myDbHelper.find_add_fencer(fencerNameR);
				
				View NameBox=findViewById(R.id.Left_name);
				NameBox.setEnabled(false);
				NameBox=findViewById(R.id.Right_name);
				NameBox.setEnabled(false);
				this.handleInternalGlobals(null);
				this.createBout();
			}
			return retVal;
		}
		/*
		 * Start a new bout
		 */
		/*
		 * Handle options
		 */
		public boolean onOptionsItemSelected(MenuItem item)
		{
		   super.onOptionsItemSelected(item);
		   boolean retVal=false;
		   
		   switch(item.getItemId())
		   {
		   case R.id.New_bout:
			   this.finish_bout();
			   this.clear_all_state();
			   retVal=true;
			   break;
		   case R.id.Finish_bout:
			   this.finish_bout();
			   this.finish();
			   retVal=true;
			   break;
		   case R.id.Start_bout:
			   retVal=true;
			   retVal=this.start_new_bout();
			   break;
		   }
		   if(true == retVal)
			   return retVal;
		   else
			   return super.onOptionsItemSelected(item);
		}
		/*
		 * Reset scores as needed.
		 */
		private void reset_score(Bundle savedInstanceState)
		{
			TextView scoreMeter;
			CharSequence lScoreText;
			CharSequence rScoreText;
			if(null == savedInstanceState)
			{
				lScoreText = "0";
				rScoreText = "0";
			}
			else
			{
				int theScore = savedInstanceState.getInt(current_L);
				lScoreText = Integer.toString(theScore);
				theScore = savedInstanceState.getInt(current_R);
				rScoreText = Integer.toString(theScore);
			}
			scoreMeter=(TextView)findViewById(R.id.DisplayScoreLeft);
			scoreMeter.setText(lScoreText);
			scoreMeter=(TextView)findViewById(R.id.DisplayScoreRight);
			scoreMeter.setText(rScoreText);			
		}
		/*
		 *  Inflate the menu; this adds items to the action bar if it is present.
		 */
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.new_finish, menu);
			return true;
		}
		
		
		/*
		 * Are we entering the first touch?
		 * If the score is 0 to 0, we are entering the first touch. 
		 
		private boolean isFirstTouch()
		{
			Log.d(TAG,"isFirstTouch");
			boolean retVal = false;
			TextView scoreTextView = (TextView)findViewById(R.id.DisplayScoreLeft);
			CharSequence current_score = scoreTextView.getText();
			Integer score_num=Integer.parseInt(current_score.toString());
			scoreTextView = (TextView)findViewById(R.id.DisplayScoreRight);
			current_score = scoreTextView.getText();
			score_num = score_num + Integer.parseInt(current_score.toString());
			if(0 == score_num) 
				retVal = true;
			Log.d(TAG,"isFirstTouch: retVal="+retVal);
			return retVal;
		}
		*/
		
		
		/*
		 * Create the Bout record for this bout.
		 */
		private void createBout()
		{
			String fencerNameL = ((ComboBox)findViewById(R.id.Left_name)).getText();
			String fencerNameR = ((ComboBox)findViewById(R.id.Right_name)).getText();
			current_bout_id = myDbHelper.createBout(fencerNameL,fencerNameR);					
		}
		/*
		 * Insert Touch table record(s).
		 */
		private void insert_touches(touch_scored whoWon)
		{
		  Log.d(TAG,"insert_touches");
		  boolean lTouchScored=false;
		  boolean rTouchScored=false;
		  int lTouchActionSeq=Integer.MAX_VALUE;
		  int rTouchActionSeq=Integer.MAX_VALUE;
		  
		  switch(whoWon)
		  {
		  case LEFT:
			  lTouchScored=true;
			  break;
		  case RIGHT:
			  rTouchScored=true;
			  break;
		  default:
			  lTouchScored=true;
			  rTouchScored=true;
			  break;
		  }
		  touch_sequence = touch_sequence+1;
		  
		  lTouchActionSeq=((Spinner)findViewById(R.id.Left_action)).getSelectedItemPosition();
		  rTouchActionSeq=((Spinner)findViewById(R.id.Right_action)).getSelectedItemPosition();
		  Log.d(TAG," lTouchScored:"+lTouchScored+" rTouchScored:"+
		  rTouchScored+" lTouchActionSeq:"+lTouchActionSeq+" rTouchActionSeq:"+rTouchActionSeq+
		  " touch_sequence:"+touch_sequence);
		  myDbHelper.add_touch(current_bout_id,rTouchScored,lTouchScored,rTouchActionSeq,lTouchActionSeq,touch_sequence);
		}
		/*
		 * Which side scored a touch?
		 */
		private touch_scored who_scored()
		{
			Log.d(TAG,"which_scored");
			
			boolean touch_left=false;
			boolean touch_right=false;
			
			touch_scored retVal=touch_scored.NONE;
			if (((CheckBox)findViewById(R.id.left_score_checkbox)).isChecked())
				touch_left = true;
			if (((CheckBox)findViewById(R.id.right_score_checkbox)).isChecked())
				touch_right=true;
			if((true == touch_right) && (true == touch_left))
				retVal=touch_scored.BOTH;
			if((touch_scored.NONE == retVal) && (touch_right == true))
				retVal=touch_scored.RIGHT;
			if((touch_scored.NONE == retVal) && (touch_left == true))
				retVal=touch_scored.LEFT;
			
			return retVal;
		}
		/*
		 * When commit is pressed, check that form is okay.  If so, increment scores 
		 * and ready for next touch to be entered.
		 */
		public void onCommitClick(View v)
		{
			int status=0;
			
			if(0 == current_bout_id)
			{
				troubleDialogBuilder.setTitle(R.string.NoBoutTitle);
				troubleDialogBuilder.setMessage(R.string.NoBoutMessage);
				AlertDialog noBoutAlert=troubleDialogBuilder.create();
				noBoutAlert.show();
				status = -1;
			}
			touch_scored whoScored = who_scored();
			//If user has not entered a touch, then we have a Trouble. Inform her.
			if((0 == status ) && (touch_scored.NONE == whoScored))
			{
				troubleDialogBuilder.setTitle(R.string.NoScoreTitle);
				troubleDialogBuilder.setMessage(R.string.NoScoreMessage);
				AlertDialog noScoreAlert=troubleDialogBuilder.create();
				noScoreAlert.show();
				status=-1;
			}
			// If user has not entered both action fields, we have a Trouble. Inform her.
			if(0 == status)
			{
				int whichMessage=Integer.MAX_VALUE;
				int lTouchActionSeq=((Spinner)findViewById(R.id.Left_action)).getSelectedItemPosition();
				int rTouchActionSeq=((Spinner)findViewById(R.id.Right_action)).getSelectedItemPosition();
				if ((0 == lTouchActionSeq) && (0 ==rTouchActionSeq))
				   whichMessage=R.string.BothActionNotEntered;
				if(Integer.MAX_VALUE == whichMessage)
				{
					if(0 == lTouchActionSeq)
						whichMessage=R.string.LeftActionNotEntered;
				}
				if(Integer.MAX_VALUE == whichMessage)
				{
					if(0 == rTouchActionSeq)
						whichMessage=R.string.RightActionNotEntered;
				}
				if(Integer.MAX_VALUE != whichMessage)
				{
					troubleDialogBuilder.setTitle(R.string.NoActionTitle);
					troubleDialogBuilder.setMessage(whichMessage);
					AlertDialog noActionAlert=troubleDialogBuilder.create();
					noActionAlert.show();
					status = -1;
				}
			}
			// Increment display score as needed, write info to database, clear screen input areas.
			if(0 == status)
			{ 
		    switch(whoScored)
		    {
			case LEFT:
				this.inc_score(R.id.DisplayScoreLeft);
				break;
			case RIGHT: //R.id.right_score_checkbox
				this.inc_score(R.id.DisplayScoreRight);
				break;
			default:
				this.inc_score(R.id.DisplayScoreRight);
				this.inc_score(R.id.DisplayScoreLeft);
				break;
		    }
				this.insert_touches(whoScored);
				this.clear_input_state();			
			}		
		}
		/*
		 * Ready form for a new touch.
		 */
		private void clear_input_state()
		{
			View tempView;
			
			//Called when we determine clear button is pressed.
			for(int kk=0; kk < InputActivity.ClearableFields.length; ++kk)
			{
				tempView = findViewById(InputActivity.ClearableFields[kk]);
				
				if ( tempView.getClass().equals(CheckBox.class) )
					((CompoundButton) tempView).setChecked(false);
				else if (tempView.getClass().equals(Spinner.class))
					( (Spinner)tempView ).setSelection(0);
			}		
		}
		/*
		 * Ready form for a new bout.
		 */
		private void clear_all_state()
		{
			this.clear_input_state();
			this.reset_score(null);
			this.touch_sequence = 0;
			ComboBox fencerNameBox;
			fencerNameBox = (ComboBox)findViewById(R.id.Left_name);
			fencerNameBox.setText("");
			fencerNameBox = (ComboBox)findViewById(R.id.Right_name);
			fencerNameBox.setText("");
		}
		
		/*
		 * Decrement score display
		 */
		private void dec_score(int score_id)
		{
			TextView scoreTextView = (TextView)findViewById(score_id);
			CharSequence current_score = scoreTextView.getText();
			Integer current_score_num=Integer.parseInt(current_score.toString());
			if(current_score_num > 0)
				current_score_num = current_score_num - 1;
			current_score=current_score_num.toString();
			scoreTextView.setText(current_score);
		}
		/* 
		 * Increment score display
		 */
		private void inc_score(int score_id)
		{
			TextView scoreTextView = (TextView)findViewById(score_id);
			CharSequence current_score = scoreTextView.getText();
			Integer current_score_num=Integer.parseInt(current_score.toString());
			current_score_num = current_score_num + 1;
			current_score=current_score_num.toString();
			scoreTextView.setText(current_score);
		}
		
		public void onClearClick(View v)
		{
			CheckBox theCheckBox;
			
			theCheckBox=(CheckBox)findViewById(R.id.left_score_checkbox);
			if (theCheckBox.isChecked())
				this.dec_score(R.id.DisplayScoreLeft);
			theCheckBox=(CheckBox)findViewById(R.id.right_score_checkbox);
			if(theCheckBox.isChecked())
				this.dec_score(R.id.DisplayScoreRight);
			this.clear_input_state();
				
		}
}
