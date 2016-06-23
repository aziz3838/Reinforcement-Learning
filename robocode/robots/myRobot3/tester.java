package myRobot3;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;



public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NeuralNet NeuralNet = null;
	      try
	      {
	    	 
	         FileInputStream fileIn = new FileInputStream("NN.txt");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         NeuralNet = (NeuralNet) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	         //return;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("NeuralNet class not found");
	         c.printStackTrace();
	         return;
	      }
	      
	      System.out.println(NeuralNet.numEnergy);
	}
	
	public static void start(File filename)
	{
		NeuralNet NeuralNet = null;
	      try
	      {
	    	 
	         FileInputStream fileIn = new FileInputStream(filename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         NeuralNet = (NeuralNet) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i)
	      {
	         i.printStackTrace();
	         //return;
	      }catch(ClassNotFoundException c)
	      {
	         System.out.println("NeuralNet class not found");
	         c.printStackTrace();
	         return;
	      }
	      
	      System.out.println(NeuralNet.numEnergy);
	}

}
