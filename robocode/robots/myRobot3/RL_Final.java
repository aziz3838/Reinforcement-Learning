package myRobot3;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.CustomEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.Robot;
import robocode.RoundEndedEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.Event;
import robocode.util.Utils;

//import robocode.



public class RL_Final extends AdvancedRobot {

	
	private static final int PI = 180;
	static States States = new States();
	static Q Q = new Q();
	static NeuralNet NeuralNet = new NeuralNet();					//NN!!!!!!!!!!!
	int reward = 0;
	double epsilon;
	double scantime=0;
	static int run=0;
	
	static int state1=0, state2=0, action1=0, state3=0, action2=0, action3=0;
	static int[] state1NN, state2NN;
	static int reward1=0, reward2=0, reward3=0;
	static boolean firstrun = true;
	double startTime=0, endTime=0;
	static int roundCount = 0;
	long time[] = new long[5000];
	static int turnsReached[] = new int[5000];
	
	private byte moveDirection = 1;
	private int wallMargin = 60;
	
	static int roundCountTotal =0;
	static int winsCount = 0;
	static ArrayList<Integer> winsRate = new ArrayList<Integer>();
	static int totalcountQ3=0;
	public void run() {
	
		if(roundCountTotal == 0)
		{
			NeuralNet.loadData(getDataFile("NN.txt"));
			
			
		}
		
		//System.out.println(NeuralNet.numActions);

			if(roundCountTotal >= 50)
			{
				winsRate.add(winsCount*2);
				roundCountTotal = 0;
				winsCount = 0;
			}
			roundCountTotal++;

			
			
//			epsilon = 0.8;
//			
//		totalcountQ3++;
//		if(totalcountQ3>999)
//			epsilon = 0;
			
//		epsilon = 0;


//		
//		epsilon = 0;
//		loadData();	

		epsilon = 0.1;
		
	    setAdjustGunForRobotTurn(true);
	    setAdjustRadarForGunTurn(true);
	    turnRadarRightRadians(Double.POSITIVE_INFINITY);
	    
		while (true) {
			//doMove();
			//learn();
			


			
		}
	}

	private void executeAction(int action, double variable) 
	{
		switch(action){
			case 0:	 attackFace();
				break;
			case 1:	 fire(variable);
				break;
			case 2:  avoid2();
				break;
			case 3:	runAway();
				break;
		}
		
	}


	public void avoid2()
	{
		// switch directions if we've stopped
		if (getVelocity() == 0)
			moveDirection *= -1;

		// circle our enemy
		setTurnRight(States.bearing + 90);
		setAhead(100 * moveDirection);
		
		if (getTime() % 100 == 0) {
			fire(1);
		}
	}
	
	public void attackFace()
	{
		setTurnRight(States.bearing);
		setAhead(100);
		
		if (getTime() % 100 == 0) {
			fire(1);
		}
	
	}
	
