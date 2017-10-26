package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SpinIntakeTimed extends Command {
	private double m_power;
	long initialTime;
	long duration = 3000;
    public SpinIntakeTimed(double m_power) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.intakeroller);
    	this.m_power = m_power;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	initialTime = System.currentTimeMillis();
    	Robot.intakeroller.setRoller(m_power);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.intakeroller.setRoller(m_power);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return System.currentTimeMillis() - initialTime > duration;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.intakeroller.setRoller(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
