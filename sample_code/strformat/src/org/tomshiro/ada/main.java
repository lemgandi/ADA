package org.tomshiro.ada;

import java.util.*;

public class main {
   public static void main(String [] args) {
      StrFormatLoop myFmtLoop= new StrFormatLoop(args[0]);
      myFmtLoop.feedValue(new String("%Charles"));
      myFmtLoop.feedValue(new Integer(2289));
      myFmtLoop.feedValue(new Float(129.2992));
      myFmtLoop.feedValue(new String("%boof%"));
      System.out.println("FINISHED:"+myFmtLoop.getFormattedString());
   }
}
