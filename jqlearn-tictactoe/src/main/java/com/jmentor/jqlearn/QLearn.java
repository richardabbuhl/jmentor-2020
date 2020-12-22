// Copyright 2002, Richard Abbuhl

package com.jmentor.jqlearn;

import com.jmentor.jtictactoe.DecimalHelper;
import com.jmentor.jtictactoe.Player;
import com.jmentor.jtictactoe.Random;
import com.jmentor.jtictactoe.Board;
import org.apache.commons.math3.random.MersenneTwister;

import java.io.*;
import java.text.DecimalFormat;

public class QLearn extends Player {

   double alpha;
   double epsilon;
   double MaxOutput;
   double MinOutput;
   boolean disp_out;
   double [] qvalues;
   int nqvalues;

   Random random;
   MersenneTwister rg;

   static DecimalFormat decfmt  = DecimalHelper.defaultDecimalFormat("#0.0000000");

   public QLearn()
   {
      MaxOutput = -1000.0;
      MinOutput = 1000.0;
      alpha = 0.10;
      epsilon = 0.00;
      nqvalues = getPower(3, 9);
      disp_out = false;
      random = new Random();
      rg = new MersenneTwister(13);

      qvalues = new double[nqvalues];
      for (int i = 0; i < nqvalues; i++) {
         qvalues[i] = 0.0;
      }
   }

   public double GetMin()
   {
      return( MinOutput );
   }

   public double GetMax()
   {
      return( MaxOutput );
   }

   public void set_display_outputs( boolean display_outputs )
   {
      disp_out = display_outputs;
   }

   public void setAlpha(double value)
   {
     alpha = value;
   }

   public double getAlpha()
   {
     return( alpha );
   }

   public void setEpsilon(double value)
   {
     epsilon = value;
   }

   public double getEpsilon()
   {
     return( epsilon );
   }

   public void SaveMinMax( double result )
   {
      if (result < MinOutput) {
         MinOutput = result;
      }
      if (result > MaxOutput) {
         MaxOutput = result;
      }
   }

   /* random integer generator, uniform */
   public int Pick_Value(int i, int n)
   {
      /* return an integer in i, i+1, ... n */
      return( rg.nextInt( n + 1 ) );
   }

   public int getIndex(Board board)
   {
      int theIndex = 0;
      for (int i = 0; i < Board.BOXSIZE; i++) {
         theIndex += board.Get(i) * getPower(i, 3);
      }
      if (theIndex < 0 || theIndex >= nqvalues) {
         System.out.println("theIndex = " + theIndex);
      }
      return( theIndex );
   }

   public int getIndex(int [] board)
   {
      int theIndex = 0;
      for (int i = 0; i < Board.BOXSIZE; i++) {
         theIndex += board[i] * getPower(i, 3);
      }
      if (theIndex < 0 || theIndex >= nqvalues) {
         System.out.println("theIndex = " + theIndex);
      }
      return( theIndex );
   }

   public int getPower( int i, int n )
   {
     return( (int)Math.pow( i, n ) );
   }

