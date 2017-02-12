
package org.usfirst.frc.team2976.robot.commands;

import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;

/**
 *
 */
public class Shoot extends Command {

	 public Shoot() {
	    	requires(Robot.revCounter);
	    }

	    protected void initialize() {
	    }
	    // Called repeatedly when this Command is scheduled to run
	    protected void execute() {
	    	SmartDashboard.putNumber("RightMotorCount", Robot.revCounter.getCount());
	    	SmartDashboard.putNumber("RightMotorVelocity", Robot.revCounter.getVelocity());
	    	
	    	//Robot.revCounter.setLeftMotor(Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber()));
	    	
	    	Robot.revCounter.setWheelPID(PIDMain.map(Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber()), 0, 1, 0, 3200));    	
	    	SmartDashboard.putNumber("Input",Robot.revCounter.wheelShooterPID.getInput());
	    	SmartDashboard.putNumber("Output",Robot.revCounter.wheelShooterPID.getOutput());
	    	SmartDashboard.putNumber("Error",Robot.revCounter.wheelShooterPID.getError());
	    	SmartDashboard.putNumber("Setpoint	",Robot.revCounter.wheelShooterPID.getSetpoint());

	    }

	    // Make this return true when this Command no longer needs to run execute()
	    protected boolean isFinished() {
	        return Robot.oi.driveStick.getRawButton(OI.Button.B.getBtnNumber());
	    }
	    // Called once after isFinished returns true
	    protected void end() {
	    
	    }
	    // Called when another command which requires one or more of the same
	    // subsystems is scheduled to run
	    protected void interrupted() {
	    }
}

