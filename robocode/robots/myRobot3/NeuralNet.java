package myRobot3;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Random;



import static java.lang.Math.*;


public class NeuralNet implements java.io.Serializable{

	//Initial Variables
	private  int 	NUM_INPUTS 		= 11; //7 for states, 4 for actions
	private  int 	NUM_HIDDEN 		= 15;
	private  int 	NUM_OUTPUTS 	= 1;
	private  double LEARNING_RATE = 0.05;
	private  double 	MOMENTUM 	= 0.1;	//Adjust this for part C.
	private  int OPTION 			= 1;	//option 0: binary. option 1: bipolar.
	
	 int numStates = 12;
	 int numActions = 4;
	  final int numEnergy =2;
	  final int numGunHeat = 2;
	  final int numDistance = 3;
	 
	//Weights
	private  double[][] weight_Input2Hidden = new double[NUM_HIDDEN][NUM_INPUTS];
	private  double[] weight_Hidden2Output = new double[NUM_HIDDEN + 1]; //we need a 2D array if outputs are more than 1.
	private  double[][] delta_weight_Input2Hidden = new double[NUM_HIDDEN][NUM_INPUTS];
	private  double[] delta_weight_Hidden2Output = new double[NUM_HIDDEN + 1]; //we need a 2D array if outputs are more than 1.
	private  final double WEIGHT_MIN 	= -50;
	private  final double WEIGHT_MAX 	=  -10;
	//Training
	private  final static int 	NUM_PATTERNS 	= 48;
	private  int[][] 	training_Input 		= new int[NUM_PATTERNS][NUM_INPUTS];
	private  double[] 	training_Output 	= new double[NUM_PATTERNS]; //Needs to be 2D if outputs are more than 1.
	
	//Variables
	private  double[] error = new double[NUM_HIDDEN + 1];
	private  double[] weighted_sum_s = new double[NUM_HIDDEN + 1];
	private  double[] activation_u = new double[NUM_HIDDEN + 1];
	private  double total_weighted_sum_s, total_activation_u, total_error;
	private  double E;
	private  double[] final_error = new double[10000];
	
	
	
	//QTable
	private  double table[][];
	
	 public  NeuralNet () {
		 
		 table = new double[numStates][numActions];
		 
//		 try {
//			initialize();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		//for RL. take this out when only wanting to test NeuralNet
		 
	} 
	 

		
		public static void main(String[] args) throws FileNotFoundException {
			// TODO Auto-generated method stub

			
			NeuralNet NN = new NeuralNet();
			NN.initializeWeights();
			NN.trainingData();
			NN.start();

			//System.out.println("Error: "+" "+E+" at "+epoch+" epoch");
			//out.close();
			
			//testNN();
			
			//Serialize Object
//			try
//		      {
//		         FileOutputStream fileOut =
//		         new FileOutputStream("NN.ser");
//		         ObjectOutputStream out = new ObjectOutputStream(fileOut);
//		         out.writeObject(NN);
//		         out.close();
//		         fileOut.close();
//		         System.out.printf("Serialized data is saved");
//		      }catch(IOException i)
//		      {
//		          i.printStackTrace();
//		      }

			
			NN.saveData();
			
			//NN.loadData(new File("NN.txt"));
			//System.out.println("aaaaaaaaaaaaaaaaaaaaa");
			//NN.start();
		}

