package org.usfirst.frc.team2976.robot.subsystems;
import edu.wpi.first.wpilibj.Ultrasonic;

import org.usfirst.frc.team2976.robot.Robot;
import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.DriveWithJoystick;
import com.ctre.*;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
import util.RPS;
/**
 *@author NeilHazra
 */

//this class works, lets not touch it
public class DriveTrain extends Subsystem {
	public CANTalon rightFrontMotor, leftFrontMotor;
	public CANTalon rightBackMotor, leftBackMotor;
	public Ultrasonic ultrasonic;
	private RobotDrive m_drive;
	public PIDMain rotationLock;
	public PIDSource gyroSource;
	public boolean xBox;

	public DriveTrain() {
		xBox = true;
		Robot.rps.reset();
		Timer.delay(1); 
		
		gyroSource = new PIDSource() {
			public double getInput() {
				return getHeading();
			}
		};
		
		//rotationLock = new PIDMain(gyroSource, (int) getHeading(), 100, 0, 0, 0);	
		//rotationLock = new PIDMain(gyroSource, 0, 100, -0.016, -0.0003	, 0);	
		rotationLock = new PIDMain(gyroSource, (int) getHeading(), 100, -0.02, -0.0006	, 0);	
		//rotationLock = new PIDMain(gyroSource, (int) getHeading(), 100, -0.017, -0.0014	, 0);	
		
		rightFrontMotor = new CANTalon(RobotMap.RightFrontDriveMotor);
		leftFrontMotor = new CANTalon(RobotMap.LeftFrontDriveMotor);
		rightBackMotor = new CANTalon(RobotMap.RightBackDriveMotor);
		leftBackMotor = new CANTalon(RobotMap.LeftBackDriveMotor);
		
		rightBackMotor.enableBrakeMode(true);
		leftBackMotor.enableBrakeMode(true);
		rightFrontMotor.enableBrakeMode(true);
		leftFrontMotor.enableBrakeMode(true);
		
		m_drive = new RobotDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
		
		m_drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		m_drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		
		ultrasonic = new Ultrasonic(RobotMap.ultrasonicSensorA,RobotMap.ultrasonicSensorB);
		ultrasonic.setEnabled(true);
	}

	public void initDefaultCommand() {
		setDefaultCommand(new DriveWithJoystick());
	}
	public void rotationLockDrive(double x, double y) {
		m_drive.mecanumDrive_Cartesian(x, y, rotationLock.getOutput(), 0);
	}
	public void rotationLockFieldCentricDrive(double x, double y){
		m_drive.mecanumDrive_Cartesian(x, y, rotationLock.getOutput(), getHeading());
	}		
	public double getHeading() {
		return Robot.rps.getAngle();
	}
	public double getDistanceInches()	{
		return ultrasonic.getRangeInches();
	}
	// Rounds numbers to 2 decimal places to make SmartDashboard nicer. Could
	// also be used to prevents OPs things
	public double round(double input) {
		double roundOff = Math.round(input * 100.0) / 100.0;
		return roundOff;
	}
	  public double driveCurve(double input, boolean invert, boolean slowMode){
	    	double slider = 1;	
	    	if (slowMode){
	    		slider = 0.4;
	    	}
	    	return driveCurve(input, invert, slider);
	}    
	 //Returns curved drive values with Slider (which indicates sensitivity)    
	public double driveCurve(double input, boolean invert, double slider) {
		double value = 0.0;
		slider = (slider + 1) / 2;
		//This code was written by Jasmine Cheng, please talk to her if you are concerned with redundancy, overcomplication, and nested if's
		//It works tho :)
		if (invert == false) {
			if (input > 0) {
				if (input < 0.1) {
					input = 0;
				} else {
					input = slider * ((0.09574 * Math.pow(10, input * 1.059)) - 0.09574);
				}
			} else {
				if (input > -0.1) {
					input = 0;
				} else {
					input = -1 * input;
					input = -slider * ((0.09574 * Math.pow(10, input * 1.059)) - 0.09574);
				}
			}
		} else {
			if (input > 0) {
				if (input < 0.1) {
					input = 0;
				} else {
					input = slider * ((0.09574 * Math.pow(10, input * 1.059)) - 0.09574);
				}
			} else {
				if (input > -0.1) {
					input = 0;
				} else {
					input = -1 * input;
					input = -slider * ((0.09574 * Math.pow(10, input * 1.059) - 0.09574));
				}
			}
		}
		value = input;
		return value;
	}
	/**
	 * This is the basic, feedback less drive system
	 * @param x
	 * @param y
	 * @param rotation
	 */
	public void openLoopCartesianDrive(double x, double y, double rotation) {
		m_drive.mecanumDrive_Cartesian(x, y, rotation, 0);
	}
	public void openLoopFieldCentricDrive(double x, double y, double rotation) {
		m_drive.mecanumDrive_Cartesian(x, y, rotation, getHeading());
	}
	public void tankDrive(double l, double r)	{
		m_drive.tankDrive(-l, r);//left motor reversed
	}
	
}
	