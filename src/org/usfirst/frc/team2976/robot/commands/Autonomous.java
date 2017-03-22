
package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main autonomous command to pickup and deliver the soda to the box.
 */
public class Autonomous extends CommandGroup {
	public Autonomous() {
		
	   	Robot.vision.saveAllPicture = true;
	   	TraceLog.Instance.SetFolder("AutoTurn");
	   	
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new AutoTurn(1000));
	addSequential(new TimedDrive(300, -0.40, true)); //2 foot
	
		/*
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		addSequential(new TimedDrive(300, -0.40, true)); //2 foot
		addSequential(new AutoTurn(1000));
		*/
		//addSequential(new TimedDrive(550, -0.40, true)); //2 foot
		//addSequential(new TimedDrive(300, -0.40, true)); //1 foot
		//addSequential(new TimedDrive(300, -0.30, true)); //1/2 foot
		
		//addSequential(new TimedDrive(350, -0.50, false)); //right
		//addSequential(new TimedDrive(550, -0.50, false)); //right
		
		//addSequential(new TimedDrive(350, 0.50, false)); //left
		//addSequential(new TimedDrive(550, 0.50, false)); //left
		
		/*
		allign();
		addSequential(new TimedDrive(800,-0.50,true)); //drives straight
		*/
	}
	
}