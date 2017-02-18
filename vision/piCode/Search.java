package piCode;
import visionCore.Vision;

import com.github.sarxos.webcam.Webcam;

import edu.wpi.first.wpilibj.networktables.NetworkTable;


public class Search 
{
	NetworkTable table=null;
	private boolean begin=false;
	private boolean terminate=false;
	Vision vision=new Vision();
	private Webcam webcam=null;
	public boolean processCommand()
	{
		if(begin)
		{
			double[] target=vision.process(webcam.getImage());
			//NOTE: Commented out code below is accurate to significance of variables
			/*
			table.putNumber("x", target[0]);
			table.putNumber("y", target[1]);
			table.putNumber("distance", target[2]);
			table.putNumber("targetAngle", target[3]);
			*/
			table.putNumberArray("target", target);
			table.putBoolean("done", true);
		}
		if(terminate)
		{
			webcam.close();
			return true;
		}
		return false;
	}
	public void initializeTable()
	{
		webcam=Webcam.getDefault();
		webcam.open();
		table=NetworkTable.getTable("Vision2976");
		table.putNumber("viewAngle", vision.viewAngle);
	}
	public void getNextCommand()
	{
		begin=table.getBoolean("begin", false);
		terminate=table.getBoolean("terminate",false);
	}
}
