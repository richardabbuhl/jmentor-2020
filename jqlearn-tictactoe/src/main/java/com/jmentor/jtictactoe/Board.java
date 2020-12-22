// Copyright 2002, Richard Abbuhl

package com.jmentor.jtictactoe;

public class Board implements Cloneable {

   public final static int COLS      = 3;
   public final static int ROWS      = 3;
   public final static int BOXSIZE   = (COLS*ROWS);
   public final static int MAXMOVES  = 8;

   int [] square = new int[ BOXSIZE ];
   int MoveNumber;

   public Board()
   {
      ClearAll();
   }

   public void ClearAll()
   {
      for (int i = 0; i < BOXSIZE; i++) {
         square[i] = 0;
      }
      MoveNumber = 0;
   }

   public void IncrementMove()
   {
      MoveNumber++;
   }

   public int TotalMoves()
   {
      return( MoveNumber );
   }

   public void Set( int x, int value )
   {
      //assert( (value == 0) || (value == 1) || (value == 2) );
      square[x] = value;
   }

   public int Get( int x )
   {
      return( square[x] );
   }

   public int NextAvail( int start )
   {
      for (int i = start; i < BOXSIZE; i++) {
         if (square[i] == 0) {
            return( i );
         }
      }
      return( -1 );
   }

   public Object clone()
   {
      Board cpy = new Board();
      for (int i = 0; i < BOXSIZE; i++) {
         cpy.square[i] = square[i];
      }
      cpy.MoveNumber = MoveNumber;
      return( cpy );
   }

   public void DisplayAll()
   {
      for (int i = 0; i < BOXSIZE; i++) {
         System.out.print(square[i] + " ");
         if (i == 2 || i == 5 ) System.out.println();
      }
      System.out.println();
      System.out.println();
   }

//   public void DisplayAll( FILE *fp )
//   {
//      for (int i = 0; i < BOXSIZE; i++) {
//         fprintf( fp, "%1d ", square[i] );
//         if (i == 2 || i == 5 ) fprintf( fp, "\n" );
//      }
//      fprintf( fp, "\n" );
//   }

   public int NumMoves( int this_piece )
   {
      int count = 0;

      for (int i = 0; i < BOXSIZE; i++) {
         if (square[i] == this_piece) count++;
      }
      return( count );
   }

   public boolean Win( int N )
   {
      boolean win;

      /* Check for horizontal win */
      win = (square[0] == N) && (square[1] == N) && (square[2] == N);
      if (win) return( true );
      win = (square[3] == N) && (square[4] == N) && (square[5] == N);
      if (win) return( true );
      win = (square[6] == N) && (square[7] == N) && (square[8] == N);
      if (win) return( true );

      /* Check for vertical win */
      win = (square[0] == N) && (square[3] == N) && (square[6] == N);
      if (win) return( true );
      win = (square[1] == N) && (square[4] == N) && (square[7] == N);
      if (win) return( true );
      win = (square[2] == N) && (square[5] == N) && (square[8] == N);
      if (win) return( true );

      /* Check for diagonal win */
      win = (square[0] == N) && (square[4] == N) && (square[8] == N);
      if (win) return( true );
      win = (square[2] == N) && (square[4] == N) && (square[6] == N);
      if (win) return( true );

      /* Did not win */
      return( false );
   }

   public boolean Tie()
   {
      /* Check the number of moves */
      return( TotalMoves() > MAXMOVES );
   }
}
