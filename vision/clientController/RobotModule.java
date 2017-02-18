package clientController;

import java.awt.image.BufferedImage;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

//This Code is intended to be on the main robot, DO NOT use on Raspberry PI
public class RobotModule
{
	//Note: image1 is light off, image2 is light on
	private boolean initialized=false;
	private NetworkTable table=null;
	public void init()
	{
		table=NetworkTable.getTable("Vision2017Team2976ID0119");
		
		initialized=true;
	}
	public double[] getTarget()
	{
		checkInit();
		double[] target= new double[5];
		target[0]=table.getNumber("XPosition", Double.NaN);
		target[1]=table.getNumber("YPosition", Double.NaN);
		target[2]=table.getNumber("TargetAngle", Double.NaN);
		target[3]=table.getNumber("TargetDistance", Double.NaN);
		if(table.getBoolean("Failed", false))
		{
			table.putBoolean("Failed", false);
			target[4]=1.0;
		}
		else
		{
			target[4]=0.0;
		}
		return target;
	}
	public void processImage(BufferedImage image1, BufferedImage image2)
	{
		transmitImage(image1, "image1");
		transmitImage(image2, "image2");
		table.putBoolean("Process", true);
	}
	public boolean waitToFinish(long timeout)
	{
		int sleepTime=50;
		long t;
		for(long i=0;i<=(timeout/sleepTime);i++)
		{
			t=System.currentTimeMillis();
			if(table.getBoolean("Finished", false))
			{
				table.putBoolean("Finished", false);
				return true;
			}
			try
			{
				Thread.sleep(sleepTime-(System.currentTimeMillis()-t));
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
	public void transmitImage(BufferedImage image)
	{
		checkInit();
		transmitImage(image, "Image");
	}
	public void transmitImage(BufferedImage image, String key)
	{
		checkInit();
		transmitBytes(ImageByteConverter.getImageToBytes(image), key);
	}
	public void trasnmitBytes(byte[] bytes)
	{
		checkInit();
		transmitBytes(bytes, "Bytes");
	}
	public void transmitBytes(byte[] bytes, String key)
	{
		checkInit();
		table.putRaw(key, bytes);
	}
	private void checkInit()
	{
		if(!initialized)
		{
			System.out.printf("WARNING: Not initialized. Automatically initializing...\n");
			init();
			System.out.printf("INFO: Initialized\n");
		}
	}
}
