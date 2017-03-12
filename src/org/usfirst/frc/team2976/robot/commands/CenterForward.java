package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;
import Vision.Result;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;

/**
 * @author NeilHazra
 */
public class CenterForward extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	double tolerance;
	double baseSpeed = 0.5;
	boolean isClose = false;
	long prevTime = 0;
	
	long targetDistance = 36;
	
	public CenterForward(long targetDistanceToStop) {
		requires(Robot.drivetrain);
		targetDistance = targetDistanceToStop;
		
		Robot.traceLog.Log("CenterForward", "Created");
	}

	protected void initialize() {
		pidSource = new PIDSource() {
			public double getInput() {
				Result x = Robot.vision.LastGoodResult;
				if (x == null) {
					return 0;
				}
				double sideDistance = x.sideDistance();
				if (isClose && x.age() > 500) {
					sideDistance = 0;
				}
				return sideDistance;
			}
		};
		visionPID = new PIDMain(pidSource, 0, 100, 0.01, 0.000, 0);
		visionPID.setOutputLimits(-0.15, 0.15);
		visionPID.resetPID();
		visionPID.setSetpoint(0);
		baseSpeed = 0.5;
		
		Robot.traceLog.Log("CenterForward", "Initialized");
	}

	protected void execute() {
		double differential = visionPID.getOutput();
		if (isClose) {
			baseSpeed = 0.4;
		}
		double left = baseSpeed + differential;
		double right = baseSpeed - differential;
		
		Robot.drivetrain.m_drive.tankDrive(-left, right);
		
		double distance = 0;
		double sideDistance = 0;
		if (Robot.vision.LastGoodResult != null) {
			distance = Robot.vision.LastGoodResult.distance();
			sideDistance = Robot.vision.LastGoodResult.sideDistance();
		}
		String trace = "Input:"+(int)(visionPID.getInput()*100)/100.0;
		trace += ", Diff:"+ (int)(differential*100)/100.0;
		trace += ", Distance:"+(int)(distance*100)/100.0;
		trace += ", Side:"+(int)(sideDistance*100)/100.0;
		trace += ", Left:"+(int)(left*100)/100.0;
		trace += ", Right:"+(int)(right*100)/100.0;
		
		Robot.traceLog.Log("CenterForward", trace);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		Result x = Robot.vision.LastGoodResult;
		if (x != null) {
			if (x.distance() < 36 && isClose != true) {
				isClose = true;
				System.out.println("IsClose");
				prevTime = System.currentTimeMillis();
			}
		}
		boolean terminatingCase = (System.currentTimeMillis() - prevTime > 500) && isClose;
		if (terminatingCase) {
			Robot.traceLog.Log("CenterForward", "Finished");
		}
		return terminatingCase;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.drivetrain.m_drive.tankDrive(0, 0);
		Robot.traceLog.Log("CenterForward", "Ended");
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}