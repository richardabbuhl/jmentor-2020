// Copyright 2002, Richard Abbuhlv

package com.jmentor.jqlearn;

import com.jmentor.jtictactoe.*;
import org.apache.commons.math3.random.MersenneTwister;

import java.text.DecimalFormat;

public class Teach extends Game {

   final double SCOREWIN   = 1.0;
   final double SCORELOSE  = -1.0;
   final double SCORETIE   = 0.0;

   QLearn qlearn;
   CPUTime cpu;
   MersenneTwister rg;
   long elapsed_iters;

   String ntf_file;
   boolean USE_MINIMAX;
   boolean USE_HUMAN;
   boolean TEST_NETWORK;
   boolean APPLY_NETWORK;
   boolean VERBOSE;
   boolean CONTAINS_DESIRED;
   boolean XML_DESIRED;
   boolean DISPLAY_BOARD_IO;
   int ITERATION_PRINT;
   boolean SAVE_WEIGHTS;
   int ngames;
   boolean dolearn;
   int num_hidden;
   double pattern_error;
   double total_error;
   double ALPHA;
   double EPSILON;
   boolean ANNEAL_EPSILON;
   double ANNEAL_EPSILON_RATE;
   boolean NETWORK_GOES_FIRST;

   Train train;

   static DecimalFormat decfmt1  = DecimalHelper.defaultDecimalFormat("#00.00000");
   static DecimalFormat decfmt2  = DecimalHelper.defaultDecimalFormat("#00.00");

   Teach()
   {
      qlearn = new QLearn();
      cpu = new CPUTime();
      rg = new MersenneTwister(13);
      ClearScore();
      USE_MINIMAX = true;
      USE_HUMAN = false;
      TEST_NETWORK = false;
      APPLY_NETWORK = false;
      VERBOSE = true;
      CONTAINS_DESIRED = true;
      XML_DESIRED = false;
      DISPLAY_BOARD_IO = false;
      ITERATION_PRINT = 100;
      SAVE_WEIGHTS = false;
      elapsed_iters = -1;
      ngames = 0;
      dolearn = true;
      num_hidden = 30;
      ALPHA = 0.10;
      EPSILON = 0.00;
      ANNEAL_EPSILON = false;
      ANNEAL_EPSILON_RATE = 0.01;
      NETWORK_GOES_FIRST = true;
   }

   void initializeQLearn()
   {
      qlearn.setAlpha( ALPHA );
      qlearn.setEpsilon( EPSILON );
   }

   void correctPredictions(Board previous, Board board, boolean isTerminal, double finalValue)
   {
      qlearn.Learn( previous, board, isTerminal, finalValue );
      elapsed_iters++;
   }

   /* random integer generator, uniform */
   int Pick_Value(int i, int n)
   {
      /* return an integer in i, i+1, ... n */
      return( rg.nextInt( n + 1 ) );
   }

   double AverageScore()
   {
      return( ((Player1_Wins * 2.0) + (double)Num_Ties) / (double)Num_Games );
   }

