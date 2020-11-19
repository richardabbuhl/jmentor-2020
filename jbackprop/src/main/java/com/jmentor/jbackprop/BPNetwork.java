/*--------------------------------------------------------
*  Copyright Richard Abbuhl, 2002-2003
*
*  bpnetwork.java (backpropagation toolkit)
*  R. Abbuhl, 5 February 1989
*  %W% %G%
*
*   This is the set of routines used for backpropagation
*  on a neural network.
*
*  Revision History:
*
*     12/01/02, R. Abbuhl   
*     Converted to Java.
*
*     8/22/89, R. Abbuhl
*     Added comments, changed bp so either sigmoid, or
*     hyperbolic functions could be called.
*
*     4/19/90, R. Abbuhl
*     Removed the TimeStart(), and TimeStop() since
*     these timing functions are now implemented
*     in the package CPUTIME.C.
*
*     4/23/90, R. Abbuhl
*     Removed type declarations and routines related
*     to adding noise to the weights to stop oscillation.
*
*     5/1/90, R. Abbuhl
*     Added routines for reading and writing a header and
*     all the weight and bias values to a file using a byte
*     format.
*
*     5/3/90, R. Abbuhl
*     Fixed a bug in DisplayParams() where the bias value
*     was being displayed instead of the beta value.
*
*     5/14/90, R. Abbuhl
*     Fixed a bug in ReadHeader() where the version
*     number of the weights file was be compared using
*     the strcmp function and the string was not null
*     terminated.  Changed strcmp to strncmp so it would
*     not cause an error reading a non-null terminated
*     string.
*
*     05/26/90, R. Abbuhl
*     Changed def_input and def_output to pointer array
*     variables to enhance performance.
*
*     10/19/90, R. Abbuhl
*     Changed the type of file i/o to be binary for
*     saving and retrieving the weights.  This corrected
*     problems which occurred on the 386 and not on
*     UNIX.
*
*     12/02/90, R. Abbuhl
*     Changed a1 to aa1 because it causes a multiply
*     defined symbol to error occur using Lattice C.
*
*     02/18/91, R. Abbuhl
*     Accumalation of changes:
*       o Fixed two bugs allocating pointer arrays.
*       o Moved allocation of neural arrays to MYALLOC.
*
*     07/27/91, R. Abbuhl
*     Added declaration for start_iter.
*
*     09/09/91, R. Abbuhl
*     Added declaration for activ_func.
*
*     09/10/91, R. Abbuhl
*     Changed version ID from char[3] to int.
*
*     07/01/93, R. Abbuhl
*     Removed unncessary code.
*
--------------------------------------------------------*/

package com.jmentor.jbackprop;

import java.io.*;
import java.text.DecimalFormat;

public class BPNetwork extends Network {

   static DecimalFormat decfmt = DecimalHelper.defaultDecimalFormat("#0.0000000");

