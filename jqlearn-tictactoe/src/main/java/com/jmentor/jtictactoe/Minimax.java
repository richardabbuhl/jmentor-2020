package com.jmentor.jtictactoe;

import java.text.DecimalFormat;

public class Minimax extends Player
{
   static double ZEROS     = 0.1;
   static double ONES      = 0.2;
   static double TWOS      = 0.3;
   static double BLOCKS    = 0.3;
   static double SCOREWIN  = 2.0;
   static DecimalFormat decfmt  = DecimalHelper.defaultDecimalFormat("#0.0000000");
   static DecimalFormat decfmt2 = DecimalHelper.defaultDecimalFormat("0.0000");
   
   class nodetype {
      int [] board;
      int turn;
      int which;
      double value;
      nodetype son;
      nodetype next;

      public nodetype()
      {
         board = new int[Board.BOXSIZE];
      }
   };

   private int NumAllocs;
   private int NumFrees;
   private int NumLookAheads;
   private double MaxOutput;
   private double MinOutput;
   
   public Minimax( int lookAheads )
   {
      NumLookAheads = lookAheads;
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

   public int CheckGood( int [] b, int x, int y, int z, int N, int OPPOSITEN )
   {
      boolean good;

      good = (b[x] == N) && (b[y] == N) && (b[z] == OPPOSITEN);
      if (good) return( 1 );
      good = (b[x] == N) && (b[y] == OPPOSITEN) && (b[z] == N);
      if (good) return( 1 );
      good = (b[x] == OPPOSITEN) && (b[y] == N) && (b[z] == N);
      if (good) return( 1 );

      return( 0 );
   }

   public double CalcGood( int [] b, double AMT, int N, int OPPOSITEN )
   {
      double amt=0.0;
      int good;

      /* Check for horizontal good */
      good = CheckGood( b, 0, 1, 2, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      good = CheckGood( b, 3, 4, 5, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      good = CheckGood( b, 6, 7, 8, N, OPPOSITEN);
      if (good == 1) amt += AMT;


      /* Check for vertical good */
      good = CheckGood( b, 0, 3, 6, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      good = CheckGood( b, 1, 4, 7, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      good = CheckGood( b, 2, 5, 8, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      /* Check for diagonal good */
      good = CheckGood( b, 0, 4, 8, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      good = CheckGood( b, 2, 4, 6, N, OPPOSITEN );
      if (good == 1) amt += AMT;

      /* Return goodness */
      return( amt );
   }

   public int CheckBlock( int [] b, int x, int y, int z, int N, int OPPOSITEN )
   {
      boolean block;

      block = (b[x] == N) && (b[y] == OPPOSITEN) && (b[z] == OPPOSITEN);
      if (block) return( 1 );
      block = (b[x] == OPPOSITEN) && (b[y] == N) && (b[z] == OPPOSITEN);
      if (block) return( 1 );
      block = (b[x] == OPPOSITEN) && (b[y] == OPPOSITEN) && (b[z] == N);
      if (block) return( 1 );

      return( 0 );
   }

   public double CalcBlock( int [] b, double AMT, int N, int OPPOSITEN )
   {
      double amt=0.0;
      int block;

      /* Check for horizontal block */
      block = CheckBlock( b, 0, 1, 2, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      block = CheckBlock( b, 3, 4, 5, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      block = CheckBlock( b, 6, 7, 8, N, OPPOSITEN);
      if (block == 1) amt += AMT;


      /* Check for vertical block */
      block = CheckBlock( b, 0, 3, 6, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      block = CheckBlock( b, 1, 4, 7, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      block = CheckBlock( b, 2, 5, 8, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      /* Check for diagonal block */
      block = CheckBlock( b, 0, 4, 8, N, OPPOSITEN );
      if (block == 1) amt += AMT;

      block = CheckBlock( b, 2, 4, 6, N, OPPOSITEN );
      if (block == 1) amt += AMT;

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

   public double Map( double xw )
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

   public void ClearAll( int [] square )
   {
      for (int i = 0; i < Board.BOXSIZE; i++) {
         square[i] = 0;
      }
   }   

   public boolean Tie( int [] square )
   {
      for (int i = 0; i < Board.BOXSIZE; i++) {
        if ( square[i] == 0 ) return( false );
      }
      return( true );
   }

   public boolean Win( int [] square, int N )
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

   public int OP( int value )
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

   public nodetype Generate( int [] brd, int value )
   {
      nodetype first = null;
      nodetype last = null;
      nodetype temp = null;

      /* Check for a win */
      if (Win( brd, 1 ) || Win( brd, 2 ) || Tie( brd )) {
         return( null );
      }

      first = null;
      last = null;
      for (int i = 0; i < Board.BOXSIZE; i++) {

         if (brd[ i ] == 0) {
            //temp = new struct nodetype;
            temp = new nodetype();
            //assert( temp != null );
            NumAllocs++;
            ClearAll(temp.board);
            temp.turn = 0;
            temp.son = null;
            temp.next = null;
            temp.which = i;
            temp.value = -999.0;

            for (int j = 0; j < Board.BOXSIZE; j++) {
               temp.board[j] = brd[j];
            }
            temp.board[i] = value;

            if (first == null) {
               first = temp;
               last = temp;
            } else {
               last.next = temp;
               last = last.next;
            }
         }
      }

      /* Return the result */
      return( first );
   }

   public void Expand( nodetype p, int plevel, int depth, int player )
   {
      nodetype q = null;
      int opp;
   
      if (plevel < depth) {
   
         /* p is not at the maximum level */
         q = Generate( p.board, player );
         opp = OP( player );

         p.son = q;

         while (q != null) {

            if (p.turn == 1) {
               q.turn = -1;
            } else {
               q.turn = 1;
            }
            q.son = null;

            Expand( q, plevel+1, depth, opp );
            q = q.next;
         }
      }
   }

   public nodetype BuildTree( int [] brd, int looklevel, int player )
   {
      nodetype ptree = null;
      int i;

      //ptree = new struct nodetype;
      ptree = new nodetype();
      //assert( ptree != null );
      NumAllocs++;

      /* Clear the board */
      ClearAll(ptree.board);

      for (i = 0; i < Board.BOXSIZE; i++) {
         ptree.board[i] = brd[ i ];
      }

      ptree.turn = 1;
      ptree.son = null;
      ptree.next = null;
      ptree.value = -999.0;
   
      Expand( ptree, 0, looklevel, player );
      return( ptree );
   }

   public double Evaluate( int [] b, int value )
   {
      double score1;
      double score2;
      double result;
      int opp;

      opp = OP( value );
      if (Win( b, value )) {

         result = SCOREWIN;

      } else if (Win( b, opp )) {

         result = -SCOREWIN;

      } else if (Tie( b )) {

         result = 0.0;

      } else if (false) {

         score2 = CalcGood( b, ONES, 0, value );
         score2 += CalcGood( b, TWOS, value, 0 );

         score1 = CalcGood( b, ONES, 0, opp );
         score1 += CalcGood( b, TWOS, opp, 0 );

         result = score2 - score1;
         SaveMinMax( result );

      } else {

         if (false) {
           System.out.println(CalcGood( b, ZEROS, 0, 0 ) );
           System.out.println(CalcGood( b, ONES, 0, value ) );
           System.out.println(CalcGood( b, TWOS, value, 0 ) );
           System.out.println(CalcBlock( b, BLOCKS, value, opp ) );
         }

         score2  = CalcGood( b, ZEROS, 0, 0 );
         score2 += CalcGood( b, ONES, 0, value );
         score2 += CalcGood( b, TWOS, value, 0 );
         score2 += CalcBlock( b, BLOCKS, value, opp );

         if (false) {
            System.out.println(CalcGood( b, ZEROS, 0, 0 ) );
            System.out.println(CalcGood( b, ONES, 0, opp ) );
            System.out.println(CalcGood( b, TWOS, opp, 0 ) );
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
            System.out.println(score2 );
            System.out.println(score1 );
            System.out.println(result );
         }

         SaveMinMax( result );
      }

      result = Map( result );
      return( result );
   }

   public nodetype BestBranch( nodetype pnd, int player, double [] pvalue )
   {
      if (pnd.son == null) {

         pvalue[0] = Evaluate( pnd.board, player );
         pnd.value = pvalue[0];       

         //System.out.println("----------");
         //DisplayAll(pnd.board);
         //System.out.println("---------- " + decfmt2.format(pvalue[0]) );
         
         return( pnd );

      } else {

         nodetype pbest = null;
         double [] val = new double[1];

         /* the node is not a leaf, traverse the list of sons */
         nodetype p = pnd.son;

         BestBranch( p, player, pvalue );
         pbest = p;

         if (pnd.turn == -1) {
            pvalue[0] = -pvalue[0];
         }

         p = p.next;
         while (p != null) {

            BestBranch( p, player, val );

            if (pnd.turn == -1) {
              val[0] = -val[0];
            }

            if (val[0] > pvalue[0]) {
               pvalue[0] = val[0];
               pbest = p;
            }

            p = p.next;
         }

         if (pnd.turn == -1) {
            pvalue[0] = -pvalue[0];
         }

         //System.out.println("----------");
         //DisplayAll(pbest.board);
         //System.out.println("---------- " + decfmt2.format(pvalue[0]) );

         return( pbest );
      }
   }

   public void FreeTree( nodetype ptree )
   {
      if (ptree != null) {
         FreeTree( ptree.son );
         FreeTree( ptree.next );
         ptree = null;
         NumFrees++;
      }
   }

   public void DisplayAll( int [] square )
   {
      int value = 0;
      for (int i = 0; i < Board.BOXSIZE; i++) {
        if (square[i] == 2) {
           value = 1;
        } else if (square[i] == 1) {
           value = 2;
        } else if (square[i] == 0) {
           value = 0;
        }
        System.out.print( value + " " );
        if (i == 2 || i == 5 ) System.out.println();
      }
      System.out.println();
   }   

   public void DisplayTree( nodetype ptree )
   {
      if (ptree != null) {
         DisplayAll( ptree.board );
         System.out.print( "^value = " + decfmt2.format(ptree.value) + ", " );
         System.out.println( "turn = " + ptree.turn );
         System.out.println();
         System.out.println( "son:" );
         DisplayTree( ptree.son );
         System.out.println( "next:" );
         DisplayTree( ptree.next );
      }
   }

   public int Move( int [] brd, int player )
   {
      final int looklevel = NumLookAheads;
      nodetype ptree = null;
      nodetype best;
      double [] value = new double[1];

      ptree = BuildTree( brd, looklevel, player );
      //DisplayTree( ptree );
      best = BestBranch( ptree, player, value );
      if (brd[best.which] != 0) {
        System.out.println("Error:  chosen move is not empty" + best.which);
        DisplayAll(brd);
        return( -999 );
      }
      return( best.which );
   }

   public int Move2( Board brd, int player, double [] output_value )
   {
      final int looklevel = NumLookAheads;
      nodetype ptree = null;
      nodetype best;
      double [] value = new double[1];

      ptree = BuildTree( brd.square, looklevel, player );
      //DisplayTree( ptree );
      best = BestBranch( ptree, player, value );
      if (brd.Get(best.which) != 0) {
        System.out.println("Error:  chosen move is not empty" + best.which);
        DisplayAll(brd.square);
        return( -999 );
      }
      brd.Set( best.which, player );
      brd.IncrementMove();
      FreeTree( ptree );
      return( best.which );
   }

   public int Move( Board brd, int player )
   {
      double [] value = new double[1];
      int which;
      which = Move2( brd, player, value );
      //printf("value = %lf\n", value );
      return( which );
   }

   public int evaluateMove (int [] squares)
   {
      int numX_SQUARES = 0;
      int numO_SQUARES = 0;
      int which = 0;

      for (int i = 0; i < Board.BOXSIZE; i++) {
         if (squares[i] == 1) numX_SQUARES++;
         else if (squares[i] == 2) numO_SQUARES++;
      }
      if (numX_SQUARES == 0 && numO_SQUARES == 0) {
         which = 4; // Move2( squares, 2 );
      } else if (numX_SQUARES > numO_SQUARES) {
         which = Move( squares, 2 );
      } else {
         which = Move( squares, 1 );
      }
      return which;
   } // end Interface

   public int evaluateRandomMove(int[] b)
   {
      int move;
      int count = 0;
      int [] saved = new int[Board.BOXSIZE];

      // Save all of the open values.
      for (int i = 0; i < Board.BOXSIZE; i++) {
         if (b[i] == 0) {
           saved[count++] = i;
         }
      }

      // Check for an error.
      if (count == 0) {
        System.out.println("Error:  count is 0");
        DisplayAll(b);
        return( -999 );
      }

      // Get an index within the saved values.
      move = (int) (Math.random() * count);  // 0 to count
      if (b[saved[move]] != 0) {
        System.out.println("Error:  chosen move is not empty" + saved[move]);
        DisplayAll(b);
        return( -999 );
      }
      return saved[move];
   } // end evaluateRandomMove   

   public void doTest(boolean randomFirst)
   {
      int Num_Games = 0;
      int Player1_Wins = 0;
      int Player2_Wins = 0;
      int Num_Ties = 0;
      Board board = new Board();
      Random random = new Random();
      DecimalFormat decfmt  = DecimalHelper.defaultDecimalFormat("#0.0000000");
   
      for (int i = 0; i < 10000; i++) {

        // Increment the number of games played.
        Num_Games++;

        // Clear the board for the next game.
        board.ClearAll();

        // Play until the game is complete.
        while (true) {

           // Player one moves.
           if (randomFirst)
              random.Move(board, 1);
           else
              Move(board, 1);

           // Check for a win.
           if (board.Win( 1 )) {
              Player1_Wins++;
              break;
           }

           // Check for a tie.
           if (board.Tie()) {
              Num_Ties++;
              break;
           }
          
           // Player two moves
           if (randomFirst)
              Move(board, 2);
           else
              random.Move(board, 2);

           // Check for a win.
           if (board.Win( 2 )) {
              Player2_Wins++;
              break;
           }

           // Check for a tie.
           if (board.Tie()) {
              Num_Ties++;
              break;
           }
        }
      }

      System.out.print(Num_Games + " games, ");
      System.out.print("1_Wins=" + Player1_Wins + " " + 
         decfmt.format((double)Player1_Wins / (double)Num_Games) + ", " );
      System.out.print("2_Wins=" + Player2_Wins + " " + 
         decfmt.format((double)Player2_Wins / (double)Num_Games) + ", " );
      System.out.println("Ties=" + Num_Ties + " " + 
         decfmt.format((double)Num_Ties / (double)Num_Games) );
   }

   public static void main (String[] args)
   {
      Minimax me = new Minimax(4);
      System.out.println("Random  first: "); me.doTest(true);
      System.out.println("Minimax first: "); me.doTest(false);
   }
}
