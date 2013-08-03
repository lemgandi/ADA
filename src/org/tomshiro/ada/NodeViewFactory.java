package org.tomshiro.ada;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.util.ArrayList;

import android.view.View;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Spinner;
import android.content.Context;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.TimePicker;
import android.util.Log;
import android.util.AttributeSet;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import android.widget.ArrayAdapter;
import java.util.HashMap;


// Given an xml node, construct a view from it

public class NodeViewFactory  {
	static final String TAG = "NodeViewFactory";
//TODO: Future viewTypes: Duration (hh:mm:ss), DateTime (MM-DD-YY hh:mm:ss),Text(mush), id (integer ranged to primary key of a given table)
	
	
	
	// static final int DurationView = 2;   //Etc
	Context myContext = null;
	AdaDB myDbHelper = null;
	Resources myResources=null;
	ViewFiller myDomainFiller = null;
	AttributeSet linearLayoutAttribs;
	// Used only by makeSlot.  Cannot put it inside the method body. Bummer.
	private static int last_assigned_slot_id=0;
	private static int last_assigned_view_id=0;
	private ViewTypeParser VTypeParser;
	
	public NodeViewFactory(Context theContext,AdaDB theHelper,Resources r)
{
	Log.d(TAG,"NodeViewFactory");
	myContext = theContext;
	myDbHelper = theHelper;
	myResources=r;
	myDomainFiller = new ViewFiller();
	VTypeParser = new ViewTypeParser();
	this.fillAttribs();
}

private void fillAttribs()
{
   XmlResourceParser pullParser = myResources.getLayout(R.layout.report_input_element);
   int state = 0;
   do {
       try {
           state = pullParser.next();
       } catch (Exception e1) {
           e1.printStackTrace();
       }       
       if (state == XmlPullParser.START_TAG) {
           if (pullParser.getName().equals("LinearLayout")) {
               linearLayoutAttribs = Xml.asAttributeSet(pullParser);
               break;
           }
       }
   } while(state != XmlPullParser.END_DOCUMENT);
}
/*
 * Make container for input
 */
private LinearLayout makeSlot(int height)
{	
	LinearLayout retVal = new LinearLayout(myContext,linearLayoutAttribs);
	// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)retVal.getLayoutParams();
	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myContext,linearLayoutAttribs);
	if(height != 0)
		params.height=height;
	Log.d(TAG,"makeSlot: params="+params);
	Log.d(TAG,"makeSlot: last_assigned_slot_id="+last_assigned_slot_id);
	
	if(0 == last_assigned_slot_id)
	{		
		last_assigned_slot_id=R.id.FirstInputSlotID;
		retVal.setId(R.id.FirstInputSlotID);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);		
	}
	else
	{
		params.addRule(RelativeLayout.BELOW,last_assigned_slot_id);
		// Add arbitrary constant avoid conflicts with other ids
		last_assigned_slot_id=last_assigned_slot_id+8251;
		retVal.setId(last_assigned_slot_id);		
	}
	retVal.setLayoutParams(params);
	
	return retVal;
}


private View makeDurationView(String label,String name,int height)
{
	Log.d(TAG,"makeDurationView");
	View retVal = null;
	LinearLayout myLayout = this.makeSlot(height);
	TextView labelText = new TextView(myContext);
	labelText.setTag(R.id.ViewTypeTag,Integer.valueOf(ViewTypeParser.LabelView));
	labelText.setText(label);
	myLayout.addView(labelText);
	TimePicker timePickerView = new TimePicker(myContext);
	timePickerView.setIs24HourView(true);
	this.assignID(timePickerView, name, ViewTypeParser.InputDurationView);
	myLayout.addView(timePickerView);
	retVal = myLayout;
	return retVal;
	
}
private View makeDomainView(int type,String label,String name,int height)
{
	Log.d(TAG,"makeDomainView");
	View retVal = null;
	LinearLayout myLayout=this.makeSlot(height);	
	TextView labelText = new TextView(myContext);
	labelText.setTag(R.id.ViewTypeTag,Integer.valueOf(ViewTypeParser.LabelView));
	labelText.setText(label);
	myLayout.addView(labelText);
	Spinner domainSpinner = new Spinner(myContext);
	int fillType = 0;
	switch(type)
	{
	case ViewTypeParser.InputActionView:
		fillType = ViewFiller.ACTIONLIST;
		break;
	default:
		fillType = ViewFiller.FENCERLIST;
		break;		
	}
	this.assignID(domainSpinner,name,type);
	myDomainFiller.fill_view(domainSpinner,fillType,myDbHelper,myContext);
	myLayout.addView(domainSpinner);
	retVal = myLayout;
	return retVal;
}
private void assignID(View v,String name,int viewType)
{
	Log.d(TAG,"assignID");
	if(0 == last_assigned_view_id)
		last_assigned_view_id=R.id.FirstInputViewID;
	else // Add arbitrary constant to avoid conflicts with other ids.
		last_assigned_view_id=last_assigned_view_id+2163;
	
	Log.d(TAG,"assignID: last_assigned_view_id="+last_assigned_view_id);
	
	v.setId(last_assigned_view_id);
	v.setTag(R.id.ViewNameTag,name);
	v.setTag(R.id.ViewTypeTag,Integer.valueOf(viewType));
}

