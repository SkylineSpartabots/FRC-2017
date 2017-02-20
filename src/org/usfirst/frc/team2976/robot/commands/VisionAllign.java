package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import util.PIDMain;
import util.PIDSource;

/**
 *
 */
public class VisionAllign extends Command {
	
	PIDMain visionPID;
	PIDSource pidSource;
	double tolerance;
	
    public VisionAllign(double tolerance) {
    	requires(Robot.drivetrain);
    	this.tolerance = tolerance;
    			
    	pidSource = new PIDSource()	{
			public double getInput() {
				Robot.vision.compute();
				return Robot.vision.result.sideDistance();
			}	
    	};
    	
    	visionPID = new PIDMain(pidSource, 0, 100, 0.001, 0.001, 0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drivetrain.rotationLockDrive(visionPID.getOutput(), 0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return (pidSource.getInput()<tolerance);
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
