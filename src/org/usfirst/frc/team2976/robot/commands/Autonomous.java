
package org.usfirst.frc.team2976.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * The main autonomous command to pickup and deliver the
 * soda to the box.
 */
public class Autonomous extends CommandGroup {
    public Autonomous() {
    	addSequential (new TimedDrive(1700, -0.5));
    	
    //	addSequential(new AutoDrive(100));
    }
}