	public void runAway()
	{
		setTurnRight(States.bearing - 10);
		setAhead(-100);
		
		if (getTime() % 100 == 0) {
			fire(1);
		}
	}
	
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {

//      double absbearing_rad = (getHeading()+e.getBearing())%(360);
//      //this section sets all the information about our target
//      double enemyX = getX()+Math.sin(absbearing_rad)*e.getDistance(); //works out the x coordinate of where the target is
//      double enemyY = getY()+Math.cos(absbearing_rad)*e.getDistance(); //works out the y coordinate of where the target is
//      scantime = getTime();
      
		//Update Variables
		States.update(getEnergy(),		//positive energy is good (my energy is bigger)
					e.getDistance(),
					getGunHeat(),
					0,
					0);
		
//		if(getEnergy() > 40)
//		{
//			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
//				setFire(Math.min(400 / e.getDistance(), 3));
//		}
		
		double firePower = Math.min(400 / e.getDistance(), 3);
	    //  calculate gun turn toward enemy
	    double turn = getHeading() - getGunHeading() + e.getBearing();
	    // normalize the turn to take the shortest path there
	    setTurnGunRight(normalizeBearing(turn));
	    
	    //Squaring Off
	    //setTurnRight(e.getBearing() + 90);

	    
	    
	    
	    //QLEARNING
	    if(firstrun)
		{
			//record initial state as state1
			//Update Variables

			state1 = States.getCurrentState();
			state1NN = States.getCurrentStateNN();
			
			
			//take a random action
			//action1 = Q.actionOfMaxQ(state1);		//which is basically the first action.
			//executeAction(action1);
			
			Random randomGenerator = new Random();
			action1 = randomGenerator.nextInt(States.numActions - 1);
			executeAction(action1, firePower);
			
			firstrun = false;
		}else{
			//learn();
			
			
			int tempState = States.getCurrentState();
			int[] tempStateNN = States.getCurrentStateNN();
			if(tempState != state2)
			{
				
				//state2 is the CURRENT State. state1 is the previous
				
				//Q LEARNING
				state2 = tempState;
				state2NN = tempStateNN;
				//Q.QLearning(state1, action1, state2, reward);									//How can we update NN
				NeuralNet.trainNN(state1NN, action1, state2NN, reward);
				//store current state as state1
				state1 = state2;
				state1NN = state2NN;
				//reset reward
				reward = 0;
				
				
				//take action
				if(Math.random() > epsilon )
				{
					//action1 = Q.actionOfMaxQ(state1);											//Get it from NN
					
					//int tempStateNN[] = States.getCurrentStateNN();				//returns vector of Energy, Distance, and GunHeat
					action1 = NeuralNet.actionOfMaxQNN(tempStateNN);
					
					//executeAction(action1, firePower);
				}else{
				    Random randomGenerator = new Random();
				    action1 = randomGenerator.nextInt(States.numActions - 1);
				    //executeAction(action1, firePower);	
				}
			}
			
			if(action1 == 1 && getGunHeat() != 0)
				reward += -3;
		
			executeAction(action1, firePower);

			}
	    
	    
	    
//	    //SARSA
//	    if(run==0)
//		{
//			//record initial state as state1
//			//Update Variables
//
//			state3 = States.getCurrentState();
//			
//			
//			//take a random action
//			//action1 = Q.actionOfMaxQ(state1);		//which is basically the first action.
//			//executeAction(action1);
//			
//			Random randomGenerator = new Random();
//			action3 = randomGenerator.nextInt(States.numActions - 1);
//			executeAction(action3, firePower);
//			
//			run = 1;
//			
//		}else if(run==1){
//			
//			reward2 = reward;
//			
//			state2 = state3;
//			action2 = action3;
//			state3 = States.getCurrentState();
//			
//			
//			//take a random action
//			//action1 = Q.actionOfMaxQ(state1);		//which is basically the first action.
//			//executeAction(action1);
//			
//			Random randomGenerator = new Random();
//			action3 = randomGenerator.nextInt(States.numActions - 1);
//			executeAction(action3, firePower);
//			
//			
//			
//			run = 2;
//		}else{
//			
//			
//
//			
//			
//			int tempState = States.getCurrentState();
//			if(tempState != state3)
//			{
//				
//				reward1 = reward2;
//				reward2 = reward;
//				
//				state1 = state2;
//				action1 = action2;
//				state2 = state3;
//				action2 = action3;
//				
//				state3 = tempState;
//				Q.SARSA(state1, action1, state2, action2, reward1);
//
//				//reset reward
//				reward = 0;
//				
//				
//				//take action
//				if(Math.random() > epsilon )
//				{
//					action3 = Q.actionOfMaxQ(state1);		
//					//executeAction(action1, firePower);
//				}else{
//				    Random randomGenerator = new Random();
//				    action3 = randomGenerator.nextInt(States.numActions - 1);
//				    //executeAction(action1, firePower);	
//				}
//			}
//			
//			if(action3 == 1 && getGunHeat() != 0)
//				reward += -3;
//		
//			executeAction(action3, firePower);
//
//			}
			
		
		
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
	}


	//public void onHitByBullet(HitByBulletEvent e) {
	//	turnLeft(90 - e.getBearing());
	//}
	
