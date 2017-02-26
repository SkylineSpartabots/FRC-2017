package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
/**
 *@author NeilHazra
 */
public class FinalAllign extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	double tolerance;

	double setpoint = -3;
	
	public FinalAllign() {
		requires(Robot.drivetrain);
		pidSource = new PIDSource() {
			public double getInput() {
				Robot.vision.compute();
				
				double x = setpoint;
				
				if (Robot.vision.result!=null && Robot.vision.result.age()<300) {
				//	x = Robot.vision.result.sideDistance();
				}
				
				SmartDashboard.putNumber("Side Distance Input", x);
				SmartDashboard.putNumber("outputVision", visionPID.getOutput());

				return x;
			}
		};
		SmartDashboard.putNumber("Vision Error", visionPID.getError());
		visionPID = new PIDMain(pidSource, setpoint, 150, 0.08, 0.0, 0);
		visionPID.setOutputLimits(-0.7, 0.7);

	}

	// Called just before this Command runs the first time
	protected void initialize() {
		visionPID.resetPID();
		visionPID.setSetpoint(Robot.drivetrain.getHeading());
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(),0.1);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;//Math.abs(Robot.vision.result.sideDistance()) < 2;
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}