	public void start()
	{
		int epoch = 0;
		double error_pattern[] = new double[NUM_PATTERNS];
		String fileName = "C:\\Users\\Administrator\\workspace\\EECE592Assi1 - modified\\src\\data_fromPart2.dat";
		loadDataFromQTable(new File(fileName));
		
//	      for (int i = 0; i < States.numStates; i++)
//		        for (int j = 0; j < States.numActions; j++)
//		        	System.out.println(table[i][j]);
//	      
	      
	      QtoNNInputVector();

		//PrintWriter out = new PrintWriter("filename.txt");
		
		
		for(int i =0; i<2000; i++)
		{
			for(int j=0; j<NUM_PATTERNS; j++)
			{
				train(training_Input[j], training_Output[j]);
			}
			
			for(int j=0; j<NUM_PATTERNS; j++)
			{
				error_pattern[j] = outputFor(training_Input[j]) - training_Output[j];
				//System.out.println(error_pattern[j] + " = "  + NN.outputFor(training_Input[j]) + " - " + training_Output[j]);
			}
			
			E = 0;
			for (int j=0; j<NUM_PATTERNS; j++){
				E += Math.pow(error_pattern[j], 2);
			}
			E = 0.5 * E;
			epoch++;
			
			//System.out.println("Error: "+" "+E+" at "+epoch+" epoch");
			System.out.println(E);
			//out.println(E);
			


			
				
			
		}
	}
	public  double outputFor(int[] inputState1NN) {
		// Forward Propagation.
		// X is inputs
		
		for(int i=0; i<NUM_HIDDEN; i++)
		{
			double temp = 0;
			for(int j=0; j<NUM_INPUTS; j++)
			{
				temp += weight_Input2Hidden[i][j] * inputState1NN[j];
				
			}
			weighted_sum_s[i] = temp;
			//System.out.println(temp);
			activation_u[i]		= sigmoid(weighted_sum_s[i]);
		}
			
		total_weighted_sum_s = 0;
		for(int i=0; i<NUM_HIDDEN + 1; i++)
		{
			total_weighted_sum_s += weight_Hidden2Output[i] * activation_u[i];
		}
		total_activation_u = sigmoid (total_weighted_sum_s);
		
			
		//System.out.println(total_activation_u);
		return total_activation_u;
	}


	public double train(int[] X, double argValue) {
		// Backpropagation
		
		
		for(int i=0; i<NUM_HIDDEN; i++)
		{
			double temp = 0;
			for(int j=0; j<NUM_INPUTS; j++)
			{
				temp += weight_Input2Hidden[i][j] * X[j];
			}
			weighted_sum_s[i] = temp;
			activation_u[i]		= sigmoid(weighted_sum_s[i]);
		}
			
		total_weighted_sum_s = 0;
		for(int i=0; i<NUM_HIDDEN + 1; i++)
		{
			total_weighted_sum_s += weight_Hidden2Output[i] * activation_u[i];
		}
		total_activation_u = sigmoid (total_weighted_sum_s);
		
		
		//Error of Output
		if(OPTION == 0)
		{
			total_error = (argValue - total_activation_u) * total_activation_u * (1-total_activation_u);
		}else{
			total_error = (argValue - total_activation_u)*0.5*(1-pow(total_activation_u,2));
		}
			
		
		//Error of Hidden Neurons
		if(OPTION == 0)
		{
			for(int i=0; i<NUM_HIDDEN + 1; i++)
			{
				error[i] = activation_u[i]*(1-activation_u[i])*total_error*weight_Hidden2Output[i];
			}
		}else{
			for(int i=0; i<NUM_HIDDEN + 1; i++)
			{
				error[i] = 0.5*(1-pow(activation_u[i],2))*total_error*weight_Hidden2Output[i];
			}
		}

		
		
		//Update Weight
		for(int i=0; i<NUM_HIDDEN + 1; i++)
		{
			weight_Hidden2Output[i] 			+= MOMENTUM*delta_weight_Hidden2Output[i] + (LEARNING_RATE *total_error * activation_u[i]);
			delta_weight_Hidden2Output[i] 		= LEARNING_RATE*total_error*activation_u[i] + (MOMENTUM * delta_weight_Hidden2Output[i]);
		}
		for(int i=0; i<NUM_HIDDEN; i++)
		{
			for(int j=0; j<NUM_INPUTS; j++)
			{
				weight_Input2Hidden[i][j] 		+= MOMENTUM*delta_weight_Input2Hidden[i][j] + (LEARNING_RATE*error[i]*X[j]);
				delta_weight_Input2Hidden[i][j] = LEARNING_RATE*error[i]*X[j] + (MOMENTUM*delta_weight_Input2Hidden[i][j]);
			}
		}
		
		
		
		return 0;
	}
	
