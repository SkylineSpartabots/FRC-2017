package org.usfirst.frc.team2976.robot;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */	
public class RobotMap {
//4 Drive Motors
	public static final int RightFrontDriveMotor = 1; 
	public static final int LeftFrontDriveMotor = 2; 
	public static final int RightBackDriveMotor = 4; 
	public static final int LeftBackDriveMotor = 3;	
	//Other motors
	public static final int intakeMotor = 5;
	public static final int climberMotor = 6;
	public static final int hopperMotor = 7; //TODO
	//servos for hopper
	public static final int hopperServoRight = 1;
	public static final int hopperServoLeft = 0;
	//ultrasonic sensors-- two ports
	public static final int ultrasonicSensorA = 2; 
	public static final int ultrasonicSensorB = 1; 
}
