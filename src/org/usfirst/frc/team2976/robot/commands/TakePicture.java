package org.usfirst.frc.team2976.robot.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ImageProcessor;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TakePicture extends Command {
	static SimpleDateFormat ft = new SimpleDateFormat ("HHmmss_MMdd");
	
    public TakePicture() {
    }

    // Called just before this Command runs the first time
    protected void initialize() {	
    	ImageProcessor.SavePicture(
    			TraceLog.Instance.GetLogFolder(), 
    			"Raw_manual_"+ft.format(new Date()), 
    			Robot.vision.TakePicture());
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
