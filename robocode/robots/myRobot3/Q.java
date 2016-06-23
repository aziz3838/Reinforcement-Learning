package myRobot3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import robocode.RobocodeFileOutputStream;

public class Q {

	public static final double ALPHA = 0.04;	//Learning Rate
	public static final double GAMMA = 0.9;	//Discounted Rate
	
	public static final int numStates = 2*3*2;
	public static final int numActions = 4;
	
	
	private double table[][];
	
	Q(){
		table = new double[numStates][numActions];
		initialize();
	}
	
	public void initialize() {
		for(int i=0; i<numStates; i++)
			for(int j=0; j<numActions; j++)
				table[i][j] = (Math.random() - 0.5) * 5000;
	}
	
	double getQ(int state, int action)
	{
		return table[state][action];
	}
	
	void setQ(int state, int action, double Q)
	{
		table[state][action] = Q;
	}
	
	int actionOfMaxQ(int state)
	{
		int action = 0;
		double max = -10000;
		for(int i=0; i<numActions; i++)
		{
			if(getQ(state, i) > max)
			{
				max = getQ(state, i);
				action = i;
			}
			
		}
		return action;
	}
	
	double maxQ(int state)
	{
		double max = -10000;
		for(int i=0; i<numActions; i++)
		{
			if(getQ(state, i) > max)
			{
				max = getQ(state, i);
			}
			
		}
		return max;
	}
	
	void QLearning (int state1, int action1, int state2, int reward)
	{
		double Q1 		= getQ(state1, action1);
		double maxQ 	= maxQ(state2);
		
		double updatedQ = Q1 + ALPHA * (reward + GAMMA * maxQ - Q1);
		setQ(state1, action1, updatedQ);
	}
	
	void SARSA (int state1, int action1, int state2, int action2, int reward)
	{
		double Q1 		= getQ(state1, action1);
		double Q2	 	= getQ(state2, action2);
		
		double updatedQ = Q1 + ALPHA * (reward + GAMMA * Q2 - Q1);
		setQ(state1, action1, updatedQ);
	}


  public void loadData(File file)
  {
    BufferedReader r = null;
    try
    {
      r = new BufferedReader(new FileReader(file));
      for (int i = 0; i < States.numStates; i++)
        for (int j = 0; j < States.numActions; j++)
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

  public void saveData(File file)
  {
    PrintStream w = null;
    try
    {
      w = new PrintStream(new RobocodeFileOutputStream(file));
      for (int i = 0; i < States.numStates; i++)
        for (int j = 0; j < States.numActions; j++)
        {
        	//w.println(" "+i+" "+ j+" "+ table[i][j]+'\n');
        	w.println(table[i][j]);
          //System.out.println("state: "+i+" Action: "+ j + ": " + table[i][j]+'\n');
        }

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
	
	
	
	
}
