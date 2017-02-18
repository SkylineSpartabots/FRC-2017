package org.usfirst.frc.team2976.robot.subsystems;

import java.awt.image.BufferedImage;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Vision extends Subsystem {
	UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
	// Set the resolution
	

	// Get a CvSink. This will capture Mats from the camera
	VideoSink image1 = CameraServer.getInstance().getServer();

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	public void setImage(BufferedImage image1, BufferedImage image2)
	{
	//	this.image1=image1;
		//this.image2=image2;
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

