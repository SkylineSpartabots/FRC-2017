package util;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

import org.usfirst.frc.team2976.robot.RobotMap;
import edu.wpi.first.wpilibj.AnalogInput; 
import java.util.Timer; 
import java.util.TimerTask; 

/**
 * The RPS (Robot Positioning system) attempts to give the most accurate information about the current heading of the robot
 * 
 * This class will run three threads that will simultaneously calculate, integrate and filter data from the following sensors:
 * 
 * Sensors: Wheel Encoders on Mecanum drive (this will be more complicated than normal)
 * ADNS Laser Tracker: a mouse
 * NAVX: experimental data double integration of acceleration
 * 
 * @author NeilHazra
 */

public class RPS {
	public AHRS ahrs;
	public ADNS_I2C adns_I2C;
	
	double startingX;
	double startingY;
	
	double targetX;
	double targetY;
			
	//private static final double ULTRASONIC_VOLTS_TO_INCHES = 41.06; //Black Ultrasonic Sensor
	private static final double ULTRASONIC_VOLTS_TO_INCHES = 108.356; //Green Ultrasonic Sensor
	private final Timer ultrasonicTimer;
	private final AnalogInput ultrasonicInput;
	private final Kalman ultrasonicKalman;
	private double ultrasonicDistanceInInches;
	
	
	public RPS(double targetX, double targetY) {
		this(targetX, targetY, 20);
	}
	
	public RPS(double targetX, double targetY, int ultrasonicMeasurementFrequencyInMillis) {
		ahrs = new AHRS(SPI.Port.kMXP);	
		adns_I2C = new ADNS_I2C();
		
		this.targetX = targetX;
		this.targetY = targetY;
		
		this.ultrasonicInput = new AnalogInput(RobotMap.ultrasonicSensor);
		
		double initialDistanceInInches = this.ultrasonicInput.getAverageVoltage() * ULTRASONIC_VOLTS_TO_INCHES;
		this.ultrasonicKalman = new Kalman(initialDistanceInInches); 
		this.setUltrasonicDistanceInInches(initialDistanceInInches);
		
		this.ultrasonicTimer = new Timer("UltrasonicDistanceTimer - " + RobotMap.ultrasonicSensor);
		this.ultrasonicTimer.schedule(new UltrasonicMeasurementTask(), 0, ultrasonicMeasurementFrequencyInMillis);
	}
	
	private synchronized void setUltrasonicDistanceInInches(double distanceInInches) {
		this.ultrasonicDistanceInInches = distanceInInches;
	}
	
	public synchronized double getUltrasonicDistanceInInches() {
		return this.ultrasonicDistanceInInches;
	}
	
	public void reset()	{
		startingX = getXDisplacementADNS();
		startingY = getYDisplacementADNS();
	}
	
	public double[] getVector()	{
		double[] toTarget = new double[2];
		toTarget[0] = targetX - getXDisplacementADNS();
		toTarget[1] = targetY - getYDisplacementADNS();
		return toTarget;
	}
	
	public double getTargetHeading()	{
		return Math.atan2(targetY,targetX);
	}
	
	public double getXDisplacementADNS()	{
		return adns_I2C.getX()-startingX;
	}
	
	public double getYDisplacementADNS()	{
		return adns_I2C.getY()-startingY;
	}
	
	public double getHeadingADNS()	{
		return adns_I2C.getTheta(); 
	}
	
	public double getAngle()	{
		return ahrs.getAngle();
	}
	
	private class UltrasonicMeasurementTask extends TimerTask
	{
		@Override
		public void run() {
			
			double distanceInInches = ultrasonicInput.getAverageVoltage() * ULTRASONIC_VOLTS_TO_INCHES;
			setUltrasonicDistanceInInches(ultrasonicKalman.getPredictedValue(distanceInInches));
			
		}
	}
}