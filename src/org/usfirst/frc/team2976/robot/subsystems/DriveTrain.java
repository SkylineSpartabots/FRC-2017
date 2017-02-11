package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;
import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.DriveWithJoystick;
import com.ctre.*;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
import util.RPS;	
/**
 *
 */
public class DriveTrain extends Subsystem  {
	private SpeedController rightFrontMotor, leftFrontMotor;
    private SpeedController rightBackMotor, leftBackMotor;
    public RobotDrive m_drive;
    public PIDMain rotationLock;
    public PIDSource gyroSource;
	public RPS rps;
	public boolean xBox;
	
	private Encoder rightFrontDriveEncoder, leftFrontDriveEncoder, rightBackDriveEncoder, 
   leftBackDriveEncoder;
	
	public DriveTrain()	{
		//if using Logitech Joystick, set xBox to false
		xBox = true;
		rps = new RPS();
		
		gyroSource = new PIDSource()	{
			public double getInput() {
				return getHeading();
			}
		};		
		rotationLock = new PIDMain(gyroSource, 0, 100, -0.0007, -0.000, 0);	
		
		rightFrontMotor = new CANTalon(RobotMap.RightFrontDriveMotor);
    	leftFrontMotor = new CANTalon(RobotMap.LeftFrontDriveMotor);
    	rightBackMotor = new CANTalon(RobotMap.RightBackDriveMotor);
    	leftBackMotor = new CANTalon(RobotMap.LeftBackDriveMotor);	                                                                                                                            
    	
    	m_drive  = new RobotDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    	m_drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
    	m_drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
    	
        rightFrontDriveEncoder = new Encoder(7,8);
    	
    	
	}
	public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoystick());
    }

    public void rotationLockDrive(double x, double y)	{
    	m_drive.mecanumDrive_Cartesian(x, y,  rotationLock.getOutput(), 0);
    	SmartDashboard.putNumber("Right Front Motor", round(rightFrontMotor.get()));
    	SmartDashboard.putNumber("Left Front Motor", round(leftFrontMotor.get()));
    	SmartDashboard.putNumber("Right Back Motor", round(rightBackMotor.get()));
    	SmartDashboard.putNumber("Left Back Motor", round(leftBackMotor.get()));
    
    }
    public double getHeading(){
    	return rps.getAngle();		
    }
   
 //Rounds numbers to 2 decimal places to make SmartDashboard nicer. Could also be used to prevents OPs things
    public double round(double input){
    	double roundOff = Math.round(input*100.0)/100.0;
    	return roundOff;
    }
    public double getSlider(boolean slow){
    	if (slow == true){
    		return 0.4;
    	} else {
    		return 1;
    	}
    }
  //Returns curved values with SlowMode Button   
   
 //Returns curved drive values with Slider (which indicates sensitivity)    
    public double driveCurve(double input, boolean invert, double slider){
    	double value = 0.0;
    	slider = (slider + 1)/2;
    	if (invert == false){
		if (input > 0){
			if (input < 0.1) {
			 input = 0;
			} else {
				input = slider*((0.09574*Math.pow(10, input * 1.059))-0.09574);
			}
		} else {
			if (input > -0.1){ 
				input = 0;
			} else {
				input = -1 *input;
				input = -slider*((0.09574*Math.pow(10, input * 1.059))-0.09574);
			}	
		}
    	} else {
		if (input > 0) {
			if (input < 0.1) {
				input = 0;
			} else {	
				input = slider*((0.09574*Math.pow(10, input * 1.059))-0.09574);
			}
		} else {
			if (input > -0.1){
				input = 0;
			} else {
				input = -1 * input;
				input = -slider*((0.09574*Math.pow(10, input * 1.059)-0.09574));
			}
		}	
    }
    	value = input;
		return value;
}

    public void drive(double x, double y, double rotation) {
    	mecanumDrive_Cartesian(x, y, rotation, 0);
    	SmartDashboard.putNumber("Right Front Motor", round(rightFrontMotor.get()));
    	SmartDashboard.putNumber("Left Front Motor", round(leftFrontMotor.get()));
    	SmartDashboard.putNumber("Right Back Motor", round(rightBackMotor.get()));
    	SmartDashboard.putNumber("Left Back Motor", round(leftBackMotor.get()));
    
    	
    }
    
    public void mecanumDrive_Cartesian(double x, double y, double rotation, double gyroAngle) {
    	double m_maxOutput = 1; 
        @SuppressWarnings("LocalVariableName")
        double xIn = x;
        @SuppressWarnings("LocalVariableName")
        double yIn = y;
        
        if (x>0.5){
        	y = 0;
        }
        // Negate y for the joystick.
        yIn = -yIn;
        
        SmartDashboard.putNumber("xIn", x);
        SmartDashboard.putNumber("yIn", y);
        SmartDashboard.putNumber("Rotation In", rotation);
        
        // Compenstate for gyro angle.
        double[] rotated = rotateVector(xIn, yIn, gyroAngle);
        xIn = rotated[0];
        yIn = rotated[1];

        double[] wheelSpeeds = new double[4];
        wheelSpeeds[MotorType.kFrontLeft.value] = xIn + yIn + rotation;
        wheelSpeeds[MotorType.kFrontRight.value] = -xIn + yIn - rotation;
        wheelSpeeds[MotorType.kRearLeft.value] = -xIn + yIn + rotation;
        wheelSpeeds[MotorType.kRearRight.value] = xIn + yIn - rotation;
        
        normalize(wheelSpeeds);
        leftFrontMotor.set(wheelSpeeds[MotorType.kFrontLeft.value] * m_maxOutput);
        rightFrontMotor.set(wheelSpeeds[MotorType.kFrontRight.value] * m_maxOutput);
        leftBackMotor.set(wheelSpeeds[MotorType.kRearLeft.value] * 0.73);
        rightBackMotor.set(wheelSpeeds[MotorType.kRearRight.value] * 0.73);
        
        SmartDashboard.putNumber("WS Front Left", wheelSpeeds[MotorType.kFrontLeft.value]);
        SmartDashboard.putNumber("WS Front Right", wheelSpeeds[MotorType.kFrontRight.value]);
        SmartDashboard.putNumber("WS Back Right", wheelSpeeds[MotorType.kRearRight.value]);
        SmartDashboard.putNumber("WS Back Left", wheelSpeeds[MotorType.kRearLeft.value]);
        /*if (m_safetyHelper != null) {
          m_safetyHelper.feed();
        }*/
      }
    protected static double[] rotateVector(double x, double y, double angle) {
        double cosA = Math.cos(angle * (3.14159 / 180.0));
        double sinA = Math.sin(angle * (3.14159 / 180.0));
        double[] out = new double[2];
        out[0] = x * cosA - y * sinA;
        out[1] = x * sinA + y * cosA;
        return out;
      }
    
    protected static void normalize(double[] wheelSpeeds) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        for (int i = 0; i < 4; i++) {
          double temp = Math.abs(wheelSpeeds[i]);
          if (maxMagnitude < temp) {
            maxMagnitude = temp;
          }
        }
        if (maxMagnitude > 1.0) {
          for (int i = 0; i < 4; i++) {
            wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
          }
        }
      }
    
    public double getRightFrontDriveEncoderCount() {
    	
		return rightFrontDriveEncoder.get();
	}

}