	//public double trainNN(double[] X, double argValue) {
	public double trainNN(int[] state1, int action1, int[] state2, int reward) {
		
		// Backpropagation
		
		int inputState1Action1NN[] = new int[11];
		if(state1[0]==0)			//Energy
		{
			inputState1Action1NN[0] = 0;
			inputState1Action1NN[1] = 1;
		}else{
			inputState1Action1NN[0] = 1;
			inputState1Action1NN[1] = 0;
		}
		
		if(state1[1]==0)			//Distance
		{
			inputState1Action1NN[2] = 0;
			inputState1Action1NN[3] = 0;
			inputState1Action1NN[4] = 1;
		}
		else if(state1[1]==1)
		{
			inputState1Action1NN[2] = 0;
			inputState1Action1NN[3] = 1;
			inputState1Action1NN[4] = 0;
		}
		else
		{
			inputState1Action1NN[2] = 1;
			inputState1Action1NN[3] = 0;
			inputState1Action1NN[4] = 0;
		}
		
		if(state1[2]==0)			//GunHeat
		{
			inputState1Action1NN[5] = 0;
			inputState1Action1NN[6] = 1;
		}
		else
		{
			inputState1Action1NN[5] = 1;
			inputState1Action1NN[6] = 0;
		}
		
		if(action1==0)
		{
			inputState1Action1NN[7] = 0;
			inputState1Action1NN[8] = 0;
			inputState1Action1NN[9] = 0;
			inputState1Action1NN[10] = 1;
		}
		else if(action1==1)
		{
			inputState1Action1NN[7] = 0;
			inputState1Action1NN[8] = 0;
			inputState1Action1NN[9] = 1;
			inputState1Action1NN[10] = 0;
		}
		else if(action1==2)
		{
			inputState1Action1NN[7] = 0;
			inputState1Action1NN[8] = 1;
			inputState1Action1NN[9] = 0;
			inputState1Action1NN[10] = 0;
		}
		else
		{
			inputState1Action1NN[7] = 1;
			inputState1Action1NN[8] = 0;
			inputState1Action1NN[9] = 0;
			inputState1Action1NN[10] = 0;
		}
		
		int inputState2NN[] = new int[11];
		if(state1[0]==0)			//Energy
		{
			inputState2NN[0] = 0;
			inputState2NN[1] = 1;
		}else{
			inputState2NN[0] = 1;
			inputState2NN[1] = 0;
		}
		
		if(state1[1]==0)			//Distance
		{
			inputState2NN[2] = 0;
			inputState2NN[3] = 0;
			inputState2NN[4] = 1;
		}
		else if(state1[1]==1)
		{
			inputState2NN[2] = 0;
			inputState2NN[3] = 1;
			inputState2NN[4] = 0;
		}
		else
		{
			inputState2NN[2] = 1;
			inputState2NN[3] = 0;
			inputState2NN[4] = 0;
		}
		
		if(state1[2]==0)			//GunHeat
		{
			inputState2NN[5] = 0;
			inputState2NN[6] = 1;
		}
		else
		{
			inputState2NN[5] = 1;
			inputState2NN[6] = 0;
		}
		
		
		for(int i =0; i<11; i++)
			if(inputState1Action1NN[i] == 0)
				inputState1Action1NN[i] = -1;
		
		for(int i =0; i<7; i++)
			if(inputState2NN[i] == 0)
				inputState2NN[i] = -1;
			
		
		
		//QValues
		//double Q1 		= getQ(state1, action1);
		double Q1		= outputFor(inputState1Action1NN);
		
		//double maxQ 	= maxQ(state2);
		double maxQ 	= maxQNN(inputState2NN);
		
		double updatedQ = Q1 + 0.04 * (reward + Q.GAMMA * maxQ - Q1);
		
		//System.out.println(Q1 + " - " + maxQ  + " - " + updatedQ);
		//setQ(state1, action1, updatedQ);
		double argValue = updatedQ;
		
		for(int i=0; i<NUM_HIDDEN; i++)
		{
			double temp = 0;
			for(int j=0; j<NUM_INPUTS; j++)
			{
				temp += weight_Input2Hidden[i][j] * inputState1Action1NN[j];
			}
			weighted_sum_s[i] = temp;
			activation_u[i]		= sigmoid(weighted_sum_s[i]);
		}
			
		total_weighted_sum_s = 0;
		for(int i=0; i<NUM_HIDDEN + 1; i++)
		{
			total_weighted_sum_s += weight_Hidden2Output[i] * activation_u[i];
		}
		total_activation_u = sigmoid (total_weighted_sum_s);
		
		
		//Error of Output
		if(OPTION == 0)
		{
			total_error = (argValue - total_activation_u) * total_activation_u * (1-total_activation_u);
		}else{
			total_error = (argValue - total_activation_u)*0.5*(1-pow(total_activation_u,2));
		}
			
		
		//Error of Hidden Neurons
		if(OPTION == 0)
		{
			for(int i=0; i<NUM_HIDDEN + 1; i++)
			{
				error[i] = activation_u[i]*(1-activation_u[i])*total_error*weight_Hidden2Output[i];
			}
		}else{
			for(int i=0; i<NUM_HIDDEN + 1; i++)
			{
				error[i] = 0.5*(1-pow(activation_u[i],2))*total_error*weight_Hidden2Output[i];
			}
		}

		
		
		//Update Weight
		for(int i=0; i<NUM_HIDDEN + 1; i++)
		{
			weight_Hidden2Output[i] 			+= MOMENTUM*delta_weight_Hidden2Output[i] + (LEARNING_RATE *total_error * activation_u[i]);
			delta_weight_Hidden2Output[i] 		= LEARNING_RATE*total_error*activation_u[i] + (MOMENTUM * delta_weight_Hidden2Output[i]);
		}
		for(int i=0; i<NUM_HIDDEN; i++)
		{
			for(int j=0; j<NUM_INPUTS; j++)
			{
				weight_Input2Hidden[i][j] 		+= MOMENTUM*delta_weight_Input2Hidden[i][j] + (LEARNING_RATE*error[i]*inputState1Action1NN[j]);
				delta_weight_Input2Hidden[i][j] = LEARNING_RATE*error[i]*inputState1Action1NN[j] + (MOMENTUM*delta_weight_Input2Hidden[i][j]);
			}
		}
		
		
		
		return 0;
	}



	
	public  double sigmoid(double x) {
		// TODO Auto-generated method stub
		if(OPTION == 0)
		{
			return (1/(1+ exp(-x)));
		}else{
			return (-1 + 2/(1+ exp(-x*0.01)));
		}
		
	}


