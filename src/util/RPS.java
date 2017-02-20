package util;
import org.usfirst.frc.team2976.robot.RobotMap;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	private static final double ULTRASONIC_VOLTS_TO_METERS = 2.5918353351028; //TODO: convert units
	
	private final AnalogInput ultrasonic;
	
	private final Kalman ultrasonicKalman;
	
	public RPS() {
		ahrs = new AHRS(SPI.Port.kMXP);	
		adns_I2C = new ADNS_I2C();
		
		ultrasonic = new AnalogInput(RobotMap.ANALOG_ULTRASONIC);
		
		ultrasonicKalman = new Kalman(1.4d, 64d, ultrasonic.getAverageVoltage() * ULTRASONIC_VOLTS_TO_METERS);
	}
	public void test()	{
		adns_I2C.saveData();
	}
	public double getUltrasonicDistance() {
		return ultrasonicKalman.getPredictedValue(ultrasonic.getAverageVoltage() * ULTRASONIC_VOLTS_TO_METERS);
	}
	public double getXDisplacementADNS()	{
		return 0;
		//return adns.x_total;
	}
	public double getYDisplacementADNS()	{
		return 0;
		//return adns.y_total;
	}
	public double getXDisplacementNAVX()	{
		return ahrs.getDisplacementX();
	}
	public double getYDisplacementNAVX()	{
		return ahrs.getDisplacementY();
	}
	public double getAngle()	{
		return ahrs.getAngle();
	}
	public double getHeadingADNS()	{
		return 0; 
	}
	public double getHeadingNAVX()	{
		return Math.atan2(ahrs.getVelocityY(),ahrs.getVelocityY());
	}
}