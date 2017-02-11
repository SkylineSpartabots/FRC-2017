package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.OI.Axis;
import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveWithJoystick extends Command {
	public DriveWithJoystick() {
		requires(Robot.drivetrain);
	}

	protected void initialize() {
	}

	protected void execute() {			
		double forward;
		double strafe;
		double ry;
		double rotation;
		boolean slowMode = false; 
		double slider;
		
//Sets values depending on which controller is used
		if (Robot.drivetrain.xBox == true){
			strafe = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
			forward = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
			ry = Robot.oi.driveStick.getRawAxis(OI.Axis.RY.getAxisNumber());
			rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
			slowMode = true; 
		}	else {
			strafe = Robot.oi.driveStick.getRawAxis(0);
			forward = Robot.oi.driveStick.getRawAxis(1);
			rotation = Robot.oi.driveStick.getRawAxis(2);
			slider = Robot.oi.driveStick.getRawAxis(3);
			if (Robot.oi.driveStick.getRawButton(1)){
				slowMode = false;
			}else {
				slowMode = true;
			};
		}
		
//Put raw values SmartDashboard
			
		
//Adjust values to the curve
			if (Robot.drivetrain.xBox==true){
				forward = Robot.drivetrain.driveCurve(forward, true, Robot.drivetrain.getSlider(slowMode));
				strafe = Robot.drivetrain.driveCurve(strafe, false, Robot.drivetrain.getSlider(slowMode));
				rotation = Robot.drivetrain.driveCurve(rotation, false, Robot.drivetrain.getSlider(slowMode));	
			} else {
				forward = Robot.drivetrain.driveCurve(forward, false, Robot.drivetrain.getSlider(slowMode));
				strafe = Robot.drivetrain.driveCurve(strafe, true, Robot.drivetrain.getSlider(slowMode));
				rotation = Robot.drivetrain.driveCurve(rotation, false, Robot.drivetrain.getSlider(slowMode));
				
			}
		
//Put adjusted values on SmartDashboard
			SmartDashboard.putNumber("Foward", Robot.drivetrain.round(forward));
			SmartDashboard.putNumber("Strafe", Robot.drivetrain.round(strafe));
			SmartDashboard.putNumber("Rotation", Robot.drivetrain.round(rotation));
			SmartDashboard.putNumber("Right front encoder", Robot.drivetrain.getRightFrontDriveEncoderCount());
			
		//	if(Math.abs(rotation)<0.3)	{
			//	Robot.drivetrain.rotationLockDrive(strafe, forward);
			//}	else	{
				Robot.drivetrain.drive(strafe, forward, rotation);
			//}
			/*
			if (Robot.oi.driveStick.getRawAxis(Axis.RTrigger.getAxisNumber()) > 0.1) {
				Robot.drivetrain.rotationLockDrive(Robot.oi.driveStick.getRawAxis(Axis.RTrigger.getAxisNumber()), 0);
			} else if (Robot.oi.driveStick.getRawAxis(Axis.LTrigger.getAxisNumber()) > 0.1) {
				Robot.drivetrain.rotationLockDrive(Robot.oi.driveStick.getRawAxis(Axis.LTrigger.getAxisNumber()), 0);
			} else {
				Robot.drivetrain.m_drive.tankDrive(ly, ry);
			}
			*/
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}