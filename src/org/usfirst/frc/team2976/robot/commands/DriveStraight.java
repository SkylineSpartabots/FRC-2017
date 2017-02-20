package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveStraight extends Command {
    double power;
    double distance_x;
    double distance_y;
    public DriveStraight(double power, double Xdistance, double Ydistance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drivetrain);
    	this.power = power;
    	this.distance_x = Xdistance;
    	this.distance_y = Ydistance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(distance_x == 0){
    		Robot.drivetrain.drive(0, -power, 0);  	
    	 	SmartDashboard.putNumber("Distance", Robot.drivetrain.getDistanceY());
    	    
    	}	else {
    		Robot.drivetrain.drive(-power, 0, 0);
    	}
    	//Robot.drivetrain.rotationLockDrive(0, -power);
     }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if(distance_x == 0){
    		return Math.abs(Robot.drivetrain.getDistanceY())>Math.abs(distance_y);
    	} else {
        	return Math.abs(Robot.drivetrain.getDistanceX())>Math.abs(distance_x);
        }
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
