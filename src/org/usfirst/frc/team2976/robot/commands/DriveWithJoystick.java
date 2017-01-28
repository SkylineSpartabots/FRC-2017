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

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		double x = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
		double ly = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
		double ry = Robot.oi.driveStick.getRawAxis(OI.Axis.RY.getAxisNumber());
		double rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());

		// SmartDashboard.putNumber("DisplacementX",Robot.drivetrain.rps.ahrs.getDisplacementX());
		// SmartDashboard.putNumber("DisplacementY",Robot.drivetrain.rps.ahrs.getDisplacementY());
		// SmartDashboard.putNumber("DisplacementZ",Robot.drivetrain.rps.ahrs.getDisplacementZ());

		// Robot.drivetrain.rotationLockDrive(x, y);

		if (Robot.oi.driveStick.getRawAxis(Axis.RTrigger.getAxisNumber()) > 0.1) {
			Robot.drivetrain.rotationLockDrive(Robot.oi.driveStick.getRawAxis(Axis.RTrigger.getAxisNumber()), 0);
		} else if (Robot.oi.driveStick.getRawAxis(Axis.LTrigger.getAxisNumber()) > 0.1) {
			Robot.drivetrain.rotationLockDrive(Robot.oi.driveStick.getRawAxis(Axis.LTrigger.getAxisNumber()), 0);
		} else {
			Robot.drivetrain.m_drive.tankDrive(ly, ry);
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