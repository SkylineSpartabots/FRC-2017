
package org.usfirst.frc.team2976.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * The main autonomous command to pickup and deliver the
 * soda to the box.
 */
public class Autonomous extends CommandGroup {
    public Autonomous() {
    	addSequential (new DriveStraight(-0.5, 0,1));
    	Timer.delay(1);
    	addSequential (new DriveStraight(-0.5, 0,1));
    	
    	//addSequential (new DriveStraight(-0.5, 0,1));
    	
    	//addSequential (new DriveStraight(-0.5, 1,0));
    	
    	
    //	addSequential(new AutoDrive(100));
    }
}
