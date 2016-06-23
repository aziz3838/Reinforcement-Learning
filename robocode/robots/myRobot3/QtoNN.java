package myRobot3;

import java.io.FileNotFoundException;

public class QtoNN {

	public static void main(String[] args) {

		String input_NN[] = new String[48];	//48 = NUMBER OF STATES * ACTION
		
		int count = 0;
			for (int i=0; i < States.numEnergy; i++)
				for (int m=0; m < States.numDistance; m++)
					for (int k=0; k < States.numGunHeat; k++)
						for(int z=0; z<States.numActions; z++)
						{
							if(i==0)
								input_NN[count] = "01";
							else
								input_NN[count] = "10";
							
							if(m==0)
								input_NN[count] += "001";
							else if(m==1)
								input_NN[count] += "010";
							else
								input_NN[count] += "100";
							
							if(k==0)
								input_NN[count] += "01";
							else
								input_NN[count] += "10";
							
							if(z==0)
								input_NN[count] += "0001";
							else if(z==1)
								input_NN[count] += "0010";
							else if(z==2)
								input_NN[count] += "0100";
							else
								input_NN[count] += "1000";
							
							//System.out.println(input_NN[count]);
							count++;
						
						}
		
	}
	

}
