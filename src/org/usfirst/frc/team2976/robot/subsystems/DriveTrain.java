package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.DriveWithJoystick;
//import com.ctre.*;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
import util.RPS;

/**
 *
 */
public class DriveTrain extends Subsystem {
	private SpeedController rightFrontMotor, leftFrontMotor;
	private SpeedController rightBackMotor, leftBackMotor;

	private Encoder rightFrontDriveEncoder, leftFrontDriveEncoder, rightBackDriveEncoder, leftBackDriveEncoder;

	public RobotDrive m_drive;
	public PIDMain rotationLock;
	public PIDSource gyroSource;
	public RPS rps;
	public boolean xBox;

	public DriveTrain() {
		xBox = true;
		rps = new RPS();

		gyroSource = new PIDSource() {
			public double getInput() {
				return getHeading();
			}
		};

		rotationLock = new PIDMain(gyroSource, 0, 100, -0.007, -0.000, 0);

		rightFrontMotor = new CANTalon(RobotMap.RightFrontDriveMotor);
		leftFrontMotor = new CANTalon(RobotMap.LeftFrontDriveMotor);
		rightBackMotor = new CANTalon(RobotMap.RightBackDriveMotor);
		leftBackMotor = new CANTalon(RobotMap.LeftBackDriveMotor);

		rightFrontDriveEncoder = new Encoder(RobotMap.RightFrontDriveEncoderA, RobotMap.RightFrontDriveEncoderB);
		leftFrontDriveEncoder = new Encoder(RobotMap.LeftFrontDriveEncoderA, RobotMap.LeftFrontDriveEncoderB);
		rightBackDriveEncoder = new Encoder(RobotMap.RightBackDriveEncoderA, RobotMap.RightBackDriveEncoderB);
		leftBackDriveEncoder = new Encoder(RobotMap.LeftBackDriveEncoderA, RobotMap.LeftBackDriveEncoderB);

		m_drive = new RobotDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
		m_drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		m_drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
	}

	public void initDefaultCommand() {
		setDefaultCommand(new DriveWithJoystick());
	}

	public void rotationLockDrive(double x, double y) {
		m_drive.mecanumDrive_Cartesian(x, y, rotationLock.getOutput(), 0);
	}

	public double getHeading() {
		return rps.getAngle();
	}

	// Rounds numbers to 2 decimal places to make SmartDashboard nicer. Could
	// also be used to prevents OPs things
	public double round(double input) {
		double roundOff = Math.round(input * 100.0) / 100.0;
		return roundOff;
	}

	// Returns curved values with SlowMode Button
	public double driveCurve(double input, boolean slowMode) {
		double slider = 1;
		if (slowMode) {
			slider = 0.4;
		}
		return driveCurve(input, slider);
	}

	// Returns curved drive values with Slider (which indicates sensitivity)
	public double driveCurve(double input, double slider) {
		slider = (slider + 1) / 2;
		if (input > 0.1) {
			input = slider * ((0.09574 * Math.pow(10, input * 1.059)) - 0.09574);
		}else if(input < -0.1){
			input = -slider * ((0.09574 * Math.pow(10, input * 1.059)) - 0.09574);
		} else { 
			return 0;
		}
		return input;
	}

	public void drive(double x, double y, double rotation) {
		m_drive.mecanumDrive_Cartesian(x, y, rotation, 0);
		SmartDashboard.putNumber("Right Front Motor", round(rightFrontMotor.get()));
		SmartDashboard.putNumber("Left Front Motor", round(leftFrontMotor.get()));
		SmartDashboard.putNumber("Right Back Motor", round(rightBackMotor.get()));
		SmartDashboard.putNumber("Left Back Motor", round(leftBackMotor.get()));
	}

	// get number of rotations from encoder and compare it to distance
	double getRightFrontDriveEncoderCount() {
		return rightFrontDriveEncoder.get();
	}

	public double getLeftFrontDriveEncoderCount() {
		return leftFrontDriveEncoder.get();
	}

	public double getRightBackDriveEncoderCount() {
		return rightBackDriveEncoder.get();
	}

	public double getLeftBackDriveEncoderCount() {
		return leftBackDriveEncoder.get();
	}

}
