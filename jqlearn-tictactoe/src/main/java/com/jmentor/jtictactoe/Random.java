// Copyright 2002, Richard Abbuhl

package com.jmentor.jtictactoe;

import org.apache.commons.math3.random.MersenneTwister;

public class Random extends Player {

   MersenneTwister rg;

   public Random()
   {
      rg = new MersenneTwister(13);
   }

   /* random integer generator, uniform */
   public int Pick_Value(int i, int n)
   {
      /* return an integer in i, i+1, ... n */
      return( rg.nextInt( n + 1 ) );
   }

   public int Move( Board b, int value )
   {
      int count = 0;
      int [] saved = new int[Board.BOXSIZE];

      // Save all of the open values.
      for (int i = 0; i < Board.BOXSIZE; i++) {
         if (b.Get(i) == 0) {
            saved[count++] = i;
         }
      }

      // Check for an error.
      if (count == 0) {
        System.out.println("Error:  count is 0");
        b.DisplayAll();
        return( -1 );
      }

      // Get an index within the saved values.
      int move = Pick_Value(0, count - 1);
      if (b.Get(saved[move]) != 0) {
        System.out.println("Error:  chosen move is not empty" + saved[move]);
        b.DisplayAll();
        return( -1 );
      }
      b.Set(saved[move], value);
      b.IncrementMove();

      // Return the move.
      return(saved[move]);
   }
}