private View makeIntegerView(String label,String name,int limit,int height)
{
	View retVal = null;
	LinearLayout myLayout=this.makeSlot(height);
	TextView labelText=new TextView(myContext);
	labelText.setText(label);
	labelText.setTag(R.id.ViewTypeTag,Integer.valueOf(ViewTypeParser.LabelView));
	myLayout.addView(labelText);
	
	//NumberPicker added API 11
	Spinner numberSpinner = new Spinner(myContext);
	ArrayList<String>numberList=new ArrayList<String>(limit);
	for(int kk=0;kk<limit;++kk)
		numberList.add(Integer.toString(kk));
	ArrayAdapter<String> numberSpinnerAdapter = new ArrayAdapter<String>(myContext,android.R.layout.simple_spinner_dropdown_item,
			numberList.toArray(new String[0]));
	numberSpinner.setAdapter(numberSpinnerAdapter);
	this.assignID(numberSpinner,name,ViewTypeParser.InputIntegerView);
	myLayout.addView(numberSpinner);
	retVal=myLayout;
	return retVal;
	
}
private View makeBooleanView(String label,String name,int height)
{
	View retVal = null;
	LinearLayout myLayout=this.makeSlot(height);
	TextView labelText = new TextView(myContext);
	labelText.setTag(R.id.ViewTypeTag,Integer.valueOf(ViewTypeParser.LabelView));
	labelText.setText(label);
	myLayout.addView(labelText);
	RadioButton myCheckBox = new RadioButton(myContext);
	this.assignID(myCheckBox,name,ViewTypeParser.InputBooleanView);
	myLayout.addView(myCheckBox);
	retVal=myLayout;
	return retVal;
}

private View makeSubmitQuit()
{
	Log.d(TAG,"makeSubmitQuit");
	View retVal = null;
	
	LinearLayout myLayout=this.makeSlot(0);
	Button submitButton = new Button(myContext,linearLayoutAttribs);
	submitButton.setText("Report");
	
	Button quitButton = new Button(myContext,linearLayoutAttribs);
	quitButton.setText("Quit");
	Log.d(TAG,"makeSubmitQuit: buttons created");
	
	RelativeLayout.LayoutParams alignParams;
	
	// this.assignID(submitButton,"Submit",0);
	alignParams = new android.widget.RelativeLayout.LayoutParams(R.dimen.input_element_hsize,R.dimen.input_element_vsize);
	alignParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	submitButton.setLayoutParams(alignParams);
	submitButton.setId(R.id.SubmitButton);
	Log.d(TAG,"makeSubmitQuit: submitbutton layout");
	
	alignParams = new android.widget.RelativeLayout.LayoutParams(R.dimen.input_element_hsize,R.dimen.input_element_vsize);
	alignParams.addRule(RelativeLayout.RIGHT_OF,submitButton.getId());
	quitButton.setLayoutParams(alignParams);
	quitButton.setId(R.id.QuitButton);
	Log.d(TAG,"makeSubmitQuit: quitbutton layout");
	
	myLayout.addView(submitButton);
	myLayout.addView(quitButton);
	retVal=myLayout;
	Log.d(TAG,"makeSubmitQuit: return");
	
	return retVal;
}

