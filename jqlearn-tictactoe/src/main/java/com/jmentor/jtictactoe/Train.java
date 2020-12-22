// Copyright 1994, Richard Abbuhl

package com.jmentor.jtictactoe;

import java.text.DecimalFormat;

public class Train extends Player {

   double MaxOutput;
   double MinOutput;

   static double ZEROS      = 0.1;
   static double ONES       = 0.2;
   static double TWOS       = 0.3;
   static double BLOCKS     = 0.3;
   static double SCOREWIN   = 2.0;

   static DecimalFormat intfmt = DecimalHelper.defaultDecimalFormat("0");
   static DecimalFormat decfmt1 = DecimalHelper.defaultDecimalFormat("#0.0");
   static DecimalFormat decfmt2 = DecimalHelper.defaultDecimalFormat("#00.0");
   static DecimalFormat decfmt3 = DecimalHelper.defaultDecimalFormat("#00.000");

   public Train()
   {
      MaxOutput = -1000.0;
      MinOutput = 1000.0;
   }

   public double GetMin()
   {
      return( MinOutput );
   }

   public double GetMax()
   {
      return( MaxOutput );
   }

   public double GetMapMin()
   {
      return( Map(MinOutput) );
   }

   public double GetMapMax()
   {
      return( Map(MaxOutput) );
   }

   public void Print_Input1( Board b )
   {
      for (int i = 0; i < Board.BOXSIZE; i++) {
         System.out.println(intfmt.format(b.Get( i )) + " ");
         //if (i == 2 || i == 5 ) System.out.println();
      }
      System.out.println(" ");
   }

   public void Print_Input2( Board b )
   {
      for (int i = 0; i < Board.BOXSIZE; i++) {
         switch( b.Get( i ) ) {
         case 0:
            System.out.println(decfmt1.format(0.9) + " " );
            System.out.println(decfmt1.format(0.0) + " " );
            System.out.println(decfmt1.format(0.0) + " " );
            break;
         case 1:
            System.out.println(decfmt1.format(0.0) + " " );
            System.out.println(decfmt1.format(0.9) + " " );
            System.out.println(decfmt1.format(0.0) + " " );
            break;
         case 2:
            System.out.println(decfmt1.format(0.0) + " " );
            System.out.println(decfmt1.format(0.0) + " " );
            System.out.println(decfmt1.format(0.9) + " " );
            break;
         default:
            System.out.println( "Error in procedure");
            break;
         }
         System.out.println(" ");
         if ((i+1) % 3 == 0) System.out.println();
      }
   }

   public void Print_Input3( Board b )
   {
      final double [] input_map = { 0.1, 0.5, 0.9 };

      for (int i = 0; i < Board.BOXSIZE; i++) {
         System.out.println(decfmt1.format(input_map[ b.Get( i ) ]) + " ");
      }
      System.out.println("  ");
   }

   public void Print_Input( Board b, int type )
   {
      if (type == 0) {
         Print_Input1( b );
      } else {
         Print_Input2( b );
      }
   }

   public boolean CheckGood( Board b, int x, int y, int z, int N, int OPPOSITEN )
   {
      boolean good;

      good = (b.Get(x) == N) && (b.Get(y) == N) && (b.Get(z) == OPPOSITEN);
      if (good) return( true );
      good = (b.Get(x) == N) && (b.Get(y) == OPPOSITEN) && (b.Get(z) == N);
      if (good) return( true );
      good = (b.Get(x) == OPPOSITEN) && (b.Get(y) == N) && (b.Get(z) == N);
      if (good) return( true );

      return( false );
   }

   double CalcGood( Board b, double AMT, int N, int OPPOSITEN )
   {
      double amt=0.0;
      boolean good;

      /* Check for horizontal good */
      good = CheckGood( b, 0, 1, 2, N, OPPOSITEN );
      if (good) amt += AMT;

      good = CheckGood( b, 3, 4, 5, N, OPPOSITEN );
      if (good) amt += AMT;

      good = CheckGood( b, 6, 7, 8, N, OPPOSITEN);
      if (good) amt += AMT;


      /* Check for vertical good */
      good = CheckGood( b, 0, 3, 6, N, OPPOSITEN );
      if (good) amt += AMT;

      good = CheckGood( b, 1, 4, 7, N, OPPOSITEN );
      if (good) amt += AMT;

      good = CheckGood( b, 2, 5, 8, N, OPPOSITEN );
      if (good) amt += AMT;

      /* Check for diagonal good */
      good = CheckGood( b, 0, 4, 8, N, OPPOSITEN );
      if (good) amt += AMT;

      good = CheckGood( b, 2, 4, 6, N, OPPOSITEN );
      if (good) amt += AMT;

      /* Return goodness */
      return( amt );
   }

