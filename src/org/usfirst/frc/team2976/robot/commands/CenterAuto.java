
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
public class CenterAuto extends CommandGroup {
	public CenterAuto() {
		
	   	Robot.vision.saveAllPicture = false;	
	   	TraceLog.Instance.SetFolder("AutoTurn");
	   	//addSequential(new TimedDrive(900, -0.40, true)); //2 foot
	   	
	   	//Center Auto
	   	addSequential(new TimedDrive(600, -0.40, true)); //1 foot
	   	addSequential(new AutoMainDrive(AutoDriver.StartPosition.Center));
	   	addSequential(new TimedDrive(800, -0.40, true)); //2 foot
	   	addSequential(new TimedDrive(400, -0.30, true)); //2 foot
		
	   	/*
	   	//Left Auto
	   	AutoDriver.AutoData.Kp = 0.02;
	   	addSequential(new TimedDrive(1000, -0.40, true)); 
	   	addSequential(new OfCenterTurn(1500,0.5,false)); //turn to the left
	   	addSequential(new AutoMainDrive(AutoDriver.StartPosition.Center)); //regular auto
	   	addSequential(new TimedDrive(800, -0.40, true)); //2 foot final forward
	*/
		/*
	   	//right Auto
	   	addSequential(new TimedDrive(500, -0.40, true)); //2 foot
	   	addSequential(new OfCenterTurn(500,0.4,true)); //turn to the right
	   	addSequential(new AutoMainDrive(AutoDriver.StartPosition.Center)); //regular auto
	   	addSequential(new TimedDrive(800, -0.40, true)); //2 foot final forward
		*/
	   	
	}
	
}