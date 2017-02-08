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
	public static final int wheelRevCounter = 1; //TODO
	public static final int shooterWheel = 0; //TODO
	public static final int ADNSInturrupt = 4;
	public static final int NCS = 5;
//4 Encoder
	//FIX PORT NUMBERS
	public static final int RightFrontDriveEncoderA = 1; //FIXME
	public static final int RightFrontDriveEncoderB = 2; //FIXME
	public static final int LeftFrontDriveEncoderA = 3; //FIXME
	public static final int LeftFrontDriveEncoderB = 4; //FIXME
	public static final int RightBackDriveEncoderA = 6; //FIXME
	public static final int RightBackDriveEncoderB = 7; //FIXME
	public static final int LeftBackDriveEncoderA = 8; //FIXME
	public static final int LeftBackDriveEncoderB = 9; //FIXME

}
