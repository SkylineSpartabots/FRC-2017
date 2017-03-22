package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *	Very basic auto code based on time
 */
public class AutoTurn extends Command {
	long initialTime;
	long duration;
	
	
	int timeoutTime = 500;
	
	boolean correctCenter = false;
	boolean isTimeout = false;
	boolean isPictureReady = false;
	boolean isTurnDone = false;
	long turnStartTime = 0;
	
	double timeToRun = 0;
	final static double power = 0.6;
	final static double diffPercent = 0.7;
	
	int todo = 0;
	

    public AutoTurn(int inputTimeoutTime) {
    	requires(Robot.drivetrain);
    	this.timeoutTime = inputTimeoutTime;

    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	initialTime = System.currentTimeMillis();

    	TraceLog.Log("AutoCenter", "Initialized");
    	
    	todo = 0; //wait for picture
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	isTimeout = System.currentTimeMillis()-initialTime > timeoutTime;
    	
    	isPictureReady = false;
    	ProcessResult visionResult = Robot.vision.LastGoodResult;
    	if (null != visionResult && visionResult.m_pictureTimestamp>initialTime)
    	{
    		isPictureReady = true;
    	}
    	
    	TraceLog.Log("AutoCenter", "Execute isTimeout="+isTimeout+",todo="+todo+",isPictureReady="+isPictureReady);
    	
    	if (todo == 0 && !isPictureReady){
    		// waiting for picture
    		// do nothing
    		return;
    	}
    	if (todo == 0 && isPictureReady){
    		double sideDistance = visionResult.m_sideDistance;
    		double leftMotorPower = 0;
    		double rightMotorPower = 0;
    		if (sideDistance > 0)
    		{
    			leftMotorPower = diffPercent * power; 
    			rightMotorPower = power;
    		}	else	{
    			leftMotorPower = power;
    			rightMotorPower = diffPercent * power;
    		}
    		
    		double angle = Math.atan(Math.abs(sideDistance)/visionResult.m_distance);
    		angle *= 180/Math.PI;
    		turnStartTime = System.currentTimeMillis();
    		
    		TraceLog.Log("AutoCenter", "Side="+visionResult.m_sideDistance+",dist="+visionResult.m_distance+", angle="+angle);
    		if (angle>2){
    			timeToRun = 200+angle*15;
    		}
    		else 
    		{
    			leftMotorPower = power;
    			rightMotorPower =  power;
    			timeToRun = 200;
            	TraceLog.Log("AutoCenter", "No need to turn. Skip to finish");
    		}
   
    		Robot.drivetrain.tankDrive(leftMotorPower, rightMotorPower);
    		todo = 1; // wait for turn
        	TraceLog.Log("AutoCenter", "Start to turn, timeToRun="+timeToRun+",leftMotorPower="+leftMotorPower+",rightMotorPower="+rightMotorPower);

    		return;
    	}
    	
    	if (todo == 1)
    	{
    		isTurnDone = System.currentTimeMillis()-turnStartTime>timeToRun;
    		TraceLog.Log("AutoCenter", "WaitForTurn, isTurnDone = "+isTurnDone);

    		if (isTurnDone)
    		{
    			//Stop motor
    			Robot.drivetrain.tankDrive(0, 0);
    			todo = 2; // finish
    		}
    	}
		
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (isTimeout) {
    		TraceLog.Log("AutoCenter", "Finished for timeout");
    		return true;
    	}
    	if (todo == 2){
    		TraceLog.Log("AutoCenter", "Finished to turn");
    		return true;
    	}
    	
    	return false;
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.drivetrain.tankDrive(0, 0);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {    	
    }
}