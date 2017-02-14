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
//rev counter for wheel
	public static final int wheelRevCounter = 9; //TODO
	public static final int shooterWheel = 10; //TODO
	public static final int ultrasonicPing = 7;
	public static final int ultrasonicEcho = 8;
	public static final int ADNSInturrupt = 4;
	public static final int NCS = 5;
	
	public static final int climberMotor = 6;
}
