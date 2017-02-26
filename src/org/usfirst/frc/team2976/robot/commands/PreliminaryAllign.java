package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
/**
 * @author NeilHazra
 */
public class PreliminaryAllign extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	
	public PreliminaryAllign() {
		requires(Robot.drivetrain);

		pidSource = new PIDSource() {
			public double getInput() {
				Robot.vision.compute();
				double x = Robot.vision.result.sideDistance();
				
				if (x == -10000) {
					x = 0;
				}

				SmartDashboard.putNumber("Side Distance", x);
				SmartDashboard.putNumber("outputVision", visionPID.getOutput());

				return x;
			}
		};
		SmartDashboard.putBoolean("is running", true);
		visionPID = new PIDMain(pidSource, 0, 150, 0.22, 0.0, 0);
		visionPID.setOutputLimits(-0.7, 0.7);
	}
	// Called just before this Command runs the first time
	protected void initialize() {
		visionPID.resetPID();
	}
	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(),
				constrain(1 - Math.abs(visionPID.getOutput()), 0, 0.5));

		System.out.println("Strafe: " + visionPID.getOutput() + " Forward: " + constrain(1 - Math.abs(visionPID.getOutput()), 0, 0.5));
		System.out.println("Vision PID:" + " " + visionPID.getInput() + ", " + visionPID.getOutput());
	}
	private double constrain(double x, double max, double min) {
		if (x > max) {
			x = max;
		} else if (x < min) {
			x = min;
		}
		return x;
	}
	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return Robot.rps.getUltrasonicDistanceInInches() < 24;
	}
	// Called once after isFinished returns true
	protected void end() {
	}
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}
