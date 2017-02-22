package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add PID drive with rotation lock
 * @author NeilHazra
 */

// Class works, don't touch

public class DriveWithJoystick extends Command {

	double prevTime;
	boolean isReversed = false;

	public DriveWithJoystick() {
		requires(Robot.drivetrain);
	}

	protected void initialize() {
		prevTime = System.currentTimeMillis();
		Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle());
	}

	protected void execute() {
		double forward;
		double strafe;
		double rotation;
		boolean slowMode = false;

		double x_relative = Robot.rps.getXDisplacementADNS();
		double y_relative = Robot.rps.getYDisplacementADNS();
		double heading = Robot.rps.getHeadingADNS();

		if (Robot.oi.driveStick.getRawButton(OI.Button.RBumper.getBtnNumber())
				&& (System.currentTimeMillis() - prevTime) > 500) {
			isReversed = !isReversed; // toggle
			prevTime = System.currentTimeMillis();
		}
		SmartDashboard.putBoolean("isReversed", isReversed);

		SmartDashboard.putBoolean("isPressed", Robot.oi.driveStick.getRawButton(OI.Button.RBumper.getBtnNumber()));
		if (isReversed) {
			strafe = -Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
			forward = -Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
			rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
			slowMode = false;
		} else {
			strafe = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
			forward = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
			rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
			slowMode = false;
		}

		forward = Robot.drivetrain.driveCurve(forward, false, slowMode);
		strafe = Robot.drivetrain.driveCurve(strafe, true, slowMode);
		rotation = Robot.drivetrain.driveCurve(rotation, false, slowMode);

		double RT = Robot.oi.driveStick.getRawAxis(OI.Axis.RTrigger.getAxisNumber());
		double LT = Robot.oi.driveStick.getRawAxis(OI.Axis.LTrigger.getAxisNumber());
		
		if (isReversed) {
			if (RT > 0.1) {
				strafe = -0.5 * RT;
			}
			if (LT > 0.1) {
				strafe = 0.5 * LT;
			}
		} else {
			if (RT > 0.1) {
				strafe = 0.5 * RT;
			}
			if (LT > 0.1) {
				strafe = -0.5 * LT;
			}
		}
		// Put adjusted values on SmartDashboard
		SmartDashboard.putNumber("Foward", Robot.drivetrain.round(forward));
		SmartDashboard.putNumber("Strafe", Robot.drivetrain.round(strafe));
		SmartDashboard.putNumber("Rotation", Robot.drivetrain.round(rotation));

		if (Math.abs(rotation) < 0.1) { // if the driver doesn't want to rotate
			if (Robot.drivetrain.rotationLock.enable(false)) {
				Robot.drivetrain.rotationLockDrive(strafe, forward);
			} else { // if soft disable is set, run regularly for a bit
				Robot.drivetrain.drive(strafe, forward, 0);
				Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle());
			}
		} else {
			Robot.drivetrain.rotationLock.disable(500); 
			Robot.drivetrain.drive(strafe, forward, rotation); // run regularly
			Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle());
		}
		// put the vision test stuff on the dashboard
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
