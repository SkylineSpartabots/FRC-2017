package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
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
    	Robot.drivetrain.drive(0, -power, 0);
       	//Robot.driveTrain.driveStraight(Robot.driveTrain.getGyro(), power);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return System.currentTimeMillis()-initialTime > duration;
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.drive(0,0,0);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}