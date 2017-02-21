package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;

/**
 *
 */
public class FinalAllignment extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	double inchesToTarget;
	double tolerance;
	
	
    public FinalAllignment(double tolerance) {
    	requires(Robot.drivetrain);
    	this.tolerance = tolerance;
    			
    	pidSource = new PIDSource()	{
			public double getInput() {
				Robot.vision.compute();
				double x = Robot.vision.result.sideDistance();
				SmartDashboard.putNumber("Side Distance", x);
				assert x!=10; //this will break the code
				return x;
			}	
    	};
    	visionPID = new PIDMain(pidSource, 0, 100, 0.1, 0.000, 0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drivetrain.rotationLockDrive(visionPID.getOutput(), 0);
		SmartDashboard.putNumber("Forward Distance", Robot.vision.result.distance());
    	SmartDashboard.putNumber("outputVision", visionPID.getOutput());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return (pidSource.getInput() < tolerance);
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
