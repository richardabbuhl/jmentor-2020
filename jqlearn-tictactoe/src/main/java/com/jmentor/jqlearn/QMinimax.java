// Copyright 2002, Richard Abbuhl

package com.jmentor.jqlearn;

import com.jmentor.jtictactoe.Minimax;

public class QMinimax extends Minimax {

   private QLearn qlearn = null;

   public QMinimax( int lookAheads, QLearn ql )
   {
      super(lookAheads);
      qlearn = ql;
   }

   public double Evaluate( int [] b, int value )
   {
      double result = qlearn.getValue( b );
      return( result );
   }
}
