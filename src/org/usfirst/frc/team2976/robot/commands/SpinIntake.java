package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class SpinIntake extends Command {

    public SpinIntake() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.intakeroller);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	boolean in = false;
    	boolean out = false;
    	in = Robot.oi.secondStick.getRawButton(OI.Button.RBumper.getBtnNumber());
    	out = Robot.oi.secondStick.getRawButton(OI.Button.LBumper.getBtnNumber());
    	Robot.intakeroller.setRoller(in, out);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
