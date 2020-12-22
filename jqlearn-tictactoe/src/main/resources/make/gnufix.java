/*--------------------------------------------------------
*  Copyright Richard Abbuhl, 2002-2003
*
*  gnufix.java
*
*   This file contains a workaround for runtime error 
*  on cygwin.
*
--------------------------------------------------------*/

public class gnufix {

   /* Workaround for runtime error on cygwin */
   private static Class c1 = gnu.java.locale.Calendar.class;

}
