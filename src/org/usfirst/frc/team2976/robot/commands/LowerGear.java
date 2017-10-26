package org.usfirst.frc.team2976.robot.commands;
import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *@author NeilHazra
 */
public class LowerGear extends Command {
	double m_power = 0.2;
	long initialTime;
	long duration = 2000;
    public LowerGear() {
    	requires(Robot.gear);
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    	initialTime = System.currentTimeMillis();
    	Robot.gear.setGearPivot(-m_power);
    	
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.gear.setGearPivot(-m_power);
    	SmartDashboard.putNumber("gear pivot", m_power);  	
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return System.currentTimeMillis() - initialTime > duration;
    	
    }
    // Called once after isFinished returns true
    protected void end() {
    	Robot.gear.setGearPivot(0);
    	SmartDashboard.putNumber("gear pivot", m_power);
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
