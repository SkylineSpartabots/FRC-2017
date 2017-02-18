package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * 
 */

//Class works, don't touch

public class DriveWithJoystick extends Command {
		
	public DriveWithJoystick() {
		requires(Robot.drivetrain);
	}
	protected void initialize() {
	
	}

	protected void execute() {				
		
		Robot.rps.test();
		
		double forward;
		double strafe;
		double rotation;
		boolean slowMode = false; 
		
		if (Robot.drivetrain.xBox = true){
			strafe = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
			forward = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
			rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
			slowMode = false; 
		}	else {
			strafe = Robot.oi.driveStick.getRawAxis(0);
			forward = Robot.oi.driveStick.getRawAxis(1);
			rotation = Robot.oi.driveStick.getRawAxis(2);
	
			if (Robot.oi.driveStick.getRawButton(1)){
				slowMode = false;
			}else {
				slowMode = true;
			};
		}
		
		/*
		SmartDashboard.putNumber("Raw forward", Robot.drivetrain.round(forward));
		SmartDashboard.putNumber("Raw strafe", Robot.drivetrain.round(strafe));
		SmartDashboard.putNumber("Raw rotation", Robot.drivetrain.round(rotation));
		*/
		
		forward = Robot.drivetrain.driveCurve(forward, false, slowMode);
		strafe = Robot.drivetrain.driveCurve(strafe	, true, slowMode);
		rotation = Robot.drivetrain.driveCurve(rotation, false, slowMode);	
		
		double RT = Robot.oi.driveStick.getRawAxis(OI.Axis.RTrigger.getAxisNumber());
		double LT = Robot.oi.driveStick.getRawAxis(OI.Axis.LTrigger.getAxisNumber());
		
		if (RT > 0.1) {
			strafe = RT;
		}
		if (LT > 0.1) {
			strafe = -LT;
		}
		//Put adjusted values on SmartDashboard
		SmartDashboard.putNumber("Foward", Robot.drivetrain.round(forward));
		SmartDashboard.putNumber("Strafe", Robot.drivetrain.round(strafe));
		SmartDashboard.putNumber("Rotation", Robot.drivetrain.round(rotation));
		
		if(Math.abs(rotation)<0.1)	{ //if the driver doesn't want to rotate
			if(Robot.drivetrain.rotationLock.enable(false))	{ //enable PID soft, if successful run PID
				Robot.drivetrain.rotationLockDrive(strafe/2,forward);  //run with locked rotation
			}	else	{ //if soft disable is set, run regularly for a bit
				Robot.drivetrain.drive(strafe, forward, 0);  
				Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle()); //save the current angle for seamless transfer
			}			
		}	else	{
			Robot.drivetrain.rotationLock.disable(500); //disable the PID for the next 300+ milli-seconds
			Robot.drivetrain.drive(strafe, forward, rotation); //run regularly
			Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle()); //save the current angle for seamless transfer
		}
		
		//put the vision test stuff on the dashboard
		
		SmartDashboard.putNumber("vision", (int)SmartDashboard.getNumber("test", 0));		
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

