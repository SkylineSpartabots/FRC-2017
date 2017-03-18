package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;
import Vision.Result;
import edu.wpi.first.wpilibj.command.Command;
import util.TraceLog;

public class CenterForward extends Command {
	double tolerance;
	double baseSpeed = 0.5;
	boolean isClose = false;
	long prevTime = 0;
	double kp = 0.01;
	long targetDistance = 36;
	
	public CenterForward(long targetDistanceToStop) {
		requires(Robot.drivetrain);
		targetDistance = targetDistanceToStop;		
		Robot.traceLog.Log("CenterForward", "Created");
	}

	protected void initialize() {
		baseSpeed = 0.5;
		Robot.traceLog.Log("CenterForward", "Initialized");
		Robot.vision.saveAllPicture=true;
	}

	protected void execute() {
		if (isClose) {
			baseSpeed = 0.4;
		}
		double distance = 0;
		double sideDistance = 0;
		
		if (Robot.vision.LastGoodResult != null)
		{
			sideDistance = Robot.vision.LastGoodResult.sideDistance();
			distance = Robot.vision.LastGoodResult.distance();
		}
		
		double differential = -1 * kp * sideDistance;
		double left = baseSpeed + differential;
		double right = baseSpeed - differential;
		
		Robot.drivetrain.m_drive.tankDrive(-left, right);
		
		String trace = "Execute--";
		trace += "diff:" + TraceLog.Round(differential, 1000);
		trace += ", distance:" + TraceLog.Round(distance, 100);
		trace += ", side:" + TraceLog.Round(sideDistance, 100);
		trace += ", Left:"+TraceLog.Round(left, 100);
		trace += ", Right:"+TraceLog.Round(right, 100);
		
		Robot.traceLog.Log("CenterForward", trace);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		Result x = Robot.vision.LastGoodResult;
		if (x != null) {
			if (x.distance() < targetDistance && isClose != true) {
				isClose = true;
				Robot.traceLog.Log("CenterForward", "IsClose = true");
				prevTime = System.currentTimeMillis();
			}
		}
		
		long afterClose = 0;
		if (isClose)
		{
			afterClose = System.currentTimeMillis() - prevTime;
			Robot.traceLog.Log("CenterForward", "IsClose:" + afterClose);
		}
		if (afterClose > 500) {
			Robot.traceLog.Log("CenterForward", "Finished");
			return true;
		}
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.drivetrain.m_drive.tankDrive(0, 0);
		Robot.traceLog.Log("CenterForward", "Ended");
		Robot.vision.saveAllPicture=false;
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}