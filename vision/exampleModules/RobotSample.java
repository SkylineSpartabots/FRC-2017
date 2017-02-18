package exampleModules;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import clientController.RobotModule;

public class RobotSample
{
	RobotModule rm;
	Webcam camera;
	public RobotSample()
	{
		RobotModule rm=new RobotModule();
		rm.init();
		camera=Webcam.getDefault();
		camera.open();
	}
	//Note: image1 is light off, image2 is light on
	public void moveToTarget()
	{
		double distance;
		double x;
		double y;
		double angle;
		//Magic
		//You can reverse the order of taking the images, but make sure to send with image2 as light on
		//If only one image is used, send image 1 as null
		while(true)
		{
			ledLight(false);
			BufferedImage image1=getImage();
			ledLight(true);
			BufferedImage image2=getImage();
			rm.processImage(image1, image2);
			while(true)
			{
				boolean finished=rm.waitToFinish(1000);//Should take less than 0.5 seconds, average 0.33
				if(finished)
				{
					break;
				}
			}
			double[] target=rm.getTarget();//Get the target, see format below
			if(target[4]==0.0)//Checks to make sure no failure
			{
				x=target[0];
				y=target[1];
				angle=target[2];
				distance=target[3];//Ask me if this is in inches or feet
				break;
			}
		}
		//End Magic
		//Move Code
		//See above for your variables.
		
	}
	public BufferedImage getImage()
	{
		return camera.getImage();
	}
	public void ledLight(boolean on)
	{
		//Turns green LED on or off
		//TODO
	}
}
