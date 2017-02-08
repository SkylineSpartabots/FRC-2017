package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
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
	
	Robot.drivetrain.rps.adns_I2C.saveData();
	SmartDashboard.putNumber("Value", 1828);
//Sets values depending on which controller is used
	if (Robot.drivetrain.xBox = true){
		strafe = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
		forward = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
		ry = Robot.oi.driveStick.getRawAxis(OI.Axis.RY.getAxisNumber());
		rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
		slowMode = false; 
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
		SmartDashboard.putNumber("Raw forward", Robot.drivetrain.round(forward));
		SmartDashboard.putNumber("Raw strafe", Robot.drivetrain.round(strafe));
		SmartDashboard.putNumber("Raw rotation", Robot.drivetrain.round(rotation));
//Adjust values to the curve
	forward = Robot.drivetrain.driveCurve(forward, false, slowMode);
	strafe = Robot.drivetrain.driveCurve(strafe, true, slowMode);
	rotation = Robot.drivetrain.driveCurve(rotation, false, slowMode);

	//Used for testing
	forward = 0;
	//strafe = 0;
	rotation = 0;
	//if the triggers are used, then over ride the regular strafe value
	
	
		//later might want to integrate this with the ADNS 9800
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
//Set these values and drive
		if(Math.abs(rotation)<0.1)	{ //if the driver doesn't want to rotate
			if(Robot.drivetrain.rotationLock.enable(false))	{ //enable PID, if successful run PID
				Robot.drivetrain.rotationLockDrive(strafe,forward);  //run with locked rotation
			}	else	{ //if soft disable is set, run regularly for a bit
				Robot.drivetrain.drive(strafe, forward, 0);  
				Robot.drivetrain.rotationLock.setSetpoint(Robot.drivetrain.rps.getAngle()); //save the current angle for seamless transfer
			}
		}	else	{
			Robot.drivetrain.rotationLock.disable(300); //disable the PID for the next 300+ milli-seconds
			Robot.drivetrain.drive(strafe, forward, rotation); //run regularly
			Robot.drivetrain.rotationLock.setSetpoint(Robot.drivetrain.rps.getAngle()); //save the current angle for seamless transfer
		}
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