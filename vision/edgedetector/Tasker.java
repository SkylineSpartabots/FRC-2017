package edgedetector;

import java.awt.Rectangle;

public class Tasker implements Runnable
{
	public double[][] result=null;
	public double[][] input=null;
	public double[][] input2=null;
	private Assignment assignment;
	private Rectangle region;
	public Tasker(Assignment assignment, Rectangle region)
	{
		this.assignment=assignment;
		this.region=region;
	}
	@Override
	public void run()
	{
		switch(assignment)
		{
			case SMOOTHEN:
				result=Conv.conv(input, Conv.GAUSS, region);
				break;
			case XDERIV:
				result=Conv.conv(input, Conv.SOBEL_X, region, 0.0);
				break;
			case YDERIV:
				result=Conv.conv(input, Conv.SOBEL_Y, region, 0.0);
				break;
			case MAGNITUDE:
				result=Conv.magnitude(input, input2, region);
				break;
		}
		
	}

}
