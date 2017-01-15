package org.usfirst.frc.team2976.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import org.usfirst.frc.team2976.robot.RobotMap;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
/**
 *
 */
public class DriveTrain extends Subsystem {
	private SpeedController rightFrontMotor, leftFrontMotor;
    private SpeedController rightBackMotor, leftBackMotor;
    private Encoder rightFrontDriveEncoder, leftFrontDriveEncoder, rightBackDriveEncoder, leftBackDriveEncoder;
    public AHRS ahrs;
	public RobotDrive m_drive;
	
	
	public DriveTrain()	{
		ahrs = new AHRS(SPI.Port.kMXP);
		
    	rightFrontMotor = new CANTalon(RobotMap.RightFrontDriveMotor);
    	leftFrontMotor = new CANTalon(RobotMap.LeftFrontDriveMotor);
    	rightBackMotor = new CANTalon(RobotMap.RightBackDriveMotor);
    	leftBackMotor = new CANTalon(RobotMap.LeftBackDriveMotor);	
    	
    	rightFrontDriveEncoder = new Encoder(RobotMap.RightFrontDriveEncoderA, RobotMap.RightFrontDriveEncoderB);
    	leftFrontDriveEncoder = new Encoder(RobotMap.LeftFrontDriveEncoderA, RobotMap.LeftFrontDriveEncoderB);
    	rightBackDriveEncoder = new Encoder(RobotMap.RightBackDriveEncoderA, RobotMap.RightBackDriveEncoderB);
    	leftBackDriveEncoder = new Encoder(RobotMap.LeftBackDriveEncoderA, RobotMap.LeftBackDriveEncoderB);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    public void drive(double y) {
    	rightFrontMotor.set(y);
    	leftFrontMotor.set(y);
    	rightBackMotor.set(y);
    	leftBackMotor.set(y);
    }
    
    
    //get numbers of encoder ticks
    private double getRightFrontDriveEncoderCount() {	
    	return rightFrontDriveEncoder.get();
    }
    private double getLeftFrontDriveEncoderCount() {	
    	return leftFrontDriveEncoder.get();
    }
    private double getRightBackDriveEncoderCount() {	
    	return rightBackDriveEncoder.get();
    }
    private double getLeftBackDriveEncoderCount() {	
    	return leftBackDriveEncoder.get();
    }
    //get velocities
    private double getRightFrontDriveEncoderVelocity() {
    	return rightFrontDriveEncoder.getRate();
    }
    private double getLeftFrontDriveEncoderVelocity() {
    	return leftFrontDriveEncoder.getRate();
    }
    private double getRightBackDriveEncoderVelocity() {
    	return rightBackDriveEncoder.getRate();
    }
    private double getLeftBackDriveEncoderVelocity() {
    	return leftBackDriveEncoder.getRate();
    }
    private double getAngle(){
		return ahrs.getAngle();
    }
}