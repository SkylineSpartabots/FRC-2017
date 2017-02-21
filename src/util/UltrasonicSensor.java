package util;

import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2976.robot.RobotMap;

import edu.wpi.first.wpilibj.AnalogInput;

public class UltrasonicSensor {
	private final Timer ultrasonicTimer;
	private final AnalogInput ultrasonicInput;
	private final Kalman ultrasonicKalman;
	private double ultrasonicDistanceInInches;
	private double voltsToInches;
	
	public UltrasonicSensor(int portNumber, int frequencyInMilliseconds, double voltsToInches) {
		
		this.ultrasonicInput = new AnalogInput(portNumber);
		
		double initialDistanceInInches = this.ultrasonicInput.getAverageVoltage() * voltsToInches;
		this.ultrasonicKalman = new Kalman(initialDistanceInInches); 
		this.setUltrasonicDistanceInInches(initialDistanceInInches);
		this.voltsToInches = voltsToInches;
		
		this.ultrasonicTimer = new Timer("UltrasonicDistanceTimer - " + portNumber);
		this.ultrasonicTimer.schedule(new UltrasonicMeasurementTask(), 0, frequencyInMilliseconds);
	}
	
	private synchronized void setUltrasonicDistanceInInches(double distanceInInches) {
		this.ultrasonicDistanceInInches = distanceInInches;
	}
	
	public synchronized double getUltrasonicDistanceInInches() {
		return this.ultrasonicDistanceInInches;
	}
	
	private class UltrasonicMeasurementTask extends TimerTask
	{
		@Override
		public void run() {
			
			double distanceInInches = ultrasonicInput.getAverageVoltage() * voltsToInches;
			setUltrasonicDistanceInInches(ultrasonicKalman.getPredictedValue(distanceInInches));
			
		}
	}

}
