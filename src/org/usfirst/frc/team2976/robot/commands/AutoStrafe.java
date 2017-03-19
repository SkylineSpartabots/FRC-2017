package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *	Very basic auto code based on time
 */
public class AutoStrafe extends Command {
	long initialTime;
	long duration;
	int timeoutTime = 500;
	boolean correctCenter = false;
	boolean isTimeout = false;
	boolean isPictureReady = false;
	boolean isStrafeDone = false;
	long strafeStartTime = 0;
	
	double timeToRun = 0;
	int todo = 0;
	
	String oldLogFolder = TraceLog.Instance.GetLogFolder();
	
    public AutoStrafe(int timeoutTime) {
    	requires(Robot.drivetrain);
    	this.timeoutTime = timeoutTime;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	initialTime = System.currentTimeMillis();
    	TraceLog.Instance.SetFolder("AutoStrafe");
    	Robot.vision.saveAllPicture = true;
    	Robot.vision.start();
    	todo = 0; //wait for picture
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	isTimeout = System.currentTimeMillis()-initialTime < 500;
    	
    	isPictureReady = false;
    	ProcessResult visionResult = Robot.vision.LastGoodResult;
    	if (null != visionResult && visionResult.m_pictureTimestamp>initialTime)
    	{
    		isPictureReady = true;
    	}
    	
    	if (todo == 0 && !isPictureReady){
    		// waiting for picture
    		// do nothing
    		return;
    	}
    	if (todo == 0 && isPictureReady){
    		double sideDistance = visionResult.m_sideDistance;
    		double motorPower = 0.5;
    		if (sideDistance > 0)
    		{
    			motorPower *= -1;
    		}
    		double distance = Math.abs(sideDistance);
    		
    		if (sideDistance>1.5){
    			timeToRun = 350;
    		}
    		
    		Robot.drivetrain.rotationLockDrive(0.5/*change*/, 0);
    		strafeStartTime = System.currentTimeMillis();
    		todo = 1; // wait for strafe
    		return;
    	}
    	
    	if (todo == 1)
    	{
    		isStrafeDone = System.currentTimeMillis()-strafeStartTime>timeToRun;
    		if (isStrafeDone)
    		{
    			//Stop motor
    			Robot.drivetrain.openLoopCartesianDrive(0, 0, 0);
    			todo = 2; // finish
    		}
    	}
		
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (isTimeout) {
    		return true;
    	}
    	if (todo == 2){
    		// finished
    		return true;
    	}
    	
    	return false;
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.openLoopCartesianDrive(0, 0, 0);
    	TraceLog.Instance.SetFolder(oldLogFolder);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.vision.start();
    }
}