   /*********************************************************
   *                                                        *
   *  Compute_Error                                         *
   *   Computes the total error as a result of the          *
   *  pass through the network.                             *
   *                                                        *
   *  Calling Sequence:                                     *
   *   double = Compute_Error();                            *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   double Compute_Error( NetworkRecord net )
   {
      int i;
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      double [] ai = net.activation; //[amin]
      double [] des_out = net.desired_output; //[amin]
      double e = 0.0;

      for (i = amin; i <= amax; i++) {
         e += (ai[i] - des_out[i]) * (ai[i] - des_out[i]);
      }
      return(e);
   }

   /*********************************************************
   *                                                        *
   *  SigmoidComputeOutput                                  *
   *   Computes the activation output of the neural         *
   *  network using the sigmoid function.                   *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SigmoidComputeOutput()                               *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SigmoidComputeOutput( NetworkRecord net )
   {
      int i,j,l;
      double [] b = net.bias;
      double [] w = net.weight;
      double [] aj = net.activation;
      double [] ai = net.activation;
      double tmp;
      int wx = 0;

      for (l = 1; l < net.num_layers; l++) {
         //aj = &net.activation[net.x_layer_size[l]];
         //b = &net.bias[net.x_layer_size[l]];
         for (j = net.x_layer_size[l]; j <= net.y_layer_size[l]; j++) {
            tmp = b[j];
            //ai = &net.activation[net.x_layer_size[l-1]];
            for (i = net.x_layer_size[l-1]; i <= net.y_layer_size[l-1]; i++) {
               tmp += w[wx++] * ai[i];
            }
            aj[j] = Sigmoid( net, tmp );
         }
      }
   }

   /*********************************************************
   *                                                        *
   *  Sigmoid                                               *
   *   Computes an activation value using a sigmoid         *
   *  function.                                             *
   *                                                        *
   *  Calling Sequence:                                     *
   *   double = Sigmoid(x)                                  *
   *                                                        *
   *  Input:                                                *
   *   double x                                             *
   *                                                        *
   *  Output:                                               *
   *   double Sigmoid(x)                                    *
   *                                                        *
   *********************************************************/
   double Sigmoid(NetworkRecord net, double x )
   {
       if (x > 15.5) {
          return(.99999988); }
       else if (x < -15.5) {
          return(.00000012); }
       else {
        return((1.0 / (1.0 + Math.exp(beta * x)))); }
   }

   /*********************************************************
   *                                                        *
   *  SigmoidComputeBP                                      *
   *   Computes the weight adjustments on the neural        *
   *  network.                                              *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SigmoidComputeBP()                                   *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SigmoidComputeBP( NetworkRecord net )
   {
      int i,j;
      int nl_m1 = net.num_layers - 1;
      int nl_m2 = net.num_layers - 2;
      int out_min = net.x_layer_size[nl_m1];
      int out_max = net.y_layer_size[nl_m1];
      int in_min  = net.x_layer_size[nl_m2];
      int in_max  = net.y_layer_size[nl_m2];
      int in_w_min = net.x_weight_size[nl_m2];
      double [] ej = net.error; // [out_min] - 1
      double [] aj = net.activation; // [out_min]
      double [] dj = net.desired_output; // [out_min]
      double [] bj = net.bias; // [out_min]
      double [] obj = net.old_bias; // [out_min]
      double [] wi = net.weight; // [in_w_min]
      double [] owi = net.old_weight; // [in_w_min]
      double [] ai = net.activation;
      double tmp_b;
      double tmp_w;
      double A1_ej;
      int nlayers;

      for (j = out_min; j <= out_max; j++) {

         /***********************/
         /* calculate the error */
         /***********************/

         ej[j] = aj[j] * (1.0 - aj[j]) * (dj[j] - aj[j]);

         /***********************/
         /* calculate constants */
         /***********************/

         A1_ej = net.weight_lrate * ej[j];

         /************************/
         /* update the bias term */
         /************************/

         tmp_b = bj[j];
         bj[j] += A1_ej + (net.bias_lrate * (tmp_b - obj[j]));
         obj[j] = tmp_b;

         /******************************/
         /* set the activation pointer */
         /******************************/

         //ai = &net.activation[in_min];

