package clientController;

import java.awt.image.BufferedImage;

import code2017.Target;
import edu.wpi.first.wpilibj.networktables.*;

public class Client
{
	private boolean initialized= false;
	private NetworkTable table=null;
	public String ipAddress = "";
	public void init()
	{
		NetworkTable.setClientMode();
		findIP();
		NetworkTable.setIPAddress(ipAddress);
		table = NetworkTable.getTable("Vision2017Team2976ID0119");
		initialized=true;
	}
	public boolean waitToProcess(long timeout)
	{
		int sleepTime=50;
		long t;
		for(long i=0;i<=(timeout/sleepTime);i++)
		{
			t=System.currentTimeMillis();
			if(table.getBoolean("Process", false))
			{
				table.putBoolean("Process", false);
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
	public void transmitTarget(Target target)
	{
		checkInit();
		if(target.nullTarget)
		{
			table.putBoolean("Fail", true);
		}
		else
		{
			table.putBoolean("Fail", false);
			table.putNumber("XPosition", target.x);
			table.putNumber("YPosition", target.y);
			table.putNumber("TargetAngle", target.angle);
			table.putNumber("TargetDistance", target.distance);
			table.putBoolean("Finished", true);
		}
	}
	public BufferedImage getImage(String key)
	{
		checkInit();
		BufferedImage image=ImageByteConverter.getBytesToImage(table.getRaw(key, null));
		return image;
	}
	//Module to find IP
	public String findIP()
	{
		//TO DO
		return ipAddress;
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
