/*
package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;

public class StrafeStraight extends Command {
	double tolerance;
    double distance_x;
    PIDSource distancePIDSource;
    PIDMain distancePID;
    
    public StrafeStraight(double distance_x, double tolerance) {
    	requires(Robot.drivetrain);
    	
    	distancePIDSource = new PIDSource()	{
    		public double getInput()	{
    			return Robot.drivetrain.getDistanceX();
    		}
    	};
    	distancePID = new PIDMain(distancePIDSource, 0, 100, 0.01, 0.0001, 0);
    	this.tolerance = tolerance;
    	this.distance_x = distance_x;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drivetrain.YLock.setSetpoint(Robot.drivetrain.YEncoder.get());
    	Robot.drivetrain.rotationLock.setSetpoint(Robot.rps.getAngle());
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {  	
    		Robot.drivetrain.yLockDrive(distancePID.getOutput());
    	 	SmartDashboard.putNumber("Distance", Robot.drivetrain.getDistanceY());
     }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    		return Math.abs(distancePID.getError())<tolerance;
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.drive(0, 0, 0);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
*/