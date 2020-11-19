/*-----------------------------------------------------------
*  Copyright Richard Abbuhl, 2002-2003
* 
* network.java
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

import java.io.*;
import java.text.DecimalFormat;

import com.jmentor.jnanoxml.*;

public class Network {

   /* Misc defines for printing */
   static DecimalFormat intfmt = DecimalHelper.defaultDecimalFormat("00");
   static DecimalFormat decfmt = DecimalHelper.defaultDecimalFormat("#0.0000000");

   int rec_terminate = NetworkRecord.FALSE;
   int VERBOSE = NetworkRecord.FALSE;

   /* Values used to define the range for initializing
      the weights.  The weights are set using the
      following function.

           randval = (double) random_number_generator;
           weight = weight_init_val1 *
                          (randval - weight_init_val2);
   */
   double weight_init_val1 = 0.5;
   double weight_init_val2 = 0.5;
   double bias_init_val1 = 0.2;
   double bias_init_val2 = 0.5;

   /* Beta term used for the forward pass of the
      network.
   */
   double beta = -1.0;

   /* ON value of a processing element.  It is a close to
      1.0 as possible.
   */
   double ON = 0.95;

   /* OFF value of a processing element.  It is as close
      to 0.0 as possible.
   */
   double OFF = 0.05;

   /* Define variable which reflect the size of storage units */
   //#define S_INT   sizeof(INT)
   //#define S_LONG  sizeof(long)
   //#define S_FLOAT sizeof(double)

   /* Define the format version */
   static int FORMAT_VERSION = 2;

   /* Define IO flags */
   //const char PRINT_LS = 0;
   static int PRINT_IO = 0;

   //extern long total_storage;

   java.util.Random drand48;

   /*********************************************************
   *                                                        *
   *  SetNumberPatterns                                     *
   *   Sets the variable num_patterns to a specified        *
   *  value which initializes the size of the patterns      *
   *  set.                                                  *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SetNumberPatterns(numpat)                            *
   *                                                        *
   *  Input:                                                *
   *   int numpat                                           *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SetNumberPatterns(NetworkRecord net, int numpat )
   {
      net.num_patterns = numpat;
   }

   /*********************************************************
   *                                                        *
   *  Allocate_Network                                      *
   *   Allocates memory for all the variables required      *
   *  for neural network processing.                        *
   *                                                        *
   *  Calling Sequence:                                     *
   *   Allocate_Network()                                   *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   int Allocate_Network( NetworkRecord net )
   {
      int num_layers_m1;
      int i;

      /**********************************/
      /* Allocate memory for activation */
      /**********************************/

      num_layers_m1 = net.num_layers - 1;
      net.num_activation = net.y_layer_size[num_layers_m1] + 1;

      /**************************************************************/
      /* Set weight_size to reflect the number of weights per layer */
      /**************************************************************/

      for (i = 0; i < num_layers_m1; i++) {
         net.weight_size[i] = net.layer_size[i] * net.layer_size[i+1];

         if (i == 0) {
            net.x_weight_size[i] = 0;
            net.y_weight_size[i] = net.weight_size[i] - 1;
         }
         else {
            net.x_weight_size[i] = net.y_weight_size[i-1] + 1;
            net.y_weight_size[i] = net.y_weight_size[i-1] + net.weight_size[i];
         }
         //#ifdef IGNORETHIS
         // if (PRINT_LS) System.out.println("x_weight_size " + i + " = " + net.x_weight_size[i] + "  ");
         // if (PRINT_LS) System.out.println("y_weight_size " + i + " = " + net.y_weight_size[i] + "  ");
         // if (PRINT_LS) System.out.println("weight_size " + i + " = " + net.weight_size[i] + "  ");
         //#endif
      }
      net.num_weights = net.y_weight_size[num_layers_m1 - 1] + 1;
      //#ifdef IGNORETHIS
      // if (PRINT_LS) System.out.println("num_weights = " + net.num_weights);
      //#endif

      /******************************/
      /* Allocate memory for weight */
      /******************************/

      //net.weight = (double [] ) 
      //   MYALLOC(&net.hweight, net.num_weights, sizeof(double));
      net.weight = new double[net.num_weights];
      if (net.weight == null) {
         System.out.println("error allocating " + net.num_weights + "weight");
         return( NetworkRecord.M_ERROR );
      }

      /**********************************/
      /* Allocate memory for old_weight */
      /**********************************/

      //net.old_weight = (double [] ) 
      //   MYALLOC(&net.hold_weight, net.num_weights, sizeof(double));
      net.old_weight = new double[net.num_weights];
      if (net.old_weight == null) {
         System.out.println("error allocating " + net.num_weights + " old_weight");
         return( NetworkRecord.M_ERROR );
      }

      /*****************************/
      /* Allocate memory for error */
      /*****************************/

      net.num_error = net.y_layer_size[num_layers_m1] + 1;
      //net.error = (double [] ) 
      //   MYALLOC(&net.herror, net.num_error, sizeof(double));
      net.error = new double[net.num_error];
      if (net.error == null) {
         System.out.println("error allocating " + net.num_error + " error");
         return( NetworkRecord.M_ERROR );
      }

      /****************************/
      /* Allocate memory for bias */
      /****************************/

      net.num_bias = net.y_layer_size[num_layers_m1] + 1;
      //net.bias = (double [] ) 
      //   MYALLOC(&net.hbias, net.num_bias, sizeof(double));
      net.bias = new double[net.num_bias];
      if (net.bias == null) {
         System.out.println("error allocating " + net.num_bias + " bias");
         return( NetworkRecord.M_ERROR );
      }

      /********************************/
      /* Allocate memory for old_bias */
      /********************************/

      //net.old_bias = (double [] ) 
      //   MYALLOC(&net.hold_bias, net.num_bias, sizeof(double));
      net.old_bias = new double[net.num_bias];
      if (net.old_bias == null) {
         System.out.println("error allocating " + net.num_bias + " old_bias\n");
         return( NetworkRecord.M_ERROR );
      }

      /**************************************/
      /* Allocate memory for desired_output */
      /**************************************/

      net.num_desired = net.y_layer_size[num_layers_m1] + 1;

      /* Return ok */
      return( NetworkRecord.M_OK );
   }

   int Allocate_Patterns( NetworkRecord net )
   {
      int num_layers_m1;

      /**********************************/
      /* Allocate memory for def_input */
      /**********************************/

      num_layers_m1 = net.num_layers - 1;
      net.num_input = net.y_layer_size[num_layers_m1] + 1;
      //net.def_input = (double [] ) MYALLOC(&net.hdef_input,
      //   net.num_patterns * net.num_input, sizeof(double));
      net.def_input = new double[net.num_patterns * net.num_input];
      if (net.def_input == null) {
         System.out.println("error allocating " + net.num_input + " def_input");
         return( NetworkRecord.M_ERROR );
      }

      /**********************************/
      /* Allocate memory for activation */
      /**********************************/

      net.activation = new double[net.num_input];
      if (net.activation == null) {
         System.out.println("error allocating " + net.num_input + " def_input");
         return( NetworkRecord.M_ERROR );
      }


      //#ifdef IGNORETHIS
      //int i;
      //for (i = 0; i < net.num_patterns; i++) {
      //   net.def_input[i] = (double [] )
      //      MYALLOC(&net.hdef_input, net.num_input, sizeof(double));
      //   if (net.def_input[i] == null) {
      //      #ifdef DOSEXE
      //       System.out.println("error allocating %d def_input[%d]\n",net.num_input,i);
      //      #endif
      //      return( networkrec.M_ERROR );
      //   }
      //}
      //#endif

      /**********************************/
      /* Allocate memory for def_output */
      /**********************************/

      net.num_output = net.y_layer_size[num_layers_m1] + 1;
      //net.def_output = (double [] ) MYALLOC(&net.hdef_output,
      //   net.num_patterns * net.num_output, sizeof(double));
      net.def_output = new double[net.num_patterns * net.num_output];
      if (net.def_output == null) {
         System.out.println("error allocating " + net.num_output + " def_output");
         return( NetworkRecord.M_ERROR );
      }

      /**************************************/
      /* Allocate memory for desired_output */
      /**************************************/

      net.desired_output = new double[net.num_output];
      if (net.desired_output == null) {
         System.out.println("error allocating " + net.num_output + " def_output");
         return( NetworkRecord.M_ERROR );
      }

      //#ifdef IGNORETHIS
      //for (i = 0; i < net.num_patterns; i++) {
      //   net.def_output[i] = (double [] )
      //      MYALLOC(&net.hdef_output, net.num_output, sizeof(double));
      //   if (net.def_output[i] == null) {
      //      #ifdef DOSEXE
      //       System.out.println("error allocating " + net.num_output + " def_output[" + i + "]");
      //      #endif
      //      return( networkrec.M_ERROR );
      //   }
      //}
      //#endif

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * Free_Network -
   *
   --------------------------------------------------------*/
   void Free_Network( NetworkRecord net )
   {
      /******************************/
      /* Allocate memory for weight */
      /******************************/

      if (net.weight != null) {
         //MYFREE( net.hweight, (char *) net.weight );
         net.weight = null;
      }

      /**********************************/
      /* Allocate memory for old_weight */
      /**********************************/

      if (net.old_weight != null) {
         //MYFREE( net.hold_weight, (char *) net.old_weight );
         net.old_weight = null;
      }

      /*****************************/
      /* Allocate memory for error */
      /*****************************/

      if (net.error != null) {
         //MYFREE( net.herror, (char *) net.error );
         net.error = null;
      }

      /****************************/
      /* Allocate memory for bias */
      /****************************/

      if (net.bias != null) {
         //MYFREE( net.hbias, (char *) net.bias );
         net.bias = null;
      }

      /********************************/
      /* Allocate memory for old_bias */
      /********************************/

      if (net.old_bias != null) {
         //MYFREE( net.hold_bias, (char *) net.old_bias );
         net.old_bias = null;
      }
   }

   /*--------------------------------------------------------
   *
   * Free_Patterns -
   *
   --------------------------------------------------------*/
   void Free_Patterns( NetworkRecord net )
   {
      /*****************************/
      /* Free memory for def_input */
      /*****************************/

      if (net.def_input != null) {
         //MYFREE( net.hdef_input, (char *) net.def_input );
         net.def_input = null;
      }

      /******************************/
      /* Free memory for def_output */
      /******************************/

      if (net.def_output != null) {
         //MYFREE( net.hdef_output, (char *) net.def_output );
         net.def_output = null;
      }
   }

   /*********************************************************
   *
   *  WriteHeader()
   *   Writes to a file which configures the layer size
   *  of the neural network.
   *
   *  Calling Sequence:
   *   WriteHeader(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   void WriteHeader(NetworkRecord net , DataOutputStream file )
   {
      int version = FORMAT_VERSION;
      java.util.Date tmpDate = new java.util.Date();
      String tmpstring = tmpDate.toString();

      try {

      /******************/
      /* Print a header */
      /******************/
      if (PRINT_IO > 0) System.out.println("WriteHeader--------------------------");

      /***********************************/
      /* Write the format version number */
      /***********************************/

      writeInt(file, version);
      if (PRINT_IO > 0) System.out.println("format version = " + version);

      /*****************************/
      /* Write the start iteration */
      /*****************************/

      writeInt(file, (int)net.start_iter);
      if (PRINT_IO > 0) System.out.println("start_iter = " + net.start_iter);

      /*********************************/
      /* Write the activation function */
      /*********************************/

      writeInt(file, net.activ_func);
      if (PRINT_IO > 0) System.out.println("activ_func = " + net.activ_func);

      /*************************************/
      /* Write the current time in seconds */
      /*************************************/

      writeInt(file, tmpstring.length());
      file.writeBytes(tmpstring);
      if (PRINT_IO > 0) System.out.println("timestamp = " + tmpstring );

      /***********************/
      /* Write the beta term */
      /***********************/

      writeDouble(file, net.unused1);
      // /* if (PRINT_IO) System.out.println("beta = %f\n", net.beta); */
      if (PRINT_IO > 0) System.out.println("unused = " + net.unused1);

      /******************************/
      /* Write the number of layers */
      /******************************/

      writeInt(file, net.num_layers);
      if (PRINT_IO > 0) System.out.println("number of layers = " + net.num_layers);

      /*************************************/
      /* Read the layer configuration file */
      /*************************************/

      for (int i = 0; i < net.num_layers; ++i) {

         /**************************/
         /* Write the x layer size */
         /**************************/

         writeInt(file, net.x_layer_size[i]);
         if (PRINT_IO > 0)
            System.out.print("x_layer " + i + " = " + net.x_layer_size[i] + "  ");

         /**************************/
         /* Write the x layer size */
         /**************************/

         writeInt(file, net.y_layer_size[i]);
         if (PRINT_IO > 0) System.out.println("y_layer " + i + " = " + net.y_layer_size[i]);
      }
      if (PRINT_IO > 0) System.out.println("\n");

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   /*********************************************************
   *
   *  WriteWeights()
   *   Writes the weights to a file.
   *
   *  Calling Sequence:
   *   WriteWeights(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   void WriteWeights(NetworkRecord net , DataOutputStream file )
   {
      try {

         /******************/
         /* Print a header */
         /******************/
         if (PRINT_IO > 0) System.out.println("WriteWeights-------------------------");

         /*********************************/
         /* Write the weights to the file */
         /*********************************/

         for (int i = 0; i < net.num_weights; i++) {

            /**************************/
            /* Write the weight value */
            /**************************/

            writeDouble(file, net.weight[i]);
            if (PRINT_IO > 0) System.out.println("weight " + i + " = " + net.weight[i]);

            /******************************/
            /* Write the old weight value */
            /******************************/

            writeDouble(file, net.old_weight[i]);
            if (PRINT_IO > 0) System.out.println("old_weight " + i + " = " + net.old_weight[i]);
         }
         if (PRINT_IO > 0) System.out.println("\n");

      } catch (Exception e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   /*********************************************************
   *
   *  WriteBias()
   *   Writes the bias values to a file.
   *
   *  Calling Sequence:
   *   WriteBias(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   void WriteBias(NetworkRecord net, DataOutputStream file )
   {
      try {

         /******************/
         /* Print a header */
         /******************/
         if (PRINT_IO > 0) System.out.println("WriteBias----------------------------");

         /*************************************/
         /* Write the bias values to the file */
         /*************************************/

         for (int i = 0; i < net.num_bias; i++) {

            /************************/
            /* Write the bias value */
            /************************/

            writeDouble(file, net.bias[i]);
            if (PRINT_IO > 0) System.out.println("bias " + i + " = " + net.bias[i]);

            /****************************/
            /* Write the old bias value */
            /****************************/

            writeDouble(file, net.old_bias[i]);
            if (PRINT_IO > 0) System.out.println("old_bias " + i + " = " + net.old_bias[i]);
         }

         if (PRINT_IO > 0) System.out.println("\n");

      } catch (Exception e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   /*********************************************************
   *
   *  SaveWeights()
   *   Save the weights to a file.
   *
   *  Calling Sequence:
   *   SaveWeights()
   *
   *  Input:
   *   None
   *
   *  Output:
   *   None
   *
   *********************************************************/
   int SaveWeights(NetworkRecord net, String filename )
   {
      /**********************************************/
      /* Open the file which configures the network */
      /**********************************************/

      try {
         /* Open the weights file for writing */
         FileOutputStream pfd = new FileOutputStream(filename);
         DataOutputStream file = new DataOutputStream(pfd);

         /*******************************/
         /* Save the header to the file */
         /*******************************/

         WriteHeader( net, file );

         /********************************/
         /* Save the weights to the file */
         /********************************/

         WriteWeights( net, file );

         /************************************/
         /* Save the bias values to the file */
         /************************************/

         WriteBias( net, file );

         /******************/
         /* Close the file */
         /******************/

         file.close();

      } catch (IOException e) {
         System.out.println( "Could not open weights file '%s' for writing\n" + net.WEIGHT_PATH );
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*********************************************************
   *
   *  ReadHeader()
   *   Reads a file which configures the layer size
   *  of the neural network.
   *
   *  Calling Sequence:
   *   ReadHeader(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   int ReadHeader(NetworkRecord net, DataInputStream file )
   {
      int version;
      String tmpstring = new String();
      int tmpstringlen;

      try {

      /******************/
      /* Print a header */
      /******************/
      if (PRINT_IO > 0) System.out.println("ReadHeader---------------------------");

      /***********************************/
      /* Read the format version number */
      /***********************************/

      version = readInt(file);
      if (PRINT_IO > 0) System.out.println("format version = " + version);
      if (version != FORMAT_VERSION) {
         System.out.println("Incompatible weight version " + version);
         return( NetworkRecord.M_ERROR );
      }

      /****************************/
      /* Read the start iteration */
      /****************************/

      net.start_iter = readInt(file);
      if (PRINT_IO > 0) System.out.println("start_iter = " + net.start_iter);

      /********************************/
      /* Read the activation function */
      /********************************/

      net.activ_func = readInt(file);
      if (PRINT_IO > 0) System.out.println("activ_func = " + net.activ_func);

      /************************************/
      /* Read the current time in seconds */
      /************************************/

      tmpstringlen = readInt(file);
      for (int i = 0; i < tmpstringlen; i++) tmpstring += (char)file.readUnsignedByte();
      if (PRINT_IO > 0) System.out.println("timestamp = " + tmpstring );

      /***********************/
      /* Read the beta term */
      /***********************/

      net.unused1 = readDouble(file);
      /* if (PRINT_IO) System.out.println("beta = %f\n", net.beta ); */
      if (PRINT_IO > 0) System.out.println("unused = " + net.unused1 );

      /*****************************/
      /* Read the number of layers */
      /*****************************/

      net.num_layers = readInt(file);
      if (net.num_layers < 0 || net.num_layers > NetworkRecord.MAX_LAYERS) {
         System.out.println ("Number of layers not specifed" );
         return( NetworkRecord.M_ERROR );
      }
      if (PRINT_IO > 0) System.out.println("number of layers = " + net.num_layers);

      /*************************************/
      /* Read the layer configuration file */
      /*************************************/

      for (int i = 0; i < NetworkRecord.MAX_LAYERS; i++) {
         net.layer_size[i] = 0;
         net.x_layer_size[i] = 0;
         net.y_layer_size[i] = 0;
      }

      for (int i = 0; i < net.num_layers; ++i) {
         net.x_layer_size[i] = readInt(file);
         if (net.x_layer_size[i] < 0) {
            System.out.println ("x_layer_size " + i + " not specifed");
            return( NetworkRecord.M_ERROR );
         }
         if (PRINT_IO > 0) System.out.println("x_layer " + i + " = " + net.x_layer_size[i] + "  ");

         net.y_layer_size[i] = readInt(file);
         if (net.y_layer_size[i] < 0) {
            System.out.println ("y_layer_size " + i + " not specifed");
            return( NetworkRecord.M_ERROR );
         }
         if (PRINT_IO > 0) System.out.println("y_layer " + i + " = " + net.y_layer_size[i] + "  ");

         net.layer_size[i] = net.y_layer_size[i] - net.x_layer_size[i] + 1;
         if (PRINT_IO > 0) System.out.println("layer_size = " + net.layer_size[i]);
      }
      if (PRINT_IO > 0) System.out.println("\n");

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*********************************************************
   *
   *  ReadWeights()
   *   Reads the weights from a file.
   *
   *  Calling Sequence:
   *   ReadWeights(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   void ReadWeights(NetworkRecord net, DataInputStream file )
   {
      /******************/
      /* Print a header */
      /******************/
      //#ifdef DOSEXE
      // if (PRINT_IO) System.out.println("ReadWeights--------------------------\n");
      //#endif

      /*********************************/
      /* Read the weights from the file */
      /*********************************/

      for (int i = 0; i < net.num_weights; i++) {

         /*************************/
         /* Read the weight value */
         /*************************/

         net.weight[i] = readDouble(file);
         if (PRINT_IO > 0) System.out.println("weight " + i + " = " + net.weight[i]);

         /*****************************/
         /* Read the old weight value */
         /*****************************/

         net.old_weight[i] = readDouble(file);
         if (PRINT_IO > 0) System.out.println("old_weight " + i + " = " + net.old_weight[i]);
      }
      if (PRINT_IO > 0) System.out.println("\n");
   }

   /*********************************************************
   *
   *  ReadBias()
   *   Reads the bias values from a file.
   *
   *  Calling Sequence:
   *   ReadBias(file)
   *
   *  Input:
   *   FILE *file
   *
   *  Output:
   *   None
   *
   *********************************************************/
   void ReadBias(NetworkRecord net, DataInputStream file )
   {
      /******************/
      /* Print a header */
      /******************/
      if (PRINT_IO > 0) System.out.println("ReadBias-----------------------------");

      /**************************************/
      /* Read the bias values from the file */
      /**************************************/

      for (int i = 0; i < net.num_bias; i++) {

         /***********************/
         /* Read the bias value */
         /***********************/

         net.bias[i] = readDouble(file);
         if (PRINT_IO > 0) System.out.println("bias " + i + " = " + net.bias[i]);

         /***************************/
         /* Read the old bias value */
         /***************************/

         net.old_bias[i] = readDouble(file);
         if (PRINT_IO > 0) System.out.println("old_bias " + i + " = " + net.old_bias[i]);
      }
      if (PRINT_IO > 0) System.out.println("\n");
   }

   /*********************************************************
   *
   *  RetrieveWeightsHeader()
   *   Retrieve the weights from a file.
   *
   *  Calling Sequence:
   *   RetrieveWeightsHeader(init)
   *
   *  Input:
   *   int init
   *
   *  Output:
   *   None
   *
   *********************************************************/
   int RetrieveWeightsHeader(NetworkRecord net, String filename )
   {
      try {

         /**********************************************/
         /* Open the file which configures the network */
         /**********************************************/

         FileInputStream pfd = new FileInputStream(filename);
         DataInputStream file = new DataInputStream(pfd);
         int result;

         /*************************************/
         /* Retrieve the header from the file */
         /*************************************/

         result = ReadHeader( net, file );
         if (result < 0) {
            return( result );
         }

         /******************/
         /* Close the file */
         /******************/

         file.close();

      } catch (IOException e) {
         System.out.println( "Could not open weights file " + net.WEIGHT_PATH );
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*********************************************************
   *
   *  RetrieveWeights()
   *   Retrieve the weights from a file.
   *
   *  Calling Sequence:
   *   RetrieveWeights(init)
   *
   *  Input:
   *   int init
   *
   *  Output:
   *   None
   *
   *********************************************************/
   int RetrieveWeights(NetworkRecord net, String filename )
   {
      int result;
      NetworkRecord temp_net = new NetworkRecord();

      try {

         /**********************************************/
         /* Open the file which configures the network */
         /**********************************************/

         FileInputStream pfd = new FileInputStream(filename);
         DataInputStream file = new DataInputStream(pfd);

         /*************************************/
         /* Retrieve the header from the file */
         /*************************************/

         result = ReadHeader( temp_net, file );
         if (result < 0) {
            return( result );
         }

         /**************************************/
         /* Retrieve the weights from the file */
         /**************************************/

         ReadWeights( net, file );

         /******************************************/
         /* Retrieve the bias values from the file */
         /******************************************/

         ReadBias( net, file );

         /******************/
         /* Close the file */
         /******************/

         file.close();

      } catch (IOException e) {
         System.out.println( "Could not open weights file " + net.WEIGHT_PATH );
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*********************************************************
   *                                                        *
   *  Init_Weights                                          *
   *   Initializes the weights to random values.            *
   *                                                        *
   *  Calling Sequence:                                     *
   *   Init_Weights()                                       *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   int Init_Weights( NetworkRecord net )
   {
      int i;
      double [] w = net.weight;
      double [] ow = net.old_weight;
      double [] b = net.bias;
      double [] ob = net.old_bias;
      double randomval;

      for (i = 0; i < net.num_weights; i++) {
         randomval = drand48.nextDouble();
         w[i] = weight_init_val1 * (randomval - weight_init_val2);
         ow[i] = w[i];
      }

      for (i = 0; i < net.num_bias; i++) {
         randomval = drand48.nextDouble();
         b[i] = bias_init_val1 * (randomval - bias_init_val2);
         ob[i] = b[i];
      }
      return( NetworkRecord.M_OK );
   }

   /*********************************************************
   *                                                        *
   *  PrintWeights                                          *
   *   Outputs the weight values.                           *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintWeights()                                       *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintWeights( NetworkRecord net )
   {
      int i;
      double [] w = net.weight;
      double [] ow = net.old_weight;

      for (i = 0; i < net.num_weights; i++) {
         System.out.println("weight ( " + i + " ) = " + w[i] + " old_weight = " + ow[i] );
      }
   }

   /*********************************************************
   *                                                        *
   *  PrintBias                                             *
   *   Outputs the bias values.                             *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintBias()                                          *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintBias( NetworkRecord net )
   {
      int i;
      double [] b = net.bias;
      double [] ob = net.old_bias;

      //#ifdef DOSEXE
      // for (i = 0; i < net.num_bias; i++) {
      //    System.out.println("bias ( " + i + " ) = %f old_bias = %f\n" + *b++ + *ob++);
      // }
      //#endif
   }

   /*********************************************************
   *                                                        *
   *  PrintActivation                                       *
   *   Print the activation values.                         *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintActivation()                                    *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintActivation( NetworkRecord net )
   {
      int i;
      double [] a = net.activation;

      for (i = 0; i < net.num_activation; i++) {
         System.out.println("activation ( " + i + " ) = " + a[i]);
      }
   }

   /*********************************************************
   *                                                        *
   *  PrintError                                            *
   *   Print the error values.                              *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintError()                                         *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintError( NetworkRecord net )
   {
      int i;
      double [] e = net.error;

      //#ifdef DOSEXE
      // for (i = 0; i < net.num_error; i++) {
      //    System.out.println("error ( " + i + " ) = %f\n" + *e++);
      // }
      //#endif
   }

   /*********************************************************
   *                                                        *
   *  SetInputNum                                           *
   *   Sets the input pattern to the neural network.        *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SetInputNum(num)                                     *
   *                                                        *
   *  Input:                                                *
   *   int num                                              *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SetInputNum(NetworkRecord net, int num )
   {
      System.arraycopy( net.def_input, num * net.num_input,
        net.activation, 0, net.num_input );
   }

   /*********************************************************
   *                                                        *
   *  SetDesiredOutputNum                                   *
   *   Sets the desired output pattern to the neural        *
   *   network.                                             *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SetDesiredOutputNum(num)                             *
   *                                                        *
   *  Output:                                               *
   *   int num                                              *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SetDesiredOutputNum(NetworkRecord net, int num )
   {
      System.arraycopy( net.def_output, num * net.num_output,
        net.desired_output, 0, net.num_output );
   }

   /*********************************************************
   *                                                        *
   *  PrintInput                                            *
   *   Print the input pattern to the neural network.       *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintInput()                                         *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintInput( NetworkRecord net )
   {
      int i;
      int amin = net.x_layer_size[0];
      int amax = net.y_layer_size[0];
      double [] ai = net.activation; //[amin]

      for (i = amin; i <= amax; i++) {
         System.out.println("activation(" + i + ") = " + ai[i] );
      }
   }

   /*********************************************************
   *                                                        *
   *  PrintDesiredOutput                                    *
   *   Prints the desired output pattern to the             *
   *  neural network.                                       *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintDesiredOutput()                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintDesiredOutput( NetworkRecord net )
   {
      int i;
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      double [] des_out = net.desired_output; //[amin]

      for (i = amin; i <= amax; i++) {
         System.out.println("desired_output(" + i + ") = " + des_out[i]);
      }
   }

   /*********************************************************
   *                                                        *
   *  PrintOutput                                           *
   *   Prints the output pattern to the neural network.     *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintOutput()                                        *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintOutput( NetworkRecord net )
   {
      int i;
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      double [] ai = net.activation; //[amin]

      for (i = amin; i <= amax; i++) {
         System.out.println("activation(" + i + ") = " + ai[i]);
      }
   }

   /*********************************************************
   *                                                        *
   *  PrintOutput2                                          *
   *   Prints the output pattern to the neural network.     *
   *                                                        *
   *  Calling Sequence:                                     *
   *   PrintOutput()                                        *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void PrintOutput2( NetworkRecord net )
   {
      int i;
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      double [] ai = net.activation; //[amin]

      for (i = amin; i <= amax; i++) {
         System.out.println(ai[i]);
      }
   }

   /*********************************************************
   *                                                        *
   *  DisplayParams                                         *
   *   Displays the current value of parameters used for    *
   *  calculations involving the network.                   *
   *                                                        *
   *  Calling Sequence:                                     *
   *   DisplayParams()                                      *
   *                                                        *
   *  Input:                                                *
   *   None                                                 *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void DisplayParams( NetworkRecord net )
   {
      // System.out.println("-------------------------------------\n");
      // System.out.println("A1 = %f\n",net.A1);
      // System.out.println("A2 = %f\n",net.A2);
      // System.out.println("A3 = %f\n",net.A3);
      // System.out.println("Momentum = %f\n",net.momentum_lrate );
      // System.out.println("Bias = %f\n",net.bias_lrate );
      // if (net.activ_func == SIGMOID) {
      //    System.out.println("ACTIVATION FUNCTION = Sigmoid\n");
      // } else {
      //    System.out.println("ACTIVATION FUNCTION = Hyperbolic\n");
      // }
      // #ifdef IGNORETHIS
      // /* System.out.println("beta = %f\n",net.beta); */
      // System.out.println("beta = %f\n",beta);
      // System.out.println("weight_init_val1 = %f  ",weight_init_val1);
      // System.out.println("weight_init_val2 = %f\n",weight_init_val2);
      // System.out.println("bias_init_val1   = %f  ",bias_init_val1);
      // System.out.println("bias_init_val2   = %f\n",bias_init_val2);
      // #endif
      // System.out.println("Total allocated memory  = %ldK\n",total_storage/1024);
   }

   /*********************************************************
   *                                                        *
   *  power                                                 *
   *   Returns a value of x raised to the nth power.        *
   *                                                        *
   *  Calling Sequence:                                     *
   *   double = power(x,n)                                   *
   *                                                        *
   *  Input:                                                *
   *   double x                                              *
   *   long n                                               *
   *                                                        *
   *  Output:                                               *
   *   double power(x,n)                                     *
   *                                                        *
   *********************************************************/
   double power( double x, long n )
   {
      return(Math.exp( n * Math.log(x) ));
   }

   /*********************************************************
   *                                                        *
   *  SetLearningRate                                       *
   *   Calculates the value of the network learning rate    *
   *  using the current iteration number.                   *
   *                                                        *
   *  Calling Sequence:                                     *
   *   SetLearningRate(iteration)                           *
   *                                                        *
   *  Input:                                                *
   *   int iteration                                        *
   *                                                        *
   *  Output:                                               *
   *   None                                                 *
   *                                                        *
   *********************************************************/
   void SetLearningRate(NetworkRecord net, long iteration )
   {
      net.weight_lrate = net.A1 + (net.A2 * power(net.A3, iteration));
   }

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

   /*--------------------------------------------------------
   *
   * Create_Network_Rec -
   *
   --------------------------------------------------------*/
   NetworkRecord Create_Network_Rec(int hHandle )
   {
      NetworkRecord net;

      /* Allocate a network record */
      //net = (NETWORK_REC *) MYALLOC( hHandle, (INT)1, (INT)sizeof( NETWORK_REC ) );
      net = new NetworkRecord();

      /* Set to basic values */
      if (net != null) {
         Init_Network_Rec( net );
      }

      /* Return the record */
      return( net );
   }

   /*--------------------------------------------------------
   *
   * Init_Network_Rec -
   *
   --------------------------------------------------------*/
   void Init_Network_Rec( NetworkRecord net )
   {
      /* Set the default output files */
      net.pattern_file = null;
      net.use_pattern_file = NetworkRecord.FALSE;

      /* Set some defaults */
      net.MEMTEST = NetworkRecord.FALSE;
      net.DISPLAY_NTF_IO = NetworkRecord.FALSE;
      net.DISPLAY_PAT_IO = NetworkRecord.FALSE;
      net.DISPLAY_NET_IO = NetworkRecord.FALSE;
      net.RESTART_TRAINING = NetworkRecord.FALSE;
      net.num_patterns = 0;

      net.version = 2;
      net.weight_lrate = 0.7;
      net.A1 = 0.7;
      net.A2 = 0.3;
      net.A3 = 0.9;
      net.momentum_lrate = 0.1;
      net.bias_lrate = 0.1;
      net.start_iter = 0;
      net.activ_func = NetworkRecord.HYPERBOLIC;
      net.OUTPUT_TYPE = NetworkRecord.NETWORK_OUTPUT;

      net.MAX_ITERATIONS = 10000;
      net.MAX_TOTAL_ERROR = 0.01;
      net.ITERATION_PRINT = 1;
      net.RANDOM_INIT_VAL = 21;

      net.WEIGHT_PATH = new String();
      net.PATTERN_PATH = new String();

      //net.num_layers = 3;
      //net.layer_size[0] = 1;
      //net.layer_size[1] = 1;
      //net.layer_size[2] = 1;
      //net.layer_size[3] = 0;
      //net.layer_size[4] = 0;
      Set_Layer_Info( net, 3, 1, 1, 1, 0, 0 );

     /* net.beta = -1.0; */
     net.unused1 = -1.0;
   }

   /*--------------------------------------------------------
   *
   * Free_Network_Rec -
   *
   --------------------------------------------------------*/
   void Free_Network_Rec(NetworkRecord net, int hHandle )
   {
      /* Free the network record */
      if (net != null) {
         //MYFREE( hHandle, (char *) net );
         net = null;
      }
   }

   /*--------------------------------------------------------------------
   *
   * Set_Layer_Info
   *
   --------------------------------------------------------------------*/
   int Set_Layer_Info(NetworkRecord net, int num_layers,
                      int layer_1, int layer_2, int layer_3, int layer_4, int layer_5 )
   {
      int i;
      int num_layers_m1;

      if (net == null) {
         return( NetworkRecord.M_ERROR );
      }

      net.num_layers = num_layers;
      net.layer_size[0] = layer_1;
      net.layer_size[1] = layer_2;
      net.layer_size[2] = layer_3;
      net.layer_size[3] = layer_4;
      net.layer_size[4] = layer_5;

      /* Set the x and y vectors */
      for (i = 0; i < net.num_layers; i++) {
         if (i == 0) {
            net.x_layer_size[i] = 0;
            net.y_layer_size[i] = net.layer_size[i] - 1;
         }
         else {
            net.x_layer_size[i] = net.y_layer_size[i-1] + 1;
            net.y_layer_size[i] = net.y_layer_size[i-1] + net.layer_size[i];
         }
      }

      /* Set the number of inputs and outputs */
      num_layers_m1 = net.num_layers - 1;
      net.num_input = net.y_layer_size[num_layers_m1] + 1;
      net.num_output = net.y_layer_size[num_layers_m1] + 1;
      net.ninputs = net.layer_size[0];
      net.noutputs = net.layer_size[num_layers_m1];

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * Random_Init - init the random seed.
   *
   --------------------------------------------------------*/
   int Random_Init( NetworkRecord net )
   {
      /* Initialize the random seed */
      drand48 = new java.util.Random( net.RANDOM_INIT_VAL );

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*-----------------------------------------------------------
   * 
   * MTime_Stamp - timestamp a file by printing the current 
   *               date and time.
   *
   -------------------------------------------------------------*/
   int MTime_Stamp( long tval )
   {
      //time( tval );
      return( NetworkRecord.M_OK );
   }


   /*-----------------------------------------------------------
   * 
   * MElapsed_Time - display the elapsed time between calls
   *                 to TimeStamp() in hours, minutes, and
   *                 seconds.
   *
   -------------------------------------------------------------*/
   int MElapsed_Time( String elapsed_string, long tval1, long tval2 )
   {
   //   long tval = (long) difftime(tval2,tval1);
   //   long aa=3600;
   //   long bb=60;
   //   long hours = tval / aa;
   //   long mins  = (tval % aa) / bb;
   //   long secs  = (tval % aa) % bb;
   //   sprintf( elapsed_string,"%ld:%02ld:%02ld\n",hours,mins,secs);
      return( NetworkRecord.M_OK );
   }

   int readInt(DataInputStream file)
   {
      try {
         int b4 = file.readUnsignedByte();
         int b3 = file.readUnsignedByte();
         int b2 = file.readUnsignedByte();
         int b1 = file.readUnsignedByte();
         return((b1 << 24) | (b2 << 16) + (b3 << 8) + b4);

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( -1 );
      }
   }

   long readLong(DataInputStream file)
   {
      try {
         int b8 = file.readUnsignedByte();
         int b7 = file.readUnsignedByte();
         int b6 = file.readUnsignedByte();
         int b5 = file.readUnsignedByte();
         int b4 = file.readUnsignedByte();
         int b3 = file.readUnsignedByte();
         int b2 = file.readUnsignedByte();
         int b1 = file.readUnsignedByte();
         return( ((long)b1 << 56) +
                 ((long)b2 << 48) +
                 ((long)b3 << 40) +
                 ((long)b4 << 32) +
                 ((long)b5 << 24) +
                 (b6 << 16) +
                 (b7 << 8) + b8 );

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( -1 );
      }
   }


   double readDouble(DataInputStream file)
   {
     return(java.lang.Double.longBitsToDouble(readLong(file)));
   }

   void writeInt(DataOutputStream file, int value)
   {
      try {
         byte b1, b2, b3, b4;
         b4 = (byte)value;
         file.writeByte(b4);
         b3 = (byte)(value >> 8);
         file.writeByte(b3);
         b2 = (byte)(value >> 16);
         file.writeByte(b2);
         b1 = (byte)(value >> 24);
         file.writeByte(b1);

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   void writeLong(DataOutputStream file, long value)
   {
      try {
         byte b1, b2, b3, b4, b5, b6, b7, b8;
         b1 = (byte)value;
         file.writeByte(b1);
         b2 = (byte)(value >> 8);
         file.writeByte(b2);
         b3 = (byte)(value >> 16);
         file.writeByte(b3);
         b4 = (byte)(value >> 24);
         file.writeByte(b4);
         b5 = (byte)(value >> 32);
         file.writeByte(b5);
         b6 = (byte)(value >> 40);
         file.writeByte(b6);
         b7 = (byte)(value >> 48);
         file.writeByte(b7);
         b8 = (byte)(value >> 56);
         file.writeByte(b8);

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
      }
   }

   void writeDouble(DataOutputStream file, double value)
   {
     writeLong(file, java.lang.Double.doubleToLongBits(value));
   }


   /*--------------------------------------------------------
   *
   * Read_NTF_File
   *
   --------------------------------------------------------*/
   int Read_NTF_File(NetworkRecord net, String filename )
   {
      int wlen;
      int num_layers_m1;
      int fd;


      try {
         /* Open the NTF file for reading */
         FileInputStream pfd = new FileInputStream(filename);
         DataInputStream file = new DataInputStream(pfd);

         /* Read the ntf version indicator */
         net.version = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("version = " + net.version);

         /* Read the misc indicators */
         net.activ_func = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("activ_func = " + net.activ_func);

         net.MAX_ITERATIONS = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("max_iterations = " + net.MAX_ITERATIONS);

         net.MAX_TOTAL_ERROR = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("MAX_TOTAL_ERROR = " + net.MAX_TOTAL_ERROR);

         net.OUTPUT_TYPE = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("OUTPUT_TYPE = " + net.OUTPUT_TYPE);

         net.ITERATION_PRINT = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("ITERATION_PRINT = " + net.ITERATION_PRINT);

         net.SAVE_WEIGHTS = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("SAVE_WEIGHTS = " + net.SAVE_WEIGHTS);

         wlen = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("wlen = " + wlen);

         for (int i = 0; i < wlen; i++) net.WEIGHT_PATH += (char)file.readUnsignedByte();
         if (net.DISPLAY_NTF_IO > 0) System.out.println("WEIGHT_PATH = " + net.WEIGHT_PATH);
         //if (wlen > 0) net.WEIGHT_PATH[wlen] = 0;

         // The pattern file is now included in the ntf file.
         if (net.version == 2) {

            wlen = readInt(file);
            if (net.DISPLAY_NTF_IO > 0) System.out.println("wlen = " + wlen);

            for (int i = 0; i < wlen; i++) net.PATTERN_PATH += (char)file.readUnsignedByte();
            if (net.DISPLAY_NTF_IO > 0) System.out.println("PATTERN_PATH = " + net.PATTERN_PATH);
            //if (wlen > 0) net.PATTERN_PATH[wlen] = 0;
         }
     
         /* Read the training parameters */
         net.A1 = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A1 = " + net.A1);
     
         net.momentum_lrate = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("momentum = " + net.momentum_lrate);
     
         net.bias_lrate = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("bias = " + net.bias_lrate);
     
         net.A2 = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A2 = " + net.A2);
     
         net.A3 = readDouble(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A3 = " + net.A3);
     
         net.RANDOM_INIT_VAL = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("RANDOM_INIT_VAL = " + net.RANDOM_INIT_VAL);
     
         /* Read the configuration parameters */
         net.num_layers = readInt(file);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("num_layers = " + net.num_layers);
     
         /*************************************/
         /* Read the layer configuration file */
         /*************************************/
         for (int i = 0; i < net.num_layers; i++) {
            net.layer_size[i] = readInt(file);
            if (i == 0) {
               net.x_layer_size[i] = 0;
               net.y_layer_size[i] = net.layer_size[i] - 1;
            }
            else {
               net.x_layer_size[i] = net.y_layer_size[i-1] + 1;
               net.y_layer_size[i] = net.y_layer_size[i-1] + net.layer_size[i];
            }
     
            if (net.DISPLAY_NTF_IO > 0) {
               System.out.println("x_layer " + i + " = " + net.x_layer_size[i] + "  ");
               System.out.println("y_layer " + i + " = " + net.y_layer_size[i] + "  ");
               System.out.println("layer_size = " + net.layer_size[i]);
            }
         }
     
         /* Set the number of inputs and outputs */
         num_layers_m1 = net.num_layers - 1;
         net.ninputs = net.layer_size[0];
         net.noutputs = net.layer_size[num_layers_m1];
     
         /* Close the file */
         file.close();

      }
      catch (Exception e)
      {
         System.out.println(e.toString());
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * Read_XML_NTF_File
   *
   --------------------------------------------------------*/
   int Read_XML_NTF_File(NetworkRecord net, String filename )
   {
     try {
       // Request document building without validation
       int i;
       int wlen;
       int num_layers_m1;
       XMLElement root = new XMLElement();
       FileReader reader = new FileReader(filename);
       
       // Get the root element
       root.parseFromReader(reader);
       String elemName = root.getName();
   //    System.out.println( elemName.length() );
   //    System.out.println( root.getName().length() );
       if (!elemName.equals("bpnetwork")) {
          System.out.println("document of the wrong type, root node != bpnetwork");
          return( NetworkRecord.M_ERROR );
       }

       /* Read the ntf version indicator */
       net.version = Integer.parseInt(root.getAttribute("version").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("version = " + net.version);

       /* Read the misc indicators */
       net.activ_func = Integer.parseInt(root.getAttribute("activ_func").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("activ_func = " + net.activ_func);

       net.MAX_ITERATIONS = Integer.parseInt(root.getAttribute("max_iterations").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("max_iterations = " + net.MAX_ITERATIONS);

       net.MAX_TOTAL_ERROR = Double.parseDouble(root.getAttribute("max_total_error").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("MAX_TOTAL_ERROR = " + net.MAX_TOTAL_ERROR);

       net.OUTPUT_TYPE = Integer.parseInt(root.getAttribute("output_type").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("OUTPUT_TYPE = " + net.OUTPUT_TYPE);

       net.ITERATION_PRINT = Integer.parseInt(root.getAttribute("iteration_print").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("ITERATION_PRINT = " + net.ITERATION_PRINT);

       net.SAVE_WEIGHTS = Integer.parseInt(root.getAttribute("save_weights").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("SAVE_WEIGHTS = " + net.SAVE_WEIGHTS);

       net.WEIGHT_PATH = root.getAttribute("weight_path").toString();
       if (net.DISPLAY_NTF_IO > 0) System.out.println("WEIGHT_PATH = " + net.WEIGHT_PATH);
       //if (wlen > 0) net.WEIGHT_PATH[wlen] = 0;

       // The pattern file is now included in the ntf file.
       if (net.version == 2) {

          net.PATTERN_PATH = root.getAttribute("pattern_path").toString();
          if (net.DISPLAY_NTF_IO > 0) System.out.println("PATTERN_PATH = " + net.PATTERN_PATH);
          //if (wlen > 0) net.PATTERN_PATH[wlen] = 0;
       }

       /* Read the training parameters */
       net.A1 = Double.parseDouble(root.getAttribute("a1").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("A1 = " + net.A1);

       net.momentum_lrate = Double.parseDouble(root.getAttribute("momentum").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("momentum = " + net.momentum_lrate);

       net.bias_lrate = Double.parseDouble(root.getAttribute("bias").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("bias = " + net.bias_lrate);

       net.A2 = Double.parseDouble(root.getAttribute("a2").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("A2 = " + net.A2);

       net.A3 = Double.parseDouble(root.getAttribute("a3").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("A3 = " + net.A3);

       net.RANDOM_INIT_VAL = Integer.parseInt(root.getAttribute("random_init_val").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("RANDOM_INIT_VAL = " + net.RANDOM_INIT_VAL);

       /* Read the configuration parameters */
       net.num_layers = Integer.parseInt(root.getAttribute("num_layers").toString());
       if (net.DISPLAY_NTF_IO > 0) System.out.println("num_layers = " + net.num_layers);

       /*************************************/
       /* Read the layer configuration file */
       /*************************************/
       String szTemp;
       for (i = 0; i < net.num_layers; i++) {
          szTemp = "layer_size" + i;
          net.layer_size[i] = Integer.parseInt(root.getAttribute( szTemp ).toString());
          if (i == 0) {
             net.x_layer_size[i] = 0;
             net.y_layer_size[i] = net.layer_size[i] - 1;
          }
          else {
             net.x_layer_size[i] = net.y_layer_size[i-1] + 1;
             net.y_layer_size[i] = net.y_layer_size[i-1] + net.layer_size[i];
          }

          if (net.DISPLAY_NTF_IO > 0) {
             System.out.println("x_layer " + i + " = " + net.x_layer_size[i] + "  ");
             System.out.println("y_layer " + i + " = " + net.y_layer_size[i] + "  ");
             System.out.println("layer_size = " + net.layer_size[i]);
          }
       }

       /* Set the number of inputs and outputs */
       num_layers_m1 = net.num_layers - 1;
       net.ninputs = net.layer_size[0];
       net.noutputs = net.layer_size[num_layers_m1];

       /* Return OK */
       return( NetworkRecord.M_OK );

     } catch (Exception e) {
       System.out.println(e.toString());
       return( NetworkRecord.M_ERROR );
     }
   }

   /*--------------------------------------------------------
   *
   * Write_NTF_File
   *
   --------------------------------------------------------*/
   int Write_NTF_File(NetworkRecord net, String filename )
   {
      try {

         /* Open the NTF file for writing */
         FileOutputStream pfd = new FileOutputStream(filename);
         DataOutputStream file = new DataOutputStream(pfd);

         int i;
         int wlen;
         int fd;
         long pos;
      
         /* Set the virgin number */
         net.version = 2;
      
         /* Write the ntf version indicator */
         writeInt(file, net.version);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("version = " + net.version);

         /* Write the misc indicators */
         writeInt(file, net.activ_func);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("activ_func = " + net.activ_func);
      
         writeInt(file, (int)net.MAX_ITERATIONS);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("max_iterations = " + net.MAX_ITERATIONS);
      
         writeDouble(file, net.MAX_TOTAL_ERROR);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("MAX_TOTAL_ERROR = " + net.MAX_TOTAL_ERROR);
      
         writeInt(file, net.OUTPUT_TYPE);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("OUTPUT_TYPE = " + net.OUTPUT_TYPE);
      
         writeInt(file, (int)net.ITERATION_PRINT);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("ITERATION_PRINT = " + net.ITERATION_PRINT);
      
         writeInt(file, net.SAVE_WEIGHTS);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("SAVE_WEIGHTS = " + net.SAVE_WEIGHTS);
      
         writeInt(file, net.WEIGHT_PATH.length());
         if (net.DISPLAY_NTF_IO > 0) System.out.println("wlen = " + net.WEIGHT_PATH.length());

         file.writeBytes( net.WEIGHT_PATH );
         if (net.DISPLAY_NTF_IO > 0) System.out.println("WEIGHT_PATH = " + net.WEIGHT_PATH);

         writeInt(file, net.PATTERN_PATH.length());
         if (net.DISPLAY_NTF_IO > 0) System.out.println("wlen = " + net.PATTERN_PATH.length());

         file.writeBytes(net.PATTERN_PATH);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("PATTERN_PATH = " + net.PATTERN_PATH);

         /* Write the training parameters */
         writeDouble(file, net.A1);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A1 = " + net.A1);

         writeDouble(file, net.momentum_lrate);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("momentum = " + net.momentum_lrate);

         writeDouble(file, net.bias_lrate);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("bias = " + net.bias_lrate);

         writeDouble(file, net.A2);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A2 = " + net.A2);

         writeDouble(file, net.A3);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("A3 = " + net.A3);

         writeInt(file, net.RANDOM_INIT_VAL);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("RANDOM_INIT_VAL = " + net.RANDOM_INIT_VAL);

         /* Write the configuration parameters */
         writeInt(file, net.num_layers);
         if (net.DISPLAY_NTF_IO > 0) System.out.println("num_layers = " + net.num_layers);

         /*************************************/
         /* Write the layer configuration file */
         /*************************************/
         for (i = 0; i < net.num_layers; i++) {
            writeInt(file, net.layer_size[i]);
            System.out.println("x_layer " + i + " = " + net.x_layer_size[i] + "  ");
            System.out.println("y_layer " + i + " = " + net.y_layer_size[i] + "  ");
            System.out.println("layer_size = " + net.layer_size[i]);
         }

         /* Get the seek position */
         //pos = lseek( fd, 0L, SEEK_CUR ) + 1L;

         /* Truncate the file to this length */
         //ftruncate( fd, pos );

         /* Close the file */
         file.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * Scan_Pattern_File
   *
   --------------------------------------------------------*/
   int Scan_Pattern_File(NetworkRecord net, String filename,
                         int contains_desired, int [] num_read )
   {
      /* Open the pattern file */
      try {
         FileReader pfd = new FileReader(filename);
         BufferedReader buff = new BufferedReader(pfd);
         StreamTokenizer data = new StreamTokenizer( buff );
         int j;
         double value;
         int npatterns = 0;
         int type;

         /* Read in as many patterns as possible */
         while (true) {

            /* Read the input values */
            for (j = 0; j < net.ninputs; j++) {
               try {
                  type = data.nextToken();
                  if (type == StreamTokenizer.TT_NUMBER) {
                     value = data.nval;
                  } else {
                     buff.close();
                     num_read[0] = npatterns;
                     return( NetworkRecord.M_OK );
                  }
               } catch (EOFException eof) {
                  buff.close();
                  num_read[0] = npatterns;
                  return( NetworkRecord.M_OK );
               }
            }

            /* Read the output values */
            if (contains_desired > 0) {
               for (j = 0; j < net.noutputs; j++) {
                  try {
                     type = data.nextToken();
                     if (type == StreamTokenizer.TT_NUMBER) {
                        value = data.nval;
                     } else {
                        buff.close();
                        num_read[0] = npatterns;
                        return( NetworkRecord.M_OK );
                     }
                  } catch (EOFException eof) {
                     buff.close();
                     num_read[0] = npatterns;
                     return( NetworkRecord.M_OK );
                  }
               }
            }

            /* Increment pattern count */
            npatterns++;
         }

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }
   }

   /*--------------------------------------------------------
   *
   * Read_Pattern_File
   *
   --------------------------------------------------------*/
   public int Read_Pattern_File(NetworkRecord net, String filename, int contains_desired )
   {
      /* Open the pattern file */
      try {
         FileReader pfd = new FileReader(filename);
         BufferedReader buff = new BufferedReader(pfd);
         StreamTokenizer data = new StreamTokenizer( buff );
         int i,j;
         double value;
         int type;

         /* Read in all the patterns */
         for (i = 0; i < net.num_patterns; i++) {

            /* Read the input values */
            for (j = 0; j < net.ninputs; j++) {
               try {
                  type = data.nextToken();
                  if (type == StreamTokenizer.TT_NUMBER) {
                     value = data.nval;
                     net.def_input[(i * net.num_input) + j] = value;
                  } else {
                     buff.close();
                     return( NetworkRecord.M_OK );
                  }

                  if (net.DISPLAY_PAT_IO > 0) {
                     int x = (i * net.num_input) + j;
                     System.out.println("def_input[" +i+ "][" +j+"](" +x+ ") = " +
                           net.def_input[x] );
                  }

               } catch (EOFException eof) {
                  buff.close();
                  return( NetworkRecord.M_OK );
               }
            }

            /* Read the output values */
            if (contains_desired > 0) {
               for (j = 0; j < net.noutputs; j++) {
                  try {

                     type = data.nextToken();
                     if (type == StreamTokenizer.TT_NUMBER) {
                        value = data.nval;
                        net.def_output[(i * net.num_output) +
                               net.x_layer_size[net.num_layers - 1] + j] = value;
                     } else {
                        buff.close();
                        return( NetworkRecord.M_OK );
                     }
                  } catch (EOFException eof) {
                     buff.close();
                     return( NetworkRecord.M_OK );
                  }
                  if (net.DISPLAY_PAT_IO > 0) {
                     int x = (i * net.num_output) + net.x_layer_size[net.num_layers - 1] + j;
                     System.out.println("def_output[" +i+ "][" +j+ "](" +x+ ") = " +
                        net.def_output[x]);
                  }
               }
            }
         }

         /* Close the file if necessary */
         pfd.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * MWrite_Pattern_File
   *
   --------------------------------------------------------*/
   int Write_Pattern_File(NetworkRecord net, String filename, int contains_desired )
   {
      try {

         /* Open the NTF file for writing */
         FileOutputStream pfd = new FileOutputStream(filename);
         PrintStream file = new PrintStream(pfd);
         double value;

         /* Write out all the patterns */
         for (int i = 0; i < net.num_patterns; i++) {

            /* Write out the input values */
            for (int j = 0; j < net.ninputs; j++) {
               value = net.def_input[(i * net.num_input) + j];
               file.println( decfmt.format(value) );
            }

            /* Write the output values */
            if (contains_desired > 0) {
               for (int j = 0; j < net.noutputs; j++) {
                  value = net.def_output[(i * net.num_output) +
                             net.x_layer_size[net.num_layers - 1] + j];
                  file.println( decfmt.format(value) );
               }
            }
         }

         /* Close the file if necessary */
         file.close();

      } catch (IOException e) {
         System.out.println( "Error -- " + e.toString() );
         return( NetworkRecord.M_ERROR );
      }

      /* Return OK */
      return( NetworkRecord.M_OK );
   }

   /*--------------------------------------------------------
   *
   * terminate_received - check for a press of the ESC key.
   *
   --------------------------------------------------------*/
   int terminate_received()
   {
   //   static unsigned key;
   //
   //   #ifdef DOSEXE2
   //      #define ESCKEY 0x011b
   //   //#include <bios.h>
   //   if ((key = _bios_keybrd( _KEYBRD_READY | _NKEYBRD_READY )) > 0) {
   //      switch (key & 0xFFF) {
   //         case ESCKEY:
   //            rec_terminate = TRUE;
   //            return( TRUE );
   //         default:
   //            key = _bios_keybrd( _KEYBRD_READ | _NKEYBRD_READ );
   //            printf("key = %#4x\n",key);
   //            return( FALSE );
   //      }
   //   }
   //   #endif
      return( NetworkRecord.FALSE );
   }

   /*--------------------------------------------------------
   *
   * max_output_index - output the index of the class with
   *                    the highest score.
   *
   --------------------------------------------------------*/
   int max_output_index( NetworkRecord net )
   {
      int i,j = 0;
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      int index = 0;
      double high_value = -10000.0;

      for (i = amin; i <= amax; i++) {
         if (net.activation[i] > high_value) {
            high_value = net.activation[i];
            index = j;
         }
         j++;
      }

      /* Return the index of the highest output */
      return( index );
   }

   void Printf_Output2( BufferedWriter buff, NetworkRecord net )
     throws java.io.IOException
   {
      int nl_m1 = net.num_layers - 1;
      int amin = net.x_layer_size[nl_m1];
      int amax = net.y_layer_size[nl_m1];
      double [] ai = net.activation; //[amin];

      for (int i = amin; i <= amax; i++) {
         buff.write(decfmt.format(ai[i]));
         buff.newLine();
         //System.out.println(decfmt.format(ai[i]));
      }
   }

}