   public boolean CheckBlock( Board b, int x, int y, int z, int N, int OPPOSITEN )
   {
      boolean block;

      block = (b.Get(x) == N) && (b.Get(y) == OPPOSITEN) && (b.Get(z) == OPPOSITEN);
      if (block) return( true );
      block = (b.Get(x) == OPPOSITEN) && (b.Get(y) == N) && (b.Get(z) == OPPOSITEN);
      if (block) return( true );
      block = (b.Get(x) == OPPOSITEN) && (b.Get(y) == OPPOSITEN) && (b.Get(z) == N);
      if (block) return( true );

      return( false );
   }

   double CalcBlock( Board b, double AMT, int N, int OPPOSITEN )
   {
      double amt=0.0;
      boolean block;

      /* Check for horizontal block */
      block = CheckBlock( b, 0, 1, 2, N, OPPOSITEN );
      if (block) amt += AMT;

      block = CheckBlock( b, 3, 4, 5, N, OPPOSITEN );
      if (block) amt += AMT;

      block = CheckBlock( b, 6, 7, 8, N, OPPOSITEN);
      if (block) amt += AMT;


      /* Check for vertical block */
      block = CheckBlock( b, 0, 3, 6, N, OPPOSITEN );
      if (block) amt += AMT;

      block = CheckBlock( b, 1, 4, 7, N, OPPOSITEN );
      if (block) amt += AMT;

      block = CheckBlock( b, 2, 5, 8, N, OPPOSITEN );
      if (block) amt += AMT;

      /* Check for diagonal block */
      block = CheckBlock( b, 0, 4, 8, N, OPPOSITEN );
      if (block) amt += AMT;

      block = CheckBlock( b, 2, 4, 6, N, OPPOSITEN );
      if (block) amt += AMT;

      /* Return block amount */
      return( amt );
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

   double Map( double xw )
   {
      double vmax = 0.9;
      double vmin = 0.1;
      double wmax = 2.0;
      double wmin = -2.0;
      double tmp;
      double result;

      tmp = (vmax - vmin) / (wmax - wmin);
      result = vmin + (tmp * (xw - wmin));
      return( result );
   }

   int OP( int value )
   {
      int opp = 0;

      if (value == 1) {
         opp = 2;
      } else if (value == 2) {
         opp = 1;
      } else {
         System.out.println("Error in OP");
      }
      return( opp );
   }

   public double Eval_Board( Board b, int value, int opp )
   {
      double score = 0.0;
      double result;
      final double CENTER = 0.7;
      final double CORNER = 0.3;
      final double OTHER  = 0.2;

      if (b.Win( value )) {

         score = SCOREWIN;

      } else if (b.Win( opp )) {

         score = -SCOREWIN;

      } else if (b.Tie()) {

         score = 0.0;

      } else {

         // center is worth 3
         if (b.Get(4) == value) score += CENTER;

         // corners are worth 2
         if (b.Get(0) == value) score += CORNER;
         if (b.Get(2) == value) score += CORNER;
         if (b.Get(6) == value) score += CORNER;
         if (b.Get(8) == value) score += CORNER;

         // everything else worth 1
         if (b.Get(1) == value) score += OTHER;
         if (b.Get(3) == value) score += OTHER;
         if (b.Get(5) == value) score += OTHER;
         if (b.Get(7) == value) score += OTHER;

         SaveMinMax( score );
         //System.out.println("\nscore = %3.1lf\n", score  + " " );
      }

      result = Map( score );
      return( result );
   }

   public double New_Eval_Board( Board b, int value, int opp )
   {
      double score1;
      double score2;
      double result;

      if (b.Win( value )) {

         result = SCOREWIN;

      } else if (b.Win( opp )) {

         result = -SCOREWIN;

      } else {

         if (false) {
           System.out.println(decfmt2.format(CalcGood( b, ONES, 0, value ))  + " " );
           System.out.println(decfmt2.format(CalcGood( b, TWOS, value, 0 ))  + " " );
         }

         score2  = CalcGood( b, ONES, 0, value );
         score2 += CalcGood( b, TWOS, value, 0 );

         if (false) {
            System.out.println(decfmt2.format(CalcGood( b, ONES, 0, opp ))  + " " );
            System.out.println(decfmt2.format(CalcGood( b, TWOS, opp, 0 ))  + ", " );
         }

         score1  = CalcGood( b, ONES, 0, opp );
         score1 += CalcGood( b, TWOS, opp, 0 );

         result = score2 - score1;

         if (false) {
            if (score1 == 0.0) score1 = 0.01;
            result = score2 / score1;
         }

         if (false) {
            System.out.println(decfmt2.format(score2)  + " " );
            System.out.println(decfmt2.format(score1)  + " " );
            System.out.println(decfmt2.format(result)  + " " );
            System.out.println(decfmt2.format(Map(result))  + " " );
         }
         SaveMinMax( result );
      }

      result = Map( result );
      return( result );
   }

   public double Calc_Output( Board b, int value, int opp )
   {
      double score1;
      double score2;
      double result;

      if (b.Win( value )) {

         result = SCOREWIN;

      } else if (b.Win( opp )) {

         result = -SCOREWIN;

      } else {

         if (false) {
           System.out.print(decfmt2.format(CalcGood( b, ZEROS, 0, 0 ))  + " " );
           System.out.print(decfmt2.format(CalcGood( b, ONES, 0, value ))  + " " );
           System.out.print(decfmt2.format(CalcGood( b, TWOS, value, 0 ))  + " " );
           System.out.print(decfmt2.format(CalcBlock( b, BLOCKS, value, opp ))  + ", " );
         }

         score2  = CalcGood( b, ZEROS, 0, 0 );
         score2 += CalcGood( b, ONES, 0, value );
         score2 += CalcGood( b, TWOS, value, 0 );
         score2 += CalcBlock( b, BLOCKS, value, opp );

         if (false) {
            System.out.print(decfmt2.format(CalcGood( b, ZEROS, 0, 0 ))  + " " );
            System.out.print(decfmt2.format(CalcGood( b, ONES, 0, opp ))  + " " );
            System.out.print(decfmt2.format(CalcGood( b, TWOS, opp, 0 ))  + ", " );
         }

         score1  = CalcGood( b, ZEROS, 0, 0 );
         score1 += CalcGood( b, ONES, 0, opp );
         score1 += CalcGood( b, TWOS, opp, 0 );

         result = score2 - score1;

         if (false) {
            if (score1 == 0.0) score1 = 0.01;
            result = score2 / score1;
         }

         if (false) {
            System.out.print(decfmt2.format(score2)  + " " );
            System.out.print(decfmt2.format(score1)  + " " );
            System.out.println(decfmt2.format(result));
         }
         SaveMinMax( result );
      }

      result = Map( result );
      return( result );
   }

   public void Print_Output( Board b, int type )
   {
      double result = Calc_Output( b, 2, 1 );
      System.out.println(decfmt3.format(result) );
      if (type != 0) {
         System.out.println();
      }
   }

   public int Move( Board b, int value )
   {
      double highest = -101.0;
      double new_value;
      int j = -1;
      int opp;

      opp = OP( value );
      for (int i = 0; i < Board.BOXSIZE; i++) {
         if (b.Get( i ) == 0) {
            b.Set( i, value );
            new_value = Calc_Output( b, value, opp );
            if (new_value > highest) {
               highest = new_value;
               j = i;
            }
            //System.out.println("%d %4.2lf %4.2lf %d\n",i,new_value,highest,j);
            b.Set( i, 0 );
         }
      }
      if (j >= 0) {
         b.Set( j, value );
         b.IncrementMove();
         //System.out.println("\n");
      }

      // Return the move.
      return( j );
   }
}
