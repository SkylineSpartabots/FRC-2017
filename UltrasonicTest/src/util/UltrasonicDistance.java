package util;

import edu.wpi.first.wpilibj.AnalogInput;

import java.util.Timer;
import java.util.TimerTask; 

public class UltrasonicDistance {
	
	Timer sonarTimer; 
	AnalogInput sonarInput; 
	double sonarVoltage;
	int sampleCount;
	double distance;
	
	public UltrasonicDistance(int portNumber,int frequencyPerSecond)
	{
		sonarTimer = new Timer("UltrasonicDistanceTimer - " + portNumber);
		sonarInput = new AnalogInput(portNumber);
		sampleCount = 1;
		sonarVoltage = 0;
		distance = 0;
		
		sonarTimer.scheduleAtFixedRate(new MeasurementTask(), 0, 1000/frequencyPerSecond);
	}
	
	public UltrasonicDistance()
	{
		this(0,200);	
	}
	
	public double getDistanceInInches()
	{
		return distance;	
	}
	
	private class MeasurementTask extends TimerTask
	{

		@Override
		public void run() {
			    	
	    	sonarVoltage += (sonarInput.getAverageVoltage() * 512);
			sampleCount++;
				
			if (sampleCount == 60)
			{
				distance = (sonarVoltage / 300);
				
				sonarVoltage = 0;
				sampleCount = 1;	
			}
		}
	}
}
