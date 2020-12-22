/*-----------------------------------------------------------
*  Copyright Richard Abbuhl, 2002-2003
* 
* cputime.java
* R. Abbuhl, 18 April 1990
* %W% %G%
*
* Contains routines which can be used for timing segments
* of C code.  The following is a list of the routines:
*
*  cputime      - return the number of clock ticks since the last
*                 call to cputime.
*  BeginTimer   - begin timing a piece of code.
*  EndTimer     - end timing a piece of code and return the 
*                 elapsed time since the call to BeginTimer()
*                 in seconds.
*  uEndTimer    - end timing a piece of code and return the 
*                 elapsed time since the call to BeginTimer()
*                 in microseconds.
*  TimerReport  - display a message with the elapsed time
*                 in seconds.
*  uTimerReport - display a message with the elapsed time
*                 in microseconds.
*  TimeStamp    - timestamp a file by printing the current 
*                 date and time.
*  ElapsedTime  - display the elapsed time between calls
*                 to TimeStamp() in hours, minutes, and
*                 seconds.
*  IterateTime  - display the average time for an iteration.
*
* Revision History:
*
*     12/01/02, R. Abbuhl   
*     Converted to Java.
*
*     5/06/90, R. Abbuhl
*     Changed the clock ticks per second to be 60
*     for rehost to an IBM RT.
*
*     5/06/90, R. Abbuhl
*     Changed the printf in ElapsedTime so the minutes
*     and seconds were output correctly using zero as the
*     padding character and two digits for the width.
*
*     9/17/92, R. Abbuhl
*     Fixed a bug in IterateTime.  The function difftime
*     returns a double not a long.
*
-------------------------------------------------------------*/

package com.jmentor.jqlearn;

import com.jmentor.jtictactoe.DecimalHelper;

import java.text.DecimalFormat;

public class CPUTime {

   static double CLOCK_TICKS_PER_SECOND = 1e-3;
   static double tbufa = 0;
   static double tbufb = 0;
   static DecimalFormat intfmt = DecimalHelper.defaultDecimalFormat("00");
   static DecimalFormat decfmt = DecimalHelper.defaultDecimalFormat("#0.0000000");

   /*-----------------------------------------------------------
   *
   * cputime - return the number of clock ticks since the last
   *           call to cputim.
   *
   -------------------------------------------------------------*/
   public double getCPUTime()
   {
      double ret;

      tbufb = 1e-3*System.currentTimeMillis();
      ret = (tbufb - tbufa);

      tbufa = tbufb;
      return((double) ret);
   }

   /*-----------------------------------------------------------
   *
   * BeginTimer - begin timing a piece of code.
   *
   -------------------------------------------------------------*/
   public void BeginTimer()
   {
      getCPUTime();
   }

   /*-----------------------------------------------------------
   *
   * EndTimer - end timing a piece of code and return the
   *            elapsed time since the call to BeginTimer()
   *            in seconds.
   *
   -------------------------------------------------------------*/
   public double EndTimer()
   {
      //return((double) cputime() / (double) CLOCK_TICKS_PER_SECOND);
      return(getCPUTime());
   }

   /*-----------------------------------------------------------
   * 
   * uEndTimer - end timing a piece of code and return the 
   *             elapsed time since the call to BeginTimer()
   *             in microseconds.
   *
   -------------------------------------------------------------*/
   public double uEndTimer()
   {
      return((double) getCPUTime() * (1.0e6 / (double) CLOCK_TICKS_PER_SECOND));
   }

   /*-----------------------------------------------------------
   * 
   * TimerReport - display a message with the elapsed time
   *               in seconds.
   *
   -------------------------------------------------------------*/
   public void TimerReport( String msg, double timeval )
   {
      long tval = (long) timeval;
      long aa=3600;
      long bb=60;
      long hours = tval / aa;
      long mins  = (tval % aa) / bb;
      long secs  = (tval % aa) % bb;

      System.out.println(msg + " " +
        intfmt.format(hours) + ":" +  
        intfmt.format(mins)  + ":" + 
        intfmt.format(secs)  + " CPU seconds");
   }

   /*-----------------------------------------------------------
   *
   * uTimerReport - display a message with the elapsed time
   *                in microseconds.
   *
   -------------------------------------------------------------*/
   public void uTimerReport( String msg, double timeval )
   {
      System.out.println(msg + " " + timeval + " microseconds\n");
   }

   /*-----------------------------------------------------------
   *
   * TimeStamp - timestamp a file by printing the current
   *             date and time.
   *
   -------------------------------------------------------------*/
   public long TimeStamp()
   {
      long tval = 0;

      //time(&tval);
      //System.out.println("%s",ctime(&tval));
      tval = (long)(1e-3*System.currentTimeMillis());
      return(tval);
   }

   /*-----------------------------------------------------------
   *
   * ElapsedTime - display the elapsed time between calls
   *               to TimeStamp() in hours, minutes, and
   *               seconds.
   *
   -------------------------------------------------------------*/
   public void ElapsedTime( long time_val1, long time_val2 )
   {
      long tval = time_val2 - time_val1;
      long aa=3600;
      long bb=60;
      long hours = tval / aa;
      long mins  = (tval % aa) / bb;
      long secs  = (tval % aa) % bb;

      System.out.println("Elapsed time " +
        intfmt.format(hours) + ":" +  
        intfmt.format(mins)     + ":" + 
        intfmt.format(secs));
   }

   /*-----------------------------------------------------------
   * 
   * IterateTime - display the average time for an iteration.
   *               to TimeStamp() in hours, minutes, and
   *               seconds.
   *
   -------------------------------------------------------------*/
   public void IterateTime( long time_val1, long time_val2, long iterations )
   {
      double tval = (double)time_val2 - (double)time_val1;
      double spi = (double) tval / (double) iterations;
      System.out.println("Seconds per iteration " + decfmt.format(spi));
   }

}
