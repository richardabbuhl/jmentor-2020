// Copyright 2002, Richard Abbuhl

package com.jmentor.jtictactoe;

import java.text.DecimalFormat;

public class Game {

   public int Num_Games;
   public int Num_Ties;
   public int Player1_Wins;
   public int Player2_Wins;

   public static DecimalFormat decfmt = DecimalHelper.defaultDecimalFormat("#0.00");

   public Game()
   {
      ClearScore();
   }

   public void ClearScore()
   {
      Num_Games = 0;
      Num_Ties = 0;
      Player1_Wins = 0;
      Player2_Wins = 0;
   }

   public void DisplayScore()
   {
      System.out.print(Num_Games + " games, " );
      System.out.print("1_Wins=" + Player1_Wins + " " + decfmt.format( (double)Player1_Wins / (double)Num_Games ) + ", " );
      System.out.print("2_Wins=" + Player2_Wins + " " + decfmt.format( (double)Player2_Wins / (double)Num_Games ) + ", " );
      System.out.println("Ties=" + Num_Ties + " " + decfmt.format( (double)Num_Ties / (double)Num_Games ) );
   }

   public void Play( Board board, Player Player1, Player Player2 )
   {
      Train train;

      // Increment the number of games played.
      Num_Games++;

      // Clear the board for the next game.
      board.ClearAll();

      // Play until the game is complete.
      while (true) {

         // Player one moves.
         Player1.Move( board, 1 );
         if (false) train.Print_Output( board, 1 );

         // Check for a win.
         if (board.Win( 1 )) {
            if (false) board.DisplayAll();
            Player1_Wins++;
            return;
         }

         // Check for a tie.
         if (board.Tie()) {
            Num_Ties++;
            return;
         }

         // Player one moves
         Player2.Move( board, 2 );
         if (false) train.Print_Output( board, 1 );

         // Check for a win.
         if (board.Win( 2 )) {
            if (false) board.DisplayAll();
            Player2_Wins++;
            return;
         }

         // Check for a tie.
         if (board.Tie()) {
            Num_Ties++;
            return;
         }
      }
   }
}