   public void save(String pName)
   {
      try {

         /* Open the NTF file for writing */
         //FileOutputStream pfd = new FileOutputStream(pName);
         //DataOutputStream file = new DataOutputStream(pfd);
         FileWriter pfd = new FileWriter(pName);
         BufferedWriter file = new BufferedWriter(pfd);

         for (int i = 0; i < nqvalues; i++) {
            //file.writeDouble(qvalues[i]);
            file.write(decfmt.format(qvalues[i]));
            file.newLine();
         }

         /* Close the file */
         file.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

    private InputStream getResourceAsStream(String name) throws IOException {
      InputStream result = getClass().getResourceAsStream(name);
      if (result == null) {
         result = getClass().getClassLoader().getResourceAsStream(name);
         if (result == null) {
            result = new FileInputStream(name);
         }
      }
      return result;
   }


   public void load(String pName)
   {
      try {

         /* Open the NTF file for writing */
         //FileInputStream pfd = new FileInputStream(pName);
         //DataInputStream file = new DataInputStream(pfd);
         InputStream pfd = getResourceAsStream(pName);
         BufferedReader file = new BufferedReader(new InputStreamReader(pfd));

         for (int i = 0; i < nqvalues; i++) {
            qvalues[i] = Double.parseDouble(file.readLine());
         }

         /* Close the file */
         file.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   public double getValue( Board curb )
   {
      int cur_Index;
      double result;

      cur_Index = getIndex( curb );
      return( qvalues[cur_Index] );
   }

   public double getValue(int [] curb)
   {
      int cur_Index;
      double result;

      cur_Index = getIndex( curb );
      return( qvalues[cur_Index] );
   }

   public void Learn( Board prevb, Board curb, boolean isTerminal, double finalValue )
   {
      int prev_Index;
      int cur_Index;

      if (isTerminal) {

         cur_Index = getIndex( curb );
         qvalues[cur_Index] = finalValue;

         //printf("cur_Index = %d, %8.6lf\n", cur_Index, qvalues[cur_Index]);

      } else {
      
         prev_Index = getIndex( prevb );
         cur_Index = getIndex( curb );
         qvalues[prev_Index] += alpha * (qvalues[cur_Index] - qvalues[prev_Index]);

         //printf("prev_Index = %d, %8.6lf, ", prev_Index, qvalues[prev_Index]);
         //printf("cur_Index = %d, %8.6lf\n", cur_Index, qvalues[cur_Index]);
      }
   }

   public int Move( Board b, int value )
   {
      int [] saved = new int[Board.BOXSIZE];
      int count = 0;
      double lowest = 10001.0;
      double highest = -10001.0;
      double new_value;
      int cur_Index;
      int j = -1;

      if (rg.nextDouble() < (1.0 - epsilon)) {

         // Read all of the output patterns from the input file.
         for (int i = 0; i < Board.BOXSIZE; i++) {

            // Try out the results of this move.
            if (b.Get( i ) == 0) {
         
               // Call the network for evaluation.
               Board next = (Board)b.clone();
               next.Set(i, value);
               cur_Index = getIndex(next);
               new_value = qvalues[cur_Index];

               if (disp_out) System.out.println("(" + i + ") new_value = " + decfmt.format(new_value));

               if (value == 1) {
                  if (new_value > highest) {
                     highest = new_value;
                     count = 0;
                     saved[count++] = i;
                     if (disp_out) System.out.println("1. " + i + " " + 
                        decfmt.format(new_value) + " " + decfmt.format(highest));

                  } else if (Math.abs(new_value - highest) < 0.00001) {
                     highest = new_value;
                     saved[count++] = i;
                     if (disp_out) System.out.println("2. " + i + " " + 
                        decfmt.format(new_value) + " " + decfmt.format(highest));
                  }

               } else if (value == 2) {
                  if (new_value < lowest) {
                     lowest = new_value;
                     count = 0;
                     saved[count++] = i;
                     if (disp_out) System.out.println("1. " + i + " " + 
                        decfmt.format(new_value) + " " + decfmt.format(lowest));

                  } else if (Math.abs(new_value - lowest) < 0.00001) {
                     lowest = new_value;
                     saved[count++] = i;
                     if (disp_out) System.out.println("2. " + i + " " + 
                        decfmt.format(new_value) + " " + decfmt.format(lowest));
                  }

               } else {
                  System.out.println("Something is wrong with " + value);
               }

               // Save the extreme value.
               SaveMinMax( new_value );
            }
         }

         // Get an index within the saved values.
         int x = Pick_Value(0, count - 1);
         if (disp_out) System.out.print("count = " + count + ", x = " + x + " " + saved[x]);
         b.Set( saved[x], value );
         b.IncrementMove();
         if (disp_out) System.out.println();

         // Return the move.
         return( saved[x] );

      } else {

         // Make a random move.
         int move = random.Move( b, value );

         // Return the move.
         return( move );
      }
   }
}
