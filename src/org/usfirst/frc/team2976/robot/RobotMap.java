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
	public static final int RightBackDriveMotor = 6; 
	public static final int LeftBackDriveMotor = 3;	
	
	//Other motors
	public static final int climberMotor = 0;
	public static final int gearIntakeMotor = 5; //lifts the robot //should be 5
	public static final int gearPivotMotor = 4; //TODO
	
	//limit switch for gear
	public static final int limitSwitch = 0;
}
