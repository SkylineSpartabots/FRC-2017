
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
public class RightAuto extends CommandGroup {
	public RightAuto() {
		
	   	Robot.vision.saveAllPicture = false;	
	   	TraceLog.Instance.SetFolder("AutoTurn");
	   	AutoDriver.AutoData.Kp = 0.02;
	   	//addSequential(new TimedDrive(1000, -0.40, true)); 
	   	addSequential(new TimedDrive(1560, -0.40, true)); 
	   	//addSequential(new OfCenterTurn(1500,0.5,false)); //turn to the left
	   	addSequential(new OnCenterTurn(700,0.7,false)); //turn to the left
	   	addSequential(new AutoMainDrive(AutoDriver.StartPosition.Center)); //reg	ular auto
	   	addSequential(new TimedDrive(800, -0.40, true)); //2 foot final forward	
	   	addSequential(new TimedDrive(400, -0.30, true)); //2 foot final forward	
	}
}