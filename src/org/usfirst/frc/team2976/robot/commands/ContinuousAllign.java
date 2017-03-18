package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import util.PIDMain;
import util.PIDSource;
import util.TraceLog;
/**
 * @author NeilHazra
 */
public class ContinuousAllign extends Command {
	PIDMain visionPID;
	PIDSource pidSource;
	
	public ContinuousAllign() {
		requires(Robot.drivetrain);
		Robot.vision.saveAllPicture=true;
		
		pidSource = new PIDSource() {
			public double getInput() {
				double sideDistance = 0;
				if (Robot.vision.LastGoodResult != null && Robot.vision.LastGoodResult.age()<30)
				{
					visionPID.enable(true);
					sideDistance = Robot.vision.LastGoodResult.sideDistance();
				}	else	{
					visionPID.disable(0);
				}
				return sideDistance;
			}
		};
		visionPID = new PIDMain(pidSource, 0, 50, 0.22, 0.0, 0);
		visionPID.setOutputLimits(-0.5, 0.5);
	}
	protected void initialize() {
		visionPID.resetPID();
	}
	protected void execute() {
		Robot.drivetrain.rotationLockDrive(visionPID.getOutput(), 0.3);
		String trace = "Execute--";
		trace += "PIDOutput:" + TraceLog.Round(visionPID.getOutput(), 1000);
		trace += ", PIDInput:" + TraceLog.Round(pidSource.getInput(), 1000);
		Robot.traceLog.Log("ContinuousAllign", trace);
	}
	
	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;//Robot.drivetrain.getDistanceInches() < 10;
	}
	protected void end() {
	}
	protected void interrupted() {
	}
}