public void setLabels(NodeList labelNodes,Activity theActivity)
{
	Log.d(TAG,"setLabels");
	Element theElement;	
	int elemType = ViewTypeParser.LabelView;
	
	for(int ii = 0; ii<labelNodes.getLength(); ++ii)
	{
		theElement = (Element)labelNodes.item(ii);
		elemType = VTypeParser.getViewType(theElement.getAttribute("type"));
		switch(elemType)
		{
		case ViewTypeParser.LabelHeader:
			theActivity.setTitle(theElement.getAttribute("text"));
			break;
		}
		
	}
}
private View makeViewFromNode(Node theNode)
{
	Log.d(TAG,"makeViewFromNode");
	View retVal = null;
	
	Log.d(TAG,"Node name:"+theNode.getNodeName());
	Element theElement = (Element)theNode;
	
	Log.d(TAG,"Node type:"+theElement.getAttribute("type"));
	Log.d(TAG,"Node label:"+theElement.getAttribute("label"));
	Log.d(TAG,"Node seq:"+theElement.getAttribute("seq"));
	Log.d(TAG,"Node name:"+theElement.getAttribute("name"));
    int viewType = 0;
    
    int viewHeight=0;
    if(theElement.hasAttribute("height"))    
    	viewHeight = Integer.parseInt(theElement.getAttribute("height"));
    
    viewType = VTypeParser.getViewType(theElement.getAttribute("type"));
    
    switch(viewType)
    {
    case ViewTypeParser.InputActionView:
    case ViewTypeParser.InputFencerView:
    	retVal = makeDomainView(viewType,theElement.getAttribute("label"),theElement.getAttribute("name"),viewHeight);
    	break;
    case ViewTypeParser.InputBooleanView:
    	retVal=makeBooleanView(theElement.getAttribute("label"),theElement.getAttribute("name"),viewHeight);
    	break;
    case ViewTypeParser.InputIntegerView:
    	int limit=99;
    	if(theElement.hasAttribute("limit"))
    	{
    		String limitStr=theElement.getAttribute("limit");
    		limit=Integer.parseInt(limitStr);	
    	}
    	retVal=makeIntegerView(theElement.getAttribute("label"),theElement.getAttribute("name"),limit,viewHeight);
    	break;
    case ViewTypeParser.InputDurationView:
    	retVal=makeDurationView(theElement.getAttribute("label"),theElement.getAttribute("name"),viewHeight);
    	break;
    }
	return retVal;
}


public Object parseParm(Node n,HashMap<String,Object>theMap)
{
	Element theElement = (Element)n;
	String refStr = theElement.getAttribute("ref");
	String valStr=(String)theMap.get(refStr);
	ViewTypeParser VTP = new ViewTypeParser();
	int parmType=VTP.getParmType(theElement.getAttribute("otype"));
	Object retVal=null;
	
	switch(parmType)
	{
	case ViewTypeParser.StringType:
		retVal=valStr;
		break;
	case ViewTypeParser.IntegerType:
		retVal=Integer.valueOf(valStr);
		break;
	case ViewTypeParser.DoubleType:
		retVal=Double.valueOf(valStr);
		break;
	case ViewTypeParser.FloatType:
		retVal=Float.valueOf(valStr);
		break;
	case ViewTypeParser.LongType:
		retVal=Long.valueOf(valStr);
		break;
	case ViewTypeParser.SecsType:
		retVal=Long.valueOf(valStr);
	}
	return retVal;
}
	// Main entry point
public ArrayList<View> makeViews(NodeList viewNodes) 
{
	Log.d(TAG,"makeViews");
	ArrayList<View> retVal = new ArrayList<View>(viewNodes.getLength());
	int success = 0;
	Log.d(TAG,"makeViews viewNodes len="+viewNodes.getLength());
	if(0 == success) 
	{
		for(int ii=0; ii<viewNodes.getLength();++ii)
		{
			retVal.add(makeViewFromNode(viewNodes.item(ii)));
		}
		retVal.add(makeSubmitQuit());
	}
	return retVal;
}


} // NodeViewFactory
