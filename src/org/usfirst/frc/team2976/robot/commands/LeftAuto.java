
package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import AutoDriver.AutoMainDrive;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main autonomous command to pickup and deliver the soda to the box.
 */
public class LeftAuto extends CommandGroup {
	public LeftAuto() {
		
	   	Robot.vision.saveAllPicture = false;	
	   	TraceLog.Instance.SetFolder("AutoTurn");
		addSequential(new TimedDrive(1000, -0.40, true)); 
	   	addSequential(new OnCenterTurn(1500,0.5,true)); //turn to the left
	   	addSequential(new AutoMainDrive(AutoDriver.StartPosition.Center)); //regular auto
	   	addSequential(new TimedDrive(800, -0.40, true)); //2 foot final forward	

	}
	
}