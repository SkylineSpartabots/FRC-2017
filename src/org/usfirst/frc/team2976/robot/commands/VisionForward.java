package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.Result;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class VisionForward extends Command {

    public VisionForward() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.rotationLockDrive(0, 0.3);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	Result x = Robot.vision.LastGoodResult; 
    	if(x==null) return false;
    	System.out.println("Distance to Target" + x.distance());
    	if(x.age()>1500 || x.distance()<30) return true;
    	//return false;
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.rotationLockDrive(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
