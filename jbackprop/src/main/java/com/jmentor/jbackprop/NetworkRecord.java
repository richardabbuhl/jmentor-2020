/*-----------------------------------------------------------
*  Copyright Richard Abbuhl, 2002-2003
* 
* networkrec.java
* R. Abbuhl, 1 November 2002
* %W% %G%
*
* Contains routines which can be used for training
* a neural network.
*
* Revision History:
*
*     12/01/02, R. Abbuhl   
*     Converted to Java.
*
-------------------------------------------------------------*/

package com.jmentor.jbackprop;

/* Define a network record */
public class NetworkRecord {

   /* Misc defines for return values */
   static int  M_ERROR  =  -1;
   static int  M_OK     =   0;

   /* Misc defines for OUTPUT_TYPE */
   static int  NETWORK_OUTPUT   = 1;
   static int  MAX_OUTPUT_IDX   = 2;

   /* Define the maximum number of layers */
   static int  MAX_LAYERS = 5;

   /* Define a type for integers */
   // #define int long

   // #define IN_AMIN( p )   p->x_layer_size[0]
   // #define OUT_AMIN( p )  p->x_layer_size[p->num_layers - 1]

   static int  SIGMOID     = 1;
   static int  HYPERBOLIC  = 2;

   static int  FALSE       = 0;
   static int  TRUE        = 1;

   int version;
   int MEMTEST;
   int OUTPUT_TYPE;
   int DISPLAY_NTF_IO;
   int DISPLAY_PAT_IO;
   int DISPLAY_NET_IO;
   int RESTART_TRAINING;

   int NTF_VERSION;
   long MAX_ITERATIONS;
   double MAX_TOTAL_ERROR;
   long ITERATION_PRINT;
   int RANDOM_INIT_VAL;
   int SAVE_WEIGHTS;
   String WEIGHT_PATH;
   String PATTERN_PATH;
   long elapsed_iters;
   String pattern_file;
   int use_pattern_file;

   /* Define the name of the file for saving weights */
   /* char *weight_filename; */

   /* Adjusted learning rate which is used to train the network.
      It is adjusted by the routine SetLearningRate, and is
      is calculated using the following function.  If
      SetLearningRate is not called then its default value
      is used as the adjusted learning rate (0.7).

           A1 = (a5 * pow(a6, iteration)) + aa1;
   */
   double weight_lrate;      /* Used to be A1 */
   double A1;                /* Used to be aa1 */
   double A2;                /* Used to be a5 */
   double A3;                /* Used to be a6 */
   double momentum_lrate;    /* Used to be a2 */
   double bias_lrate;        /* Used to be a4 */

   /* Beta term used for the forward pass of the
      network.
   */
   /* Ignore for now */
   /* double beta; */
   double unused1;

   /* Miscelaneous variables for the network */
   long start_iter;
   int activ_func;

   /* Number of layers in the network.  It is set by the 
      layer file.
   */
   int num_layers;

   /* Number of processing elements (activation units)
      in the network.  It is calculated by the formula
     
          y_layer_size[num_layers - 1] + 1
   */
   int num_activation;

   /* Starting position of activation units for each layer
      in the network.  For example, if the network
      were a three-layer 2 x 3 x 2 network, x_layer_size
      and y_layer_size should be set as follows.
     
           x_layer_size[0] = 0,  y_layer_size[0] = 1,
           x_layer_size[1] = 2,  y_layer_size[1] = 4,
           x_layer_size[2] = 5,  y_layer_size[2] = 6
     
      These values are read from the layer file.  They
      are used to access the continuous array of
      activation units.
   */
   int [] x_layer_size = new int[ MAX_LAYERS ];
   int [] y_layer_size = new int[ MAX_LAYERS ];

   /* The size of each layers.
   */
   int [] layer_size = new int[ MAX_LAYERS ];

   /*  The continuous activation array holding the
      values of the processing elements calculated
      by the forward propagation pass.  It is a
      continuous array whose layers are accessed
      by using the following arrays.
      
          i = x_array_size[desired_layer],
          j = y_array_size[desired_layer]
     
      then
     
          activation[i] to activation[j]
     
      are the activation units for desired_layer.
   */
   double [] activation;

   /* Number of weights in the network.  It is calculated
      by using values in the layer file.
   */
   int num_weights;
   int [] x_weight_size = new int[ MAX_LAYERS ];
   int [] y_weight_size = new int[ MAX_LAYERS ];
   int [] weight_size = new int[ MAX_LAYERS ];
   double [] weight;
   int hweight;
   double [] old_weight;
   int hold_weight;

   int num_error;
   double [] error;
   int herror;

   int num_bias;
   double [] bias;
   int hbias;
   double [] old_bias;
   int hold_bias;

   int num_desired;
   double [] desired_output;

   int num_patterns;
   int ninputs;
   int noutputs;
   int num_input;
   double [] def_input;
   int hdef_input;
   int num_output;
   double [] def_output;
   int hdef_output;

}
