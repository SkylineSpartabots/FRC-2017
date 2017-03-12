package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.Result;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;

/**
 *
 */

public class StrafeAllign extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	
	public StrafeAllign() {
		requires(Robot.drivetrain);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.drivetrain.rotationLock.resetPID();
		Robot.drivetrain.rotationLock.setSetpoint(Robot.drivetrain.getHeading());
		pidSource = new PIDSource() {
			public double getInput() {
				Result x = Robot.vision.LastGoodResult;
				if (x == null) {
					return 0;
				}  
				double sideDistance = x.sideDistance();
				if (x.age() > 300) {
					sideDistance = 0;
				}
				
				return sideDistance;
			}
		};
		
		visionPID = new PIDMain(pidSource, 0, 100, 0.01, 0.000, 0);
		visionPID.setOutputLimits(-0.5, 0.5);
		visionPID.resetPID();
		visionPID.setSetpoint(Robot.drivetrain.getHeading());
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		double distance = 0;
		double sideDistance = 0;
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(), 0);
		if (Robot.vision.LastGoodResult != null) {
			distance = Robot.vision.LastGoodResult.distance();
			sideDistance = Robot.vision.LastGoodResult.sideDistance();
		}
		System.out.println("Strafe Allign: Time:" + System.currentTimeMillis() + "\tDiff:" + visionPID.getOutput() + "\tDistance:" + distance
				+ "\tSideDistance:" + sideDistance);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return Math.abs(visionPID.getError())<0.5;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.drivetrain.m_drive.tankDrive(0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}