   void Play( Player Player1, Player Player2 )
   {
      Board board = new Board();
      Board previous = new Board();
      boolean goFirst = true; //Pick_Value( 0, 1 );
      int move = 0;

      // Increment the number of games played.
      Num_Games++;
      if (!dolearn) {
         //if ((Num_Games % ITERATION_PRINT) == 0) {
         //   System.out.println("Game " + Num_Games + "-------");
         //}
         if (USE_HUMAN) goFirst = true;
      }

      // Clear the board for the next game.
      board.ClearAll();
      pattern_error = 0.0;
      total_error = 0.0;

      //randomizePlayers();

      // if (dolearn && (Num_Games % ITERATION_PRINT) == 0) System.out.print(goFirst + ", ");

      if (dolearn && ANNEAL_EPSILON && (0.00001 < EPSILON) && ((Num_Games % 1000) == 0)) {
         double temp = qlearn.getEpsilon() - ANNEAL_EPSILON_RATE;
         qlearn.setEpsilon( temp < 0.0 ? 0.0 : temp );
      }

      // Play until the game is complete.
      while (true) {

         // Player one moves.
         previous = (Board)board.clone();
         if (goFirst) {
            move = Player1.Move( board, 1 );
         } else {
            move = Player2.Move( board, 1 );
         }
         if (DISPLAY_BOARD_IO) board.DisplayAll();

         // Check for a win.
         if (board.Win( 1 )) {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, true, SCOREWIN);
            }

            if (dolearn && (Num_Games % ITERATION_PRINT) == 0) {
               System.out.println(Num_Games + " " + elapsed_iters + " - 1 WINS, " + decfmt.format(AverageScore()));
            }

            if (goFirst) {
               Player1_Wins++;
            } else {
               Player2_Wins++;
            }
            break;

         // Check for a tie.
         } else if (board.Tie()) {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, true, SCORETIE);
            }

            if (dolearn && (Num_Games % ITERATION_PRINT) == 0) {
               System.out.println(Num_Games + " " + elapsed_iters + " - TIE, " + decfmt.format(AverageScore()));
            }

            Num_Ties++;
            break;

         } else {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, false, 0.0);
            }
         }

         // Player two moves
         previous = (Board)board.clone();
         if (goFirst) {
            move = Player2.Move( board, 2 );
         } else {
            move = Player1.Move( board, 2 );
         }
         if (DISPLAY_BOARD_IO) board.DisplayAll();

         // Check for a win:
         if (board.Win( 2 )) {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, true, SCORELOSE);
            }

            if (dolearn && (Num_Games % ITERATION_PRINT) == 0) {
               System.out.println(Num_Games + " " + elapsed_iters + " - 2 WINS, " + decfmt.format(AverageScore()));
            }

            if (goFirst) {
               Player2_Wins++;
            } else {
               Player1_Wins++;
            }
            break;

         // Check for a tie.
         } else if (board.Tie()) {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, true, SCORETIE);
            }

            if (dolearn && (Num_Games % ITERATION_PRINT) == 0) {
               System.out.println(Num_Games + " " + elapsed_iters + " - TIE, " + decfmt.format(AverageScore()));
            }

            Num_Ties++;
            break;

         } else {

            // use the last move and correct the prediction for the old position
            if (dolearn) {
               correctPredictions(previous, board, false, 0.0);
            }
         }
      }
   }

   void Play(String[] args)
   {
      long start = 0, end = 0;
      double train_time;
      int result;
      Minimax minimax = new Minimax( 4 );
      QMinimax qminimax = new QMinimax( 4, qlearn );
      Human human = new Human();
      Random random =  new Random();
      int pollchar;

      /* Get the options and check the input arguments */
      if (!get_options( args )) {
         show_usage();
         System.exit(0);
      }

      /* Initialize the network */
      initializeQLearn();

      if (TEST_NETWORK || APPLY_NETWORK || USE_HUMAN) {
         SAVE_WEIGHTS = false;
         qlearn.load("qweights");
         dolearn = false;
      }
      else {
         SAVE_WEIGHTS = true;
      }
      //System.out.print("A=%3.2lf, ", ALPHA);
      //System.out.print("E=%3.2lf, ", EPSILON);
      System.out.print("Hidden=0, ");

      /* Display training message */
      if (USE_MINIMAX) {
         System.out.print("Minimax, ");
      } else {
         System.out.print("Random, ");
      }
      if (USE_HUMAN) {
         System.out.println("Using Human...");
      }

      /* Display the timestamp */
      if (dolearn) {
        start = cpu.TimeStamp();
      }

      /* Start the timer */
      cpu.BeginTimer();
      /* System.out.println("\n"); */

      /* Train the network */
      if (USE_HUMAN) {
         Play( qminimax, human );
      } else {
         if (dolearn) System.out.println( "Playing " + ngames + " games" );
         for (int i = 0; i < ngames; i++) {
            if (dolearn) {
               Play( qlearn, qlearn );
            } else if (USE_MINIMAX) {
               if (NETWORK_GOES_FIRST) {
                  Play( qminimax, minimax );
               } else {
                  Play( minimax, qminimax );
               }
            } else {
               if (NETWORK_GOES_FIRST) {
                  Play( qminimax, random );
               } else {
                  Play( random, qminimax );
               }
            }
            //pollchar = ttypoll();
            //if (pollchar == 'q' || pollchar == 't') {
            //   System.out.println("=====terminated=====");
            //   break;
            //}
         }
      }

      /* Stop the timer and report the time used */
      if (dolearn) {
        train_time = cpu.EndTimer();
        cpu.TimerReport("\n\nFinished training",train_time);

        /* Display the ending timestamp and the elapsed time */
        end = cpu.TimeStamp();
        cpu.ElapsedTime(start,end);
        cpu.IterateTime(start,end,elapsed_iters);
      }

      DisplayScore();
      if (dolearn) {
        System.out.println("minimax Max = " + decfmt2.format(minimax.GetMax()) );
        System.out.println("minimax Min = " + decfmt2.format(minimax.GetMin()) );
        //System.out.println("train Max = " + decfmt2.format(train.GetMax()) + " " + decfmt2.format(train.GetMapMax()) );
        //System.out.println("train Min = " + decfmt2.format(train.GetMin()) + " " + decfmt2.format(train.GetMapMin()) );
        System.out.println("Alpha = " + decfmt2.format(qlearn.getAlpha()) + ", Epsilon = " + decfmt2.format(qlearn.getEpsilon()) );
      }

      /* Save the results of the network */
      if (SAVE_WEIGHTS) {
         System.out.println("Saving weights to qweights");
         qlearn.save("qweights");
      }
   }

   /*-----------------------------------------------------------
   * 
   * get_options - get the command line options.
   *
   -------------------------------------------------------------*/
   boolean get_options( String[] args )
   {
      /*
       * Parse command line arguments.
       */
      try {

        /* Stage 4: Process options. */
        for (int arg=0; arg < args.length; arg++) {

           if (args[arg].equals("-4"))
              DISPLAY_BOARD_IO = true;

           else if (args[arg].equals("-l")) {
              num_hidden = Integer.parseInt( args[++arg] );

           } else if (args[arg].equals("-m"))
              USE_MINIMAX = true;

           else if (args[arg].equals("-d"))
              USE_MINIMAX = false;

           else if (args[arg].equals("-h"))
              USE_HUMAN = true;

           else if (args[arg].equals("-t"))
              TEST_NETWORK = true;

           else if (args[arg].equals("-a"))
              APPLY_NETWORK = true;

           else if (args[arg].equals("-n")) {
              ngames = Integer.parseInt( args[++arg] );

           } else if (args[arg].equals("-v"))
              VERBOSE = true;

           else if (args[arg].equals("-x"))
              CONTAINS_DESIRED = false;

           else if (args[arg].equals("-o")) {
              ALPHA = Double.parseDouble( args[++arg] );

           } else if (args[arg].equals("-e")) {
              EPSILON = Double.parseDouble( args[++arg] );

           } else if (args[arg].equals("-f")) {
              ANNEAL_EPSILON = true;
              ANNEAL_EPSILON_RATE = Double.parseDouble( args[++arg] );

           } else if (args[arg].equals("-g")) {
              NETWORK_GOES_FIRST = false;

           } else
              ntf_file = args[arg];
        }

        // if (ntf_file == null) return( false );

      } catch (Exception e) {
         return( false );
      }

      /* No error occurred */
      return( true );
   }

   /*-----------------------------------------------------------
   * 
   * show_usage - display how to call this program.
   *
   -------------------------------------------------------------*/
   void show_usage()
   {
      System.out.println("JQLEARN V1.0.1, Copyright 2005 Richard Abbuhl.");
      System.out.println("usage:  jqlearn [-lmdhsrtanvxoefg0123]");
      System.out.println("   -l = specify hidden layer size (default 30).");
      System.out.println("   -m = use minimax (default).");
      System.out.println("   -d = use random.");
      System.out.println("   -h = use human.");
      System.out.println("   -s = start network training (default).");
      System.out.println("   -r = restart network training.");
      System.out.println("   -t = test network.");
      System.out.println("   -a = apply network.");
      System.out.println("   -n = number of simulations.");
      System.out.println("   -v = verbose parameter display.");
      System.out.println("   -x = pattern file does not contain desired outputs.");
      System.out.println("   -o = specify alpha (default 0.1).");
      System.out.println("   -e = specify epsilon (default 0.0).");
      System.out.println("   -f = anneal epsilon (default 0.1).");
      System.out.println("   -g = network goes second during test.");
      System.out.println("   -0 = display memory usage test.");
      System.out.println("   -1 = display ntf parameters.");
      System.out.println("   -2 = display ntf patterns.");
      System.out.println("   -3 = display network values during training.");
      System.out.println("   -4 = display board values during training.");
   }

   /*--------------------------------------------------------
   *
   * main - driver for the train program.
   *
   --------------------------------------------------------*/
   public static void main(String[] args)
   {
      Teach teach = new Teach();
      teach.Play( args );
   }
}
