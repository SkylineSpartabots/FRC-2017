
package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main autonomous command to pickup and deliver the soda to the box.
 */
public class Autonomous extends CommandGroup {
	public Autonomous() {
		
		
		
		addSequential(new TimedDrive(550, -0.40, true)); //2 foot
		addSequential(new TimedDrive(300, -0.40, true)); //1 foot
		addSequential(new TimedDrive(300, -0.30, true)); //1/2 foot
		
		//addSequential(new TimedDrive(350, -0.50, false)); //right
		//addSequential(new TimedDrive(550, -0.50, false)); //right
		
		//addSequential(new TimedDrive(350, 0.50, false)); //left
		//addSequential(new TimedDrive(550, 0.50, false)); //left
		
		/*
		allign();
		addSequential(new TimedDrive(800,-0.50,true)); //drives straight
		*/
	}
	
	public void allign()	{
		double sideDistance = 0;
		
		
		if (Robot.vision.LastGoodResult != null) {
			sideDistance = Robot.vision.LastGoodResult.sideDistance();
		}

		while (sideDistance > 0) {
			TimedDrive timedDrive = new TimedDrive(100, -0.50, false);
			addSequential(timedDrive); // drives straight
			while(timedDrive.isRunning())	{
				Timer.delay(0.1);
			}
			if (Robot.vision.LastGoodResult != null) {
				sideDistance = Robot.vision.LastGoodResult.sideDistance();
			}
			
			if(Math.abs(sideDistance)<1) continue;
		}
		while (sideDistance < 0) {
			addSequential(new TimedDrive(100, 0.50, false)); // drives straight
			Timer.delay(0.5);

			if (Robot.vision.LastGoodResult != null) {
				sideDistance = Robot.vision.LastGoodResult.sideDistance();
			}
			if(Math.abs(sideDistance)<0.5) continue;
		}

	}
}