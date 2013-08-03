package org.tomshiro.ada;

import android.util.Log;

/*
 * This class takes a printf-type format string, then allows you to throw values at it one at a time via the "feedValue()"
 * method.  It properly handles "%%" -> "%" conversion in format strings, but String feedValues do *not* need "%" converted to 
 * "%%". 
 * Use it to format all the values in an array or some other structure into a single format string.
 */
public class StrFormatLoop
{
   public static String TAG="StrFormatLoop";
   StringBuilder FmtStringEdited;
   int NextFmt=0;
   boolean NoFormats=false;

   /*
    * Init -- do some preprocessing on our string.
    */
   StrFormatLoop(String fmtString)
   {
	   Log.d(TAG,"StrFormatLoop:"+fmtString);
      FmtStringEdited=new StringBuilder(fmtString.replace("%","%%"));
      // Log.d(TAG,"FmtString first: ["+FmtStringEdited+"]");
      set_nextfmt();

      if(-1 == NextFmt)
	 NoFormats=true;
      
   }
   /*
    * Set location of next format string
    */
   private void set_nextfmt()
   {
	   Log.d(TAG,"set_nextfmt");
      if(-1 != NextFmt)
      {
	 int fmtPtr=FmtStringEdited.indexOf("%%",NextFmt);
      
	 if(-1 == fmtPtr)
	    NextFmt=fmtPtr;

	 if(-1 != NextFmt)
	 {
	    while((fmtPtr != -1) &&
		  (fmtPtr < FmtStringEdited.length()-4) && 
		  ("%%%%".equals(FmtStringEdited.substring(fmtPtr,fmtPtr+4))))
	    {
	       fmtPtr += 4;
	       if(FmtStringEdited.length() < fmtPtr)
		  fmtPtr=-1;
	       else
		  fmtPtr=FmtStringEdited.indexOf("%%",fmtPtr);
	    }
	 
	 }
	 if(-1 != fmtPtr)
	    fmtPtr=FmtStringEdited.indexOf("%%",fmtPtr);
	 
	 if((-1 != fmtPtr) && ("%%%%".equals(FmtStringEdited.substring(fmtPtr))) )
	    fmtPtr = -1;

	 NextFmt=fmtPtr;
	 /*
	 if(-1 != NextFmt)
	    Log.d(TAG,"NextFmt["+FmtStringEdited.substring(NextFmt)+"]");
	 else
	    Log.d(TAG,"NextFmt: -1");
	    */
      }
   }

      /*
       * Feed a value to be formatted to the string.
       */
   public boolean feedValue(Object val)
   {
	   Log.d(TAG,"feedValue:"+val.toString());
      boolean retVal = true;

      if(-1 == NextFmt) // No more formats left.
	 retVal=false;
      if(true == retVal)
      {
	 FmtStringEdited.deleteCharAt(NextFmt);
	 if(String.class == val.getClass())
	 {
	    String valStr=(String)val;
	    int nextFmtAddAmt = valStr.length();
	    // Advance internal pointer past "%" in passed string.
	    int pctCt=0;
            int pctPtr=0;
	    while( (pctPtr != -1) && (pctPtr < valStr.length()) )
	    {
	       pctPtr=valStr.indexOf("%",pctPtr);
	       if(pctPtr != -1)
	       {
		  ++pctPtr;
		  ++pctCt;
	       }	       
	    }
	    nextFmtAddAmt += (pctCt*2);
	    NextFmt += nextFmtAddAmt;
	 }
	 String fmtString = new String(FmtStringEdited.toString());
	 String newFmtString= java.lang.String.format(fmtString,val).
	    replace("%","%%");
	 FmtStringEdited.replace(0,FmtStringEdited.length(),newFmtString);
	 set_nextfmt();
      }
      return retVal;
   }

   private String fixPctDouble(String s)
   {
	  Log.d(TAG,"fixPctDouble");
	  
      String retVal = new String(s.replace("%%","%").replace("%%","%"));
      return retVal;
   }
   public String getFormattedString()
   {
	   Log.d(TAG,"getFormattedString");
      String theString = fixPctDouble(FmtStringEdited.toString());
      String retVal;

      return (theString);
   }
}
