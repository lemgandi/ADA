package org.tomshiro.ada;

public class ViewTypeParser {
	// Given a type string, return an int corresponding to it.
	static String[] InputViewTypes = {"Fencer","Action","Integer","Boolean","Header","SubHeader","InfoHeader","Interval",
		"Text","Id"};
	static final int InputFencerView = 0;
	static final int InputActionView = 1;
	static final int InputIntegerView = 2;
	static final int InputBooleanView = 3;
	static final int LabelHeader = 4;
	static final int LabelSubHeader = 5;
	static final int InfoHeader = 6;
	static final int InputIntervalView = 7;
	static final int InputTextView=8;
	static final int InputIdView = 9;
	
	static String[] InputParmTypes={"String","Integer","Float","Double","Long"};
	static final int StringType=0;
	static final int IntegerType=1;
	static final int FloatType=2;
	static final int DoubleType=3;
	static final int LongType=4;
	

	static String[] InputIntervalTypes = {"Years","Months","Weeks","Days","Hours","Minutes","Seconds"};
	
	// This is the integer which ReportDisplayActivity uses to distinguish the label widget from the
	// input widget when it is getting information from an ad-hoc form.
	static final int LabelView = 1958; 
	
	
	
	
	
	
	public ViewTypeParser()
	{
		// Nothing yet.
	}
	int getIntervalType(String name)
	{
	   int retVal=0;
	   while(retVal < InputIntervalTypes.length)
	   {
		   if(InputIntervalTypes[retVal].equals(name))
			   break;
		   ++retVal;
	   }
	   return retVal;
	}
	/*
	 * What kind of view is this?
	 */
	int getViewType(String name)
	{
		int retVal=0;
		while(retVal < InputViewTypes.length)
	    {
	    	if(InputViewTypes[retVal].equals(name))
	    		break;
	    	++retVal;
	    }
		return retVal;
	}
	
	/*
	 * What kind of parameter is this?
	 */
	int getParmType(String parm)
	{
		int retVal=0;
		while(retVal < InputParmTypes.length)
		{
			if(InputParmTypes[retVal].equals(parm))
				break;
			++retVal;
		}
		return retVal;
	}
}
