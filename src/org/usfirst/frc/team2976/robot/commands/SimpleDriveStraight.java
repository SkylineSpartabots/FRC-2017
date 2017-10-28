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

public class SimpleDriveStraight extends Command {
	long m_duration;
	long initialTime;
	double power;
	
	public SimpleDriveStraight(long duration) {
		m_duration = duration;
		requires(Robot.drivetrain);
		power = 0.5;
	}

	protected void initialize() {
		initialTime = System.currentTimeMillis();
	}

	protected void execute() {
		Robot.drivetrain.openLoopCartesianDrive(0, power, 0);

	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return System.currentTimeMillis() - initialTime > m_duration;
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