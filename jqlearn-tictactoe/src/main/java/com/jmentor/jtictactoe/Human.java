// Copyright 2002, Richard Abbuhl

package com.jmentor.jtictactoe;

import java.io.*;

public class Human extends Player
{
   public int Move( Board b, int value )
   {
      int x = -1;
      BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
      String theMove;

      b.DisplayAll();
      while (true) {
         System.out.print("Enter a position: ");
         try {
            x = Integer.parseInt( console.readLine() );
         } catch(java.io.IOException e) {
            x = -1;
         }
         if (x < 0 || x >= Board.BOXSIZE) {
            System.out.println("Invalid range");
            continue;
         }
         if (b.Get( x ) != 0) {
            System.out.println("Square already occupied");
            continue;
         }
         b.Set( x, value );
         b.IncrementMove();
         return( x );
      }
   }
}
