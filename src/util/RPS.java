package util;
import com.kauailabs.navx.frc.AHRS;

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
	
	double startingX;
	double startingY;
	
	double targetX;
	double targetY;
	
	
	public RPS(double targetX, double targetY) {
		ahrs = new AHRS(SPI.Port.kMXP);	
		adns_I2C = new ADNS_I2C();
		
		this.targetX = targetX;
		this.targetY = targetY;
		
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
	
}