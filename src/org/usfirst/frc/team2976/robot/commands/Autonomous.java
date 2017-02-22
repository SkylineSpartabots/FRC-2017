
package org.usfirst.frc.team2976.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The main autonomous command to pickup and deliver the
 * soda to the box.
 */
public class Autonomous extends CommandGroup {
    public Autonomous() {
	    addSequential(new FinalAlignment(0));
	    //addSequential(new TimedDrive(1000,0.5));
    }
}