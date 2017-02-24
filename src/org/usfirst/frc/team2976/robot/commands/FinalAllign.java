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

	public FinalAllign() {
		requires(Robot.drivetrain);
		pidSource = new PIDSource() {
			public double getInput() {
				Robot.vision.compute();
				double x = Robot.vision.result.sideDistance();
				if (x == -10000) {
					x = 0;
				}
				return x;
			}
		};
		visionPID = new PIDMain(pidSource, 0, 150, 0.22, 0.0, 0);
		visionPID.setOutputLimits(-0.7, 0.7);

	}

	// Called just before this Command runs the first time
	protected void initialize() {
		visionPID.resetPID();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(),0);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return Robot.vision.result.sideDistance() < 2;
	}

	// Called once after isFinished returns true
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}