package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Very basic auto code based on time
 */
public class TimedDrive extends Command {
	long initialTime;
	long duration;
	double power;
    public TimedDrive(int time_ms, double power) {
    	requires(Robot.drivetrain);
    	duration = time_ms;
    	this.power = power;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	initialTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drivetrain.rotationLockDrive(0, -power);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return System.currentTimeMillis()-initialTime > duration;
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