	public double customSigmoid(double x) {
		// TODO Auto-generated method stub
		return 0;
	}


	public void initializeWeights() {
		// TODO Auto-generated method stub
		for(int i=0; i < NUM_HIDDEN; i++)
		{
			weight_Hidden2Output[i] =  WEIGHT_MIN + (WEIGHT_MAX - WEIGHT_MIN) * (new Random().nextDouble()) * 0.001;
			delta_weight_Hidden2Output[i] = (Math.random() - 0.5) * 0.1;
			for(int j=0; j < NUM_INPUTS; j++)
			{
				weight_Input2Hidden[i][j] =  WEIGHT_MIN + (WEIGHT_MAX - WEIGHT_MIN) * (new Random().nextDouble()) * 0.001;
				delta_weight_Input2Hidden[i][j] = (Math.random() - 0.5) * 0.1;
			}
			

		}
	
		
	}

	public void trainingData()
    {
		activation_u[NUM_HIDDEN] = 1;
		

    }
	
	
	  public  void loadDataFromQTable(File fileName)
	  {
	    BufferedReader r = null;
	    try
	    {
	    	
	      r = new BufferedReader(new FileReader(fileName));
	      for (int i = 0; i < numStates; i++)
	        for (int j = 0; j < numActions; j++)
	        	table[i][j] = Double.parseDouble(r.readLine());
	      
	    }
	    catch (IOException e)
	    {
	      System.out.println("IOException trying to open reader of file: " + e);
	      //initialize();
	    }
	    catch (NumberFormatException e)
	    {
	      //initialize();
	    }
	    finally
	    {
	      try
	      {
	        if (r != null)
	          r.close();
	      }
	      catch (IOException e)
	      {
	        System.out.println("IOException trying to close reader of file: " + e);
	      }
	    }
	  }

	  
	  
