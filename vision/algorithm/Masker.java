package algorithm;

import java.awt.Rectangle;

public class Masker implements Runnable
{
	//private int[][] xMask;
	//private int[][] yMask;
	private Operation op;
	private Rectangle range;
	private int[][] mask;
	private int div=1;
	public double[][] result;
	public int[][] corners;
	private double[][] map;
	private double[][][] derivs=new double[3][][];//ixx, iyy, ixy
	/*
	public Masker(int[][] xD, int[][] yD)
	{
		this.xMask=xD;
		this.yMask=yD;
	}
	*/
	public void setMap(double[][] map)
	{
		this.map=map;
	}
	public void setDerivs(double[][][] derivs)
	{
		this.derivs=derivs;
	}
	public void setRange(Rectangle r)
	{
		range=r;
	}
	public void setOperation(Operation op)
	{
		this.op=op;
	}
	public void setMask(int[][] mask)
	{
		this.mask=mask;
	}
	public void setDivisor(int div)
	{
		this.div=div;
	}
	@Override
	public void run()
	{
		switch(op)
		{
			case CONV:
				result=FeatureDetector.conv2(map, mask, range);
				if(div!=1)
				{
					result=FeatureDetector.divide2(result, div);
				}
				break;
			case CORNER:
				result=null;
				corners=FeatureDetector.corner(derivs[0], derivs[1], derivs[2], range);
				break;
			case MAGNITUDE:
				
		}
		
	}
}
