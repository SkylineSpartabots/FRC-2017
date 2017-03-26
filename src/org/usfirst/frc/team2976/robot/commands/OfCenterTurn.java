package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class OfCenterTurn extends Command {
	long initialTime;
	long duration;
	double power;
	boolean direction;
	/**
	 * @param time_ms
	 *            the time to drive
	 * @param power
	 *            the power to drive, sign can change direction
	 * @param direction
	 *            this is either left (false) or right(true)
	 */
	public OfCenterTurn(int time_ms, double power, boolean direction) {
		requires(Robot.drivetrain);
		duration = time_ms;
		this.power = power;
		this.direction = direction;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		initialTime = System.currentTimeMillis();
		Robot.drivetrain.tankDrive(0, 0);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		if (direction) {
			Robot.drivetrain.tankDrive(-power, power);
		} else {
			Robot.drivetrain.tankDrive(power, -power);
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return System.currentTimeMillis() - initialTime > duration;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.drivetrain.openLoopCartesianDrive(0, 0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}
