package org.usfirst.frc.team2976.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class GearIn extends CommandGroup {

    public GearIn() {
    	addSequential(new IntakeGear(-0.6));
    	addSequential(new LiftGear());
    }
}
