package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *@author NeilHazra
 */
public class LowerHopper extends Command {
    public LowerHopper() {
    	requires(Robot.hopper);
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.hopper.lowerHopper(-0.2);
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; //might need to return true all the time since this function is being called all the time
    }
    // Called once after isFinished returns true
    protected void end() {
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
