package myRobot3;

public class States {

	static int numStates =2*3*2;
	static int numActions = 4;
	 static final int numEnergy =2;
	//private static final int numEnergyEnm = 5;
	 static final int numGunHeat = 2;
	 static final int numBearing = 4;
	 static final int numDistance = 3;
	private static final int distanceStep = 150;
	private static int Map[][][];
	
	
	 static int energy, energyEnm, heading, bearing, distance;
	double enemyX, enemyY, scantime;
	static int gunHeat;
	
	States()
	{
		Map = new int[numEnergy][numDistance][numGunHeat];
		initialize();
	}
	
	void initialize()
	{
		int count = 0;
		for (int i=0; i < numEnergy; i++)
			for (int m=0; m < numDistance; m++)
				for (int k=0; k < numGunHeat; k++)
							Map[i][m][k] = count++;

	      numStates = count;
	      energy=2;
	      heading=0;
	      bearing=0;
	      distance=10;
	      
	}
	
	void update(double energy,
				double distance,
				double gunheat,
				double enemyX,
				double enemyY)
				{
		
		//Update Variables
		this.energy = myGetEnergy(energy);
		this.distance = myGetDistance(distance);
		this.gunHeat = myGunHeat(gunheat);
		this.enemyX= enemyX;
		this.enemyY= enemyY;
				}
	int myGetEnergy(double energy)
	{
		if(energy < 50)
			return 0;
		else
			return 1;
	}
	
	int myGunHeat(double gunheat)
	{
		if(gunheat > 0)
			return 1;
		else
			return 0;
	}
	
	
	int myGetHeading(double heading)
	{
		//return 2;
		//System.out.println(heading);
		return (int) (heading / 90);
	}

	public int myGetBearing(double bearing) 
	{
		//return 2;
		return (int) ((bearing+180) / 90);
	}

	public int myGetDistance(double distance) 
	{
		int newDistance = (int) distance / distanceStep;
		
		if(newDistance < 0)
			newDistance = newDistance * -1;
		
		if (newDistance < numDistance)
			return newDistance;
		else
			return numDistance - 1;		//-1 for boundary
	}
	
	public int getCurrentState()
	{
		return Map[energy][distance][gunHeat];
	}
	
	public int[] getCurrentStateNN()
	{
		int temp[] = new int[3];
		temp[0] = energy;
		temp[1] = distance;
		temp[2] = gunHeat;
		return temp;
	}
	
	
}
