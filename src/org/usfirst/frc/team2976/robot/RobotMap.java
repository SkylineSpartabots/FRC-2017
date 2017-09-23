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
	public static final int intakeMotor = 8;
	public static final int climberMotor = 9;
	
	public static final int gearPivotMotor = 5; 
	public static final int gearMotor = 6;

	
	public static final int shooter = 7;
	//servos for hopper
	public static final int hopperServoRight = 1;
	public static final int hopperServoLeft = 0;
	
	//ultrasonic sensors-- two ports
	public static final int ultrasonicSensorA = 2; 
	public static final int ultrasonicSensorB = 1; 
	
	
	
	
	
	public static final int climbButton = 0;
	
	public static final int intakeBalls = 0;
	public static final int shoot = 0;
	
	public static final int raiseGearMech = 0;
	public static final int lowerGearMech = 0;
	
	
	
}