	public void doMove() {

		// always square off against our enemy
		setTurnRight(States.bearing + 90);

		// strafe by changing direction every 20 ticks
		if (getTime() % 20 == 0) {
			moveDirection *= -1;
			setAhead(100 * moveDirection);
		}
		
		
		//setAhead(100 * moveDirection);
		
//		setAhead(100);
	}
	
	public void doMoveOpposite() {

		// always square off against our enemy
		setTurnRight(States.bearing + 90);

//		// strafe by changing direction every 20 ticks
//		if (getTime() % 20 == 0) {
//			moveDirection *= -1;
//			setAhead(150 * moveDirection);
//		}
		
		
		//setAhead(100 * moveDirection);
		
		setAhead(-100);
	}
	
	
	
	public void getCloser()
	{
		setTurnRight(normalizeBearing(States.bearing + 90 - (10 * moveDirection)));
	}
	
	  public void onWin(WinEvent event)
	  {
		reward += 10;
		//learn();
		//saveData();
		
		winsCount++;
//		time[roundCount++] = getTime();
//		saveDataTime();
	  }
	
	  public void onDeath(DeathEvent event)
	  {
		reward += -10;
		//learn();
		//saveData();
		
//		time[roundCount++] = getTime();
//		saveDataTime();
		
	  }
		
	  public void onBulletHit(BulletHitEvent e)
	  {

	     double change = e.getBullet().getPower() * 3 ;
	      //out.println("Bullet Hit: " + change);
	     reward = reward + (int)change;;
	     //learn();
	  }
	
	  public void onBulletHitBullet(BulletHitBulletEvent e) 
	  {
	    reward += -3;  
	    //learn();
	   }
	  
	  public void onHitRobot(HitRobotEvent e)
	  {
	    reward += -3;
	    
	    moveDirection *= -1; 
	    //learn();
	  }
	
	  public void onBulletMissed(BulletMissedEvent e)
	  {
	    double change = -e.getBullet().getPower();
	    //out.println("Bullet Missed: " + change);
	    reward += (int)change;
	    
	    //learn();
	  }
	
	  public void onHitByBullet(HitByBulletEvent e)
	  {

	      double power = e.getBullet().getPower();
	      double change = -3 * power;
	     
	       reward += (int)change;
	       
	       //learn();

	  }
	  
	  public void onHitWall(HitWallEvent e)
	  {
	       reward += -3;
	       moveDirection *= -1; 
			//learn();
	  }
	  
	  
	

//	  private void learn()
//	  {
//			state2 = States.getCurrentState();
//			Q.QLearning(state1, action1, state2, reward);
//			//store current state as state1
//			state1 = state2;
//			//reset reward
//			reward = 0;
//	  }
	  public void loadData()
	  {
		  
	    try
	    {
	      Q.loadData(getDataFile("data.dat"));
	    }
	    catch (Exception e)
	    {
	    }
	  }
	
	  public void saveData()
	  {
	    try
	    {
	      Q.saveData(getDataFile("data.dat"));
	    }
	    catch (Exception e)
	    {
	      out.println("Exception trying to write: " + e);
	    }
	  }
	  
	  public void saveDataTurns()
	  {
	    PrintStream w = null;
	    try
	    {
	      w = new PrintStream(new RobocodeFileOutputStream(getDataFile("turns.dat")));
	      for(int i=0; i<roundCount; i++)
	    	  w.println(turnsReached[i]);



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

	  
	  public void saveWinRate()
	  {
	    PrintStream w = null;
	    try
	    {
	      w = new PrintStream(new RobocodeFileOutputStream(getDataFile("winRate.dat")));
	      for(int i=0; i<winsRate.size(); i++)
	    	  w.println(winsRate.get(i));



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

	  public void onRoundEnded(RoundEndedEvent e)
              {
//		  		//turns - the number of turns that this round reached.
//		  		turnsReached[e.getRound()] = e.getTurns();
//		  		roundCount = e.getRound();
//		  		//saveDataTurns();
              }
	  
	  public void onBattleEnded(BattleEndedEvent e) {
	       saveData();
	       saveWinRate();
//	       saveDataTurns();
	   }

	  
	  
	  
}												

