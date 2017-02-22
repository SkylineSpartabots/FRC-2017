package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;

/**
 *
 */
public class FinalAllignment extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	double inchesToTarget;
	double tolerance;
	double prevSideDistance;
	
	public FinalAllignment(double tolerance) {
		requires(Robot.drivetrain);
		this.tolerance = tolerance;

		pidSource = new PIDSource() {
			public double getInput() {
				Robot.vision.compute();
				double x = Robot.vision.result.sideDistance();
				
				if(x==-10000){
					x=0;
					//x= prevSideDistance;
				}	else{
				//	prevSideDistance = x;
				}
				
				SmartDashboard.putNumber("Side Distance", x);
				SmartDashboard.putNumber("outputVision", visionPID.getOutput());
				return x;	
			}
		};
		visionPID = new PIDMain(pidSource, 0, 100, 0.22, 0.0, 0);

		//visionPID.setOutputLimits(0.1, 0.7);
	}

	// Called just before this Command runs the first time
	protected void initialize() {

	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		// Robot.drivetrain.rotationLockDrive(0.4, 0);
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(), 0); // go
																		// forward
												
		System.out.println(visionPID.getInput() + " " + visionPID.getOutput());
		// slowly
		// SmartDashboard.putNumber("Forward Distance",
		// Robot.vision.result.distance());
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;//Math.abs(pidSource.getInput())<1;
	}
	// Called once after isFinished returns true
	protected void end() {
		SmartDashboard.putBoolean("Terminated", true);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}
