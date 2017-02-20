package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class StrafeStraight extends Command {
	double power;
	double distance_x;

	public StrafeStraight(double power, double distance_x) {
    	requires(Robot.drivetrain);
    	this.power = power;
    	this.distance_x = distance_x;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.drivetrain.drive(0, -power, 0);  	
	 	SmartDashboard.putNumber("DistanceX", Robot.drivetrain.getDistanceY());
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return Math.abs(Robot.drivetrain.getDistanceY())>Math.abs(distance_x);
		//remember it needs to be getDistanceX
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}
