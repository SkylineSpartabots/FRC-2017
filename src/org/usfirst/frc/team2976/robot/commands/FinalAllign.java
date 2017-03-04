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
public class FinalAllign extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	double tolerance;
	double baseSpeed = 0.5;
	boolean twentyInchReached = false;
	long prevTime = 0;
	public FinalAllign() {
		requires(Robot.drivetrain);
	}
	// Called just before this Command runs the first time
	protected void initialize() {
		pidSource = new PIDSource() {
			public double getInput() {
				Result x = Robot.vision.result;
				if (x == null) {
					return 0;
				}
				double sideDistance = x.sideDistance();
				SmartDashboard.putNumber("Side Distance Input", x.sideDistance());
				return sideDistance;
			}
		};
		visionPID = new PIDMain(pidSource, 0, 100, 0.01, 0.000, 0);
		visionPID.setOutputLimits(-0.15, 0.15);
		visionPID.resetPID();
		visionPID.setSetpoint(Robot.drivetrain.getHeading());
	}
	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		double differential = visionPID.getOutput();
		double left = baseSpeed + differential;
		double right = baseSpeed - differential;
		SmartDashboard.putNumber("outputVisionDifferential", differential);
		Robot.drivetrain.m_drive.tankDrive(-left, right);
	}
	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		Result x = Robot.vision.result;
		if(x != null)	{
			if(x.distance()<20 && twentyInchReached != true)	{
				twentyInchReached = true;
				System.out.println("Reached 20 Inches");
				prevTime = System.currentTimeMillis();
			}
		}	
		return (System.currentTimeMillis()-prevTime > 1000) && twentyInchReached;
	}
	// Called once after isFinished returns true
	protected void end() {
	}
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
	}
}