         for (i = in_min; i <= in_max; i++) {

            /**********************/
            /* update the weights */
            /**********************/

            tmp_w = wi[in_w_min];
            wi[in_w_min] += (A1_ej * ai[i]) + (net.momentum_lrate * (tmp_w - owi[in_w_min]));
            owi[in_w_min++] = tmp_w;
         }
      }

      for (nlayers = net.num_layers; nlayers > 2; nlayers--)
         SigmoidComputeBP2( net, nlayers );
   }

   /*********************************************************
   *                                                        *
   *  SigmoidComputeBP2                                     *
   *   Computes the weight adjustments on the hidden        *
   *  layers of the neural network.                         *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SigmoidComputeBP()                                   *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SigmoidComputeBP2(NetworkRecord net, int nlayers )
   {
      int i,j,k;
      int nl_m1 = nlayers - 1;
      int nl_m2 = nlayers - 2;
      int nl_m3 = nlayers - 3;
      int out_min = net.x_layer_size[nl_m2];
      int out_max = net.y_layer_size[nl_m2];
      int mid_min = net.x_layer_size[nl_m1];
      int mid_max = net.y_layer_size[nl_m1];
      int in_min  = net.x_layer_size[nl_m3];
      int in_max  = net.y_layer_size[nl_m3];
      int out_w_min = net.x_weight_size[nl_m2];
      int in_w_min  = net.x_weight_size[nl_m3];
      double [] ej = net.error; //[out_min] - 1
      double [] aj = net.activation; //[out_min]
      double [] bj = net.bias; //[out_min]
      double [] obj = net.old_bias; //[out_min]
      double [] wk = net.weight;
      double [] ek = net.error;
      double [] wi = net.weight; //[in_w_min]
      double [] owi = net.old_weight; //[in_w_min]
      double [] ai = net.activation;
      double sum;
      double tmp_b;
      double tmp_w;
      double A1_ej;
      int woff = net.layer_size[nl_m2];
      int wx;

      for (j = out_min; j <= out_max; j++) {

         /**************************************/
         /* propagate the error back one layer */
         /**************************************/

         //ek = &net.error[mid_min];
         //wk = &net.weight[out_w_min++];
         wx = out_w_min++;
         sum = 0.0;
         for (k = mid_min; k <= mid_max; k++) {
            sum += ek[k] * wk[wx];
            wx += woff;
         }

         /***********************/
         /* calculate the error */
         /***********************/

         ej[j] = aj[j] * (1.0 - aj[j]) * sum;

         /***********************/
         /* calculate constants */
         /***********************/

         A1_ej = net.weight_lrate * ej[j];

         /************************/
         /* update the bias term */
         /************************/

         tmp_b = bj[j];
         bj[j] += A1_ej + (net.bias_lrate * (tmp_b - obj[j]));
         obj[j] = tmp_b;

         /******************************/
         /* set the activation pointer */
         /******************************/

         //ai = &net.activation[in_min];

         for (i = in_min; i <= in_max; i++) {

            /**********************/
            /* update the weights */
            /**********************/

            tmp_w = wi[in_w_min];
            wi[in_w_min] += (A1_ej * ai[i]) + (net.momentum_lrate * (tmp_w - owi[in_w_min]));
            owi[in_w_min++] = tmp_w;
         }
      }
   }

   /*********************************************************
   *                                                        *
   *  HyperbolicComputeOutput                               *
   *   Computes the activation output of the neural         *
   *  network using the hyperbolic tangent function.        *
   *                                                        *
   *  Calling Sequence:                                     *
   *   HyperbolicComputeOutput()                            *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void HyperbolicComputeOutput( NetworkRecord net )
   {
      int i,j,l;
      double [] b = net.bias;
      double [] w = net.weight;
      double [] aj = net.activation;
      double [] ai = net.activation;;
      double tmp;
      int wx = 0;

      for (l = 1; l < net.num_layers; l++) {
         //aj = &net.activation[net.x_layer_size[l]];
         //b = &net.bias[net.x_layer_size[l]];
         for (j = net.x_layer_size[l]; j <= net.y_layer_size[l]; j++) {
            tmp = b[j];
            //ai = &net.activation[net.x_layer_size[l-1]];
            for (i = net.x_layer_size[l-1]; i <= net.y_layer_size[l-1]; i++) {
               tmp += w[wx++] * ai[i];
            }
            aj[j] = Hyperbolic(tmp);
         }
      }
   }

   /*********************************************************************
   * Also known as the "logistic function".
   *********************************************************************/
   public double bpsigmoid(double a)
   {
     return 1.0 / ( 1.0 + Math.exp ( -a ) );
   }

   /*********************************************************************
   * hyperbolic tangent = 2 * sigmoid ( 2 * a ) - 1
   *********************************************************************/
   public double bptanh(double a)
   {
     return 2.0 * bpsigmoid ( 2.0 * a ) - 1.0;
   }

   /*********************************************************
   *                                                        *
   *  Hyperbolic                                            *
   *   Computes an activation value using a the             *
   *  hyperbolic tangent function.                          *
   *                                                        *
   *  Calling Sequence:                                     *
   *   double = Hyperbolic(x)                                *
   *                                                        *
   *  Input:                                                *
   *   double x                                              *
   *                                                        *
   *  Output:                                               *
   *   double Hyperbolic(x)                                  *
   *                                                        *
   *********************************************************/
   double Hyperbolic( double x )
   {
      if (x > 9.0) {
         return( .99999988); }
      else if (x < -9.0) {
         return( -.99999988); }
      else {
        return( bptanh( x ) );
      }
   }

   /*********************************************************
   *                                                        *
   *  HyperbolicComputeBP                                   *
   *   Computes the weight adjustments on the neural        *
   *  network using the hyperbolic tangent function.        *
   *                                                        *
   *  Calling Sequence:                                     *
   *   HyperbolicComputeBP()                                *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void HyperbolicComputeBP( NetworkRecord net )
   {
      int i,j;
      int nl_m1 = net.num_layers - 1;
      int nl_m2 = net.num_layers - 2;
      int out_min = net.x_layer_size[nl_m1];
      int out_max = net.y_layer_size[nl_m1];
      int in_min  = net.x_layer_size[nl_m2];
      int in_max  = net.y_layer_size[nl_m2];
      int in_w_min = net.x_weight_size[nl_m2];
      double [] ej = net.error; //[out_min] - 1
      double [] aj = net.activation; //[out_min]
      double [] dj = net.desired_output; //[out_min]
      double [] bj = net.bias; //[out_min]
      double [] obj = net.old_bias; //[out_min]
      double [] wi = net.weight; //[in_w_min]
      double [] owi = net.old_weight; //[in_w_min]
      double [] ai = net.activation;
      double tmp_b;
      double tmp_w;
      double A1_ej;
      int nlayers;

      for (j = out_min; j <= out_max; j++) {

         /***********************/
         /* calculate the error */
         /***********************/

         ej[j] = (1.0 - (aj[j] * aj[j])) * (dj[j] - aj[j]);

         /***********************/
         /* calculate constants */
         /***********************/

         A1_ej = net.weight_lrate * ej[j];

         /************************/
         /* update the bias term */
         /************************/

         tmp_b = bj[j];
         bj[j] += A1_ej + (net.bias_lrate * (tmp_b - obj[j]));
         obj[j] = tmp_b;

         /******************************/
         /* set the activation pointer */
         /******************************/

         //ai = &net.activation[in_min];

         for (i = in_min; i <= in_max; i++) {

            /**********************/
            /* update the weights */
            /**********************/

            tmp_w = wi[in_w_min];
            wi[in_w_min] += (A1_ej * ai[i]) + (net.momentum_lrate * (tmp_w - owi[in_w_min]));
            owi[in_w_min++] = tmp_w;
         }
      }

      for (nlayers = net.num_layers; nlayers > 2; nlayers--)
         HyperbolicComputeBP2( net, nlayers );
   }

   /*********************************************************
   *                                                        *
   *  HyperbolicComputeBP2                                  *
   *   Computes the weight adjustments on the hidden        *
   *  layers of the neural network.                         *
   *                                                        *
   *  Calling Sequence:                                     *
   *   HyperbolicComputeBP()                                *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void HyperbolicComputeBP2(NetworkRecord net, int nlayers )
   {
      int i,j,k;
      int nl_m1 = nlayers - 1;
      int nl_m2 = nlayers - 2;
      int nl_m3 = nlayers - 3;
      int out_min = net.x_layer_size[nl_m2];
      int out_max = net.y_layer_size[nl_m2];
      int mid_min = net.x_layer_size[nl_m1];
      int mid_max = net.y_layer_size[nl_m1];
      int in_min  = net.x_layer_size[nl_m3];
      int in_max  = net.y_layer_size[nl_m3];
      int out_w_min = net.x_weight_size[nl_m2];
      int in_w_min  = net.x_weight_size[nl_m3];
      double [] ej = net.error; //[out_min] - 1
      double [] aj = net.activation; //[out_min]
      double [] bj = net.bias; //[out_min]
      double [] obj = net.old_bias; //[out_min]
      double [] wk = net.weight;
      double [] ek = net.error;
      double [] wi = net.weight; //[in_w_min]
      double [] owi = net.old_weight; //[in_w_min]
      double [] ai = net.activation;
      double sum;
      double tmp_b;
      double tmp_w;
      double A1_ej;
      int woff = net.layer_size[nl_m2];
      int wx;

      for (j = out_min; j <= out_max; j++) {

         /**************************************/
         /* propagate the error back one layer */
         /**************************************/

         //ek = &net.error[mid_min];
         //wk = &net.weight[out_w_min++];
         wx = out_w_min++;
         sum = 0.0;
         for (k = mid_min; k <= mid_max; k++) {
            sum += ek[k] * wk[wx];
            wx += woff;
         }

         /***********************/
         /* calculate the error */
         /***********************/

         ej[j] = (1.0 - (aj[j] * aj[j])) * sum;

         /***********************/
         /* calculate constants */
         /***********************/

         A1_ej = net.weight_lrate * ej[j];

         /************************/
         /* update the bias term */
         /************************/

         tmp_b = bj[j];
         bj[j] += A1_ej + (net.bias_lrate * (tmp_b - obj[j]));
         obj[j] = tmp_b;

         /******************************/
         /* set the activation pointer */
         /******************************/

         //ai = &net.activation[in_min];

         for (i = in_min; i <= in_max; i++) {

            /**********************/
            /* update the weights */
            /**********************/

            tmp_w = wi[in_w_min];
            wi[in_w_min] += (A1_ej * ai[i]) + (net.momentum_lrate * (tmp_w - owi[in_w_min]));
            owi[in_w_min++] = tmp_w;
         }
      }
   }

   /*--------------------------------------------------------
   *
   * ComputeNetworkOutput - compute the output from a network.
   *
   --------------------------------------------------------*/
   int ComputeNetworkOutput( NetworkRecord net )
   {
      /* Check for a valid pointer */
      if (net == null) {
         return( NetworkRecord.M_ERROR );
      }

      /* Compute the network output */
      if (net.activ_func == NetworkRecord.SIGMOID) {
         SigmoidComputeOutput( net );
      } else {
         HyperbolicComputeOutput( net );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * TrainNetwork - train the network until the stopping criterion.
   *
   --------------------------------------------------------*/
   void TrainNetwork(NetworkRecord net, long [] elapsed_iters )
   {
      long iter;
      int j;
      double pattern_error = 0.0;
      double total_error = 0.0;

      for (iter = net.start_iter; iter < net.MAX_ITERATIONS; iter++) {

         total_error = 0.0;
         SetLearningRate( net, iter );

         for (j = 0; j < net.num_patterns; j ++) {

            SetInputNum( net, j );
            SetDesiredOutputNum( net, j );

            if (net.activ_func == NetworkRecord.SIGMOID) {
               SigmoidComputeOutput( net );
               SigmoidComputeBP( net );
            } else {
               HyperbolicComputeOutput( net );
               HyperbolicComputeBP( net );
            }

            if (net.DISPLAY_NET_IO > 0) {
               PrintInput( net );
               PrintDesiredOutput( net );
               PrintOutput( net );
            }

            pattern_error = Compute_Error( net );
            total_error += pattern_error;
         }
         total_error /= (double) net.num_patterns;
         if ((iter % net.ITERATION_PRINT) == 0) {
            System.out.println("iteration = " + iter + " , te = " + decfmt.format(total_error));
         }
         if (total_error < net.MAX_TOTAL_ERROR || terminate_received() > 0 ||
             (iter+1) == net.MAX_ITERATIONS) {
            elapsed_iters[0] = iter - net.start_iter + 1;
            net.start_iter = iter + 1;
            System.out.println("final = " + iter + ", te = " + decfmt.format(total_error));
            //if (rec_terminate) fprintf(fp,"=====terminated=====\n");
            break;
         }
      }
   }

   /*--------------------------------------------------------
   *
   * TrainNetworkOnePass - train the network for one pass.
   *
   --------------------------------------------------------*/
   void TrainNetworkOnePass(NetworkRecord net, long start_iter, double [] total_error )
   {
      int j;
      double pattern_error = 0.0;
      //#ifndef DOSEXE
      //MSG msg;
      //#endif

      total_error[0] = 0.0;
      SetLearningRate( net, start_iter );
      for (j = 0; j < net.num_patterns; j ++) {

         //#ifndef DOSEXE
         ///* Intercept other messages */
         //if (PeekMessage(&msg,NULL,0,0,PM_REMOVE)) {
         //   TranslateMessage( &msg );
         //   DispatchMessage( &msg );
         //}
         //#endif

         SetInputNum( net, j );
         SetDesiredOutputNum( net, j );

         if (net.activ_func == NetworkRecord.SIGMOID) {
            SigmoidComputeOutput( net );
            SigmoidComputeBP( net );
         } else {
            HyperbolicComputeOutput( net );
            HyperbolicComputeBP( net );
         }

         if (net.DISPLAY_NET_IO > 0) {
            PrintInput( net );
            PrintDesiredOutput( net );
            PrintOutput( net );
         }
         pattern_error = Compute_Error( net );
         total_error[0] += pattern_error;
      }
      total_error[0] /= (double) net.num_patterns;
   }

   /*--------------------------------------------------------
   *
   * TestNetwork - test the network and return the rms error.
   *
   --------------------------------------------------------*/
   void TestNetwork(NetworkRecord net, double [] rms_error )
   {
      int j;
      double pattern_error = 0.0;
      double total_error = 0.0;

      for (j = 0; j < net.num_patterns; j++ ) {

         SetInputNum( net, j );
         SetDesiredOutputNum( net, j );

         if (net.activ_func == NetworkRecord.SIGMOID) {
            SigmoidComputeOutput( net );
         } else {
            HyperbolicComputeOutput( net );
         }

         if (net.DISPLAY_NET_IO > 0) {
            PrintInput( net );
            PrintOutput( net );
            PrintDesiredOutput( net );
         }

         /* Calculate the difference */
         pattern_error = Compute_Error( net );
         total_error += pattern_error;
      }
      rms_error[0] = Math.sqrt (total_error / (double) net.num_patterns);
   }

   /*--------------------------------------------------------
   *
   * ApplyNetwork - apply the network and write result to a file.
   *
   --------------------------------------------------------*/
   int ApplyNetwork(NetworkRecord net, String filename )
   {
      int j;
      int index;

      /* Open the pattern file */
      try {
         FileWriter pfd = new FileWriter(filename);
         BufferedWriter buff = new BufferedWriter(pfd);

         for (j = 0; j < net.num_patterns; j ++) {

            SetInputNum( net, j );

            if (net.activ_func == NetworkRecord.SIGMOID) {
               SigmoidComputeOutput( net );
            } else {
               HyperbolicComputeOutput( net );
            }

            if (net.DISPLAY_NET_IO > 0) {
               PrintInput( net );
               PrintOutput( net );
            }

            if (net.OUTPUT_TYPE == NetworkRecord.MAX_OUTPUT_IDX) {
               /* Find the index of the max output value */
               index = max_output_index( net );
               buff.write( index );
               buff.newLine();
            } else {
               Printf_Output2( buff, net );
            }
         }

         /* Close the file */
         buff.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

}
