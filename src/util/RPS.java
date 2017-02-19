package util;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The RPS (Robot Positioning system) attempts to give the most accurate
 * information about the current heading of the robot
 * 
 * This class will run three threads that will simultaneously calculate,
 * integrate and filter data from the following sensors:
 * 
 * Sensors: Wheel Encoders on Mecanum drive (this will be more complicated than
 * normal) ADNS Laser Tracker: a mouse NAVX: experimental data double
 * integration of acceleration
 * 
 * @author NeilHazra
 */

public class RPS {
	public AHRS ahrs;
	public ADNS_I2C adns_I2C;

	private AnalogInput sonarInput;
	
	public RPS() {
		ahrs = new AHRS(SPI.Port.kMXP);
		adns_I2C = new ADNS_I2C();
		sonarInput = new AnalogInput(0); // ultrasonic is connected to port 0
	}

	public double getXDisplacementADNS() {
		return adns_I2C.getX();
	}

	public double getYDisplacementADNS() {
		return adns_I2C.getY();
	}

	public double getHeadingADNS() {
		return adns_I2C.getTheta();
	}

	public double getAngle() {
		return ahrs.getAngle();
	}

	public double getDistance() {
		double distance = ((sonarInput.getAverageVoltage() * 512) / 5);
		SmartDashboard.putNumber("distance", distance);
		
		return distance;
	}
}