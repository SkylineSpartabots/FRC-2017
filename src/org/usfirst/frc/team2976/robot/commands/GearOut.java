package org.usfirst.frc.team2976.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class GearOut extends CommandGroup {

    public GearOut() {
    	addParallel(new LowerGear());
    	addParallel(new IntakeGear(0.6));
    	
    }
}