	  private  void testNN()
	  {
			
			int inputVectorNN[][] = new int[48][11];
			
			int count = 0;
			for (int i=0; i < numEnergy; i++)		//energy
				for (int m=0; m < numDistance; m++)	//distance
					for (int k=0; k < numGunHeat; k++)	//gunheat
						for(int z=0; z<numActions; z++)	//actions
						{
							if(i==0)
							{
								inputVectorNN[count][0] = 0;
								inputVectorNN[count][1] = 1;
							}else{
								inputVectorNN[count][0] = 1;
								inputVectorNN[count][1] = 0;
							}
							
							if(m==0)
							{
								inputVectorNN[count][2] = 0;
								inputVectorNN[count][3] = 0;
								inputVectorNN[count][4] = 1;
							}
							else if(m==1)
							{
								inputVectorNN[count][2] = 0;
								inputVectorNN[count][3] = 1;
								inputVectorNN[count][4] = 0;
							}
							else
							{
								inputVectorNN[count][2] = 1;
								inputVectorNN[count][3] = 0;
								inputVectorNN[count][4] = 0;
							}	
							
							
							
							if(k==0)
							{
								inputVectorNN[count][5] = 0;
								inputVectorNN[count][6] = 1;
							}
							else
							{
								inputVectorNN[count][5] = 1;
								inputVectorNN[count][6] = 0;
							}
							
							
							if(z==0)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 1;
							}
							else if(z==1)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 1;
								inputVectorNN[count][10] = 0;
							}
							else if(z==2)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 1;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 0;
							}
							else
							{
								inputVectorNN[count][7] = 1;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 0;
							}

							
							//System.out.println(input_NN[count]);
							count++;
							
						}
				
			
			
				//Populating the trainingInput
				for(int i =0; i<48; i++)
				{
					for(int j=0; j<NUM_INPUTS; j++)
					{
						if(inputVectorNN[i][j] == 1)
							inputVectorNN[i][j] = 1;
						else	
							inputVectorNN[i][j] = -1;
						//System.out.print(training_Input[i][j]);
					}
					
				}

				
				//Print all Outputs for all States*Action (48)
				for(int i =0; i<48; i++)
				{
						System.out.println(outputFor(inputVectorNN[i]) * 2100.0);
					
				}
				

	  }
	  
	  
	  private  void QtoNNInputVector()
	  {
			
			double inputVectorNN[][] = new double[48][11];
			
			int count = 0;
			for (int i=0; i < numEnergy; i++)		//energy
				for (int m=0; m < numDistance; m++)	//distance
					for (int k=0; k < numGunHeat; k++)	//gunheat
						for(int z=0; z<numActions; z++)	//actions
						{
							if(i==0)
							{
								inputVectorNN[count][0] = 0;
								inputVectorNN[count][1] = 1;
							}else{
								inputVectorNN[count][0] = 1;
								inputVectorNN[count][1] = 0;
							}
							
							if(m==0)
							{
								inputVectorNN[count][2] = 0;
								inputVectorNN[count][3] = 0;
								inputVectorNN[count][4] = 1;
							}
							else if(m==1)
							{
								inputVectorNN[count][2] = 0;
								inputVectorNN[count][3] = 1;
								inputVectorNN[count][4] = 0;
							}
							else
							{
								inputVectorNN[count][2] = 1;
								inputVectorNN[count][3] = 0;
								inputVectorNN[count][4] = 0;
							}	
							
							
							
							if(k==0)
							{
								inputVectorNN[count][5] = 0;
								inputVectorNN[count][6] = 1;
							}
							else
							{
								inputVectorNN[count][5] = 1;
								inputVectorNN[count][6] = 0;
							}
							
							
							if(z==0)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 1;
							}
							else if(z==1)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 1;
								inputVectorNN[count][10] = 0;
							}
							else if(z==2)
							{
								inputVectorNN[count][7] = 0;
								inputVectorNN[count][8] = 1;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 0;
							}
							else
							{
								inputVectorNN[count][7] = 1;
								inputVectorNN[count][8] = 0;
								inputVectorNN[count][9] = 0;
								inputVectorNN[count][10] = 0;
							}

							
							//System.out.println(input_NN[count]);
							count++;
							
						}
				
			
			
				//Populating the trainingInput
				for(int i =0; i<48; i++)
				{
					for(int j=0; j<NUM_INPUTS; j++)
					{
						if(inputVectorNN[i][j] == 1)
							training_Input[i][j] = 1;
						else	
							training_Input[i][j] = -1;
						//System.out.print(training_Input[i][j]);
					}
					//System.out.println();
					
				}
				
				//Populating the trainingOutput
				count = 0;
				for(int i=0; i<numStates; i++)
					for(int j=0; j<numActions; j++)
					{
						training_Output[count] = table[i][j] / 2100.0;
						//System.out.println(training_Output[count]);
						count++;
					}
				
				//training_Input
				
				

	  }

		double maxQNN(int[] state)
		{
			//outputFor
			int inputVectorNN[] = new int[11];
			if(state[0]==0)			//Energy
			{
				inputVectorNN[0] = 0;
				inputVectorNN[1] = 1;
			}else{
				inputVectorNN[0] = 1;
				inputVectorNN[1] = 0;
			}
			
			if(state[1]==0)			//Distance
			{
				inputVectorNN[2] = 0;
				inputVectorNN[3] = 0;
				inputVectorNN[4] = 1;
			}
			else if(state[1]==1)
			{
				inputVectorNN[2] = 0;
				inputVectorNN[3] = 1;
				inputVectorNN[4] = 0;
			}
			else
			{
				inputVectorNN[2] = 1;
				inputVectorNN[3] = 0;
				inputVectorNN[4] = 0;
			}
			
			if(state[2]==0)			//GunHeat
			{
				inputVectorNN[5] = 0;
				inputVectorNN[6] = 1;
			}
			else
			{
				inputVectorNN[5] = 1;
				inputVectorNN[6] = 0;
			}

			

				
			
			double outputValue[] = new double[numActions];		//outputvalue for every action
			for(int i=0; i<numActions; i++)		
			{
				
				
				if(i==0)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 1;
				}
				else if(i==1)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 1;
					inputVectorNN[10] = 0;
				}
				else if(i==2)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 1;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 0;
				}
				else
				{
					inputVectorNN[7] = 1;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 0;
				}
				
				
				for(int j=0; j<NUM_INPUTS; j++)
				{
					if(inputVectorNN[j] == 0)
						inputVectorNN[j] = -1;
				}
				
				outputValue[i] = outputFor(inputVectorNN);
			}

			
			
			//findMaxOutputValue
			int action = 0;
			double max = -10000;
			for(int i=0; i<numActions; i++)
			{
				if(outputValue[i] > max)
				{
					max = outputValue[i];
					action = i;
				}
				
			}
			return max;
	}
		
		int actionOfMaxQNN(int[] state)
		{
			//outputFor
			int inputVectorNN[] = new int[11];
			if(state[0]==0)			//Energy
			{
				inputVectorNN[0] = 0;
				inputVectorNN[1] = 1;
			}else{
				inputVectorNN[0] = 1;
				inputVectorNN[1] = 0;
			}
			
			if(state[1]==0)			//Distance
			{
				inputVectorNN[2] = 0;
				inputVectorNN[3] = 0;
				inputVectorNN[4] = 1;
			}
			else if(state[1]==1)
			{
				inputVectorNN[2] = 0;
				inputVectorNN[3] = 1;
				inputVectorNN[4] = 0;
			}
			else
			{
				inputVectorNN[2] = 1;
				inputVectorNN[3] = 0;
				inputVectorNN[4] = 0;
			}
			
			if(state[2]==0)			//GunHeat
			{
				inputVectorNN[5] = 0;
				inputVectorNN[6] = 1;
			}
			else
			{
				inputVectorNN[5] = 1;
				inputVectorNN[6] = 0;
			}

			

				
			
			double outputValue[] = new double[numActions];		//outputvalue for every action
			for(int i=0; i<numActions; i++)		
			{
				
				
				if(i==0)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 1;
				}
				else if(i==1)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 1;
					inputVectorNN[10] = 0;
				}
				else if(i==2)
				{
					inputVectorNN[7] = 0;
					inputVectorNN[8] = 1;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 0;
				}
				else
				{
					inputVectorNN[7] = 1;
					inputVectorNN[8] = 0;
					inputVectorNN[9] = 0;
					inputVectorNN[10] = 0;
				}
				
				
				for(int j=0; j<NUM_INPUTS; j++)
				{
					if(inputVectorNN[j] == 0)
						inputVectorNN[j] = -1;
				}
				
				outputValue[i] = outputFor(inputVectorNN);
			}

			
			
			//findMaxOutputValue
			int action = 0;
			double max = -10000;
			for(int i=0; i<numActions; i++)
			{
				if(outputValue[i] > max)
				{
					max = outputValue[i];
					action = i;
				}
				
			}
			return action;
	}
		
		  public void saveData()
		  {
			
		    PrintStream w = null;
		    try
		    {
		      w = new PrintStream("NN.txt");
		      
		    //weight_Input2Hidden
		      for (int i = 0; i < NUM_HIDDEN; i++)
		        for (int j = 0; j < NUM_INPUTS; j++)
		        	w.println(weight_Input2Hidden[i][j]);
		      
		    //weight_Hidden2Output
		      for (int i = 0; i < NUM_HIDDEN + 1; i++)
		        	w.println(weight_Hidden2Output[i]);
		      
		    //activation_u
		      for (int i = 0; i < NUM_HIDDEN + 1; i++)
			        	w.println(activation_u[i]);
		      


		      if (w.checkError())
		        System.out.println("Could not save the data to file!");
		      w.close();
		    }
		    catch (IOException e)
		    {
		      System.out.println("IOException trying to write to file: " + e);
		    }
		    finally
		    {
		      try
		      {
		        if (w != null)
		          w.close();
		      }
		      catch (Exception e)
		      {
		        System.out.println("Exception trying to close witer of movement file: " + e);
		      }
		    }
		  }

		  
		  public void loadData(File file)
		  {
		    BufferedReader r = null;
		    try
		    {
		    	r = new BufferedReader(new FileReader(file));
		    	
			  //weight_Input2Hidden
		      for (int i = 0; i < NUM_HIDDEN; i++)
		        for (int j = 0; j < NUM_INPUTS; j++)
		        	weight_Input2Hidden[i][j] = Double.parseDouble(r.readLine());
		      
		    //weight_Hidden2Output
		      for (int i = 0; i < NUM_HIDDEN + 1; i++)
		    	  weight_Hidden2Output[i] = Double.parseDouble(r.readLine());
		      
		    //activation_u
		      for (int i = 0; i < NUM_HIDDEN + 1; i++)
		    	  activation_u[i] = Double.parseDouble(r.readLine());

		    }
		    catch (IOException e)
		    {
		      System.out.println("IOException trying to open reader of file: " + e);
		      //initialize();
		    }
		    catch (NumberFormatException e)
		    {
		      //initialize();
		    }
		    finally
		    {
		      try
		      {
		        if (r != null)
		          r.close();
		      }
		      catch (IOException e)
		      {
		        System.out.println("IOException trying to close reader of file: " + e);
		      }
		    }
		  }

}
