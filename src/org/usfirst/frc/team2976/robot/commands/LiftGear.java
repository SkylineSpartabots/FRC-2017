package org.usfirst.frc.team2976.robot.commands;
import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

/**
 *@author NeilHazra
 */
public class LiftGear extends Command {
	double m_power = 0.3;
    public LiftGear() {
    	requires(Robot.gear);
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.gear.setGearPivot(m_power);
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !Robot.gear.getLimitSwitch(); //Limit switch is reversed
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.gear.setGearPivot(0);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
