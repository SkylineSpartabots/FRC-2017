package org.usfirst.frc.team2976.robot.commands;
import org.usfirst.frc.team2976.robot.OI;
import org.usfirst.frc.team2976.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
/**
 *
 */
public class DriveWithJoystick extends Command {
	public DriveWithJoystick() {
    	requires(Robot.drivetrain);
    }
    // Called just before this Command runs the first time
    protected void initialize() {
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double x = Robot.oi.driveStick.getRawAxis(OI.Axis.LX.getAxisNumber());
    	double y = Robot.oi.driveStick.getRawAxis(OI.Axis.LY.getAxisNumber());
    	double rotation = Robot.oi.driveStick.getRawAxis(OI.Axis.RX.getAxisNumber());
    	Robot.drivetrain.drive(x, y, rotation);
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