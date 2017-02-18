package algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class RectangleController implements Runnable
{
	private RectangleController[] rc;
	private Thread[] t;
	public double[][] map=null;
	public boolean[][] corners=null;
	private int[][] result=null;
	private double[][] ix=null;
	private double[][] iy=null;
	public double[][] ixx=null;
	public double[][] iyy=null;
	public double[][] ixy=null;
	public int[][] l2=null;
	boolean done=false;
	private Operation op;
	private double[][] gKernel=null;
	private int start=0;
	private int interval=0;
	public ArrayList<Point> interest=null;
	public boolean cornersFound=false;
	public RectangleController(int threads)
	{
		rc=new RectangleController[threads];
		t=new Thread[threads];
		for(int i=0;i<rc.length;i++)
		{
			rc[i]=new RectangleController();
			t[i]=new Thread(rc[i]);
		}
	}
	public ArrayList<Point> process(double[][] input)
	{
		System.out.println("Processing");
		this.map=RectangleController.copyOf(input);
		int threads=rc.length;
		int interval=map.length/threads;
		for(int i=0;i<rc.length;i++)
		{
			rc[i].setCommand(copyOf(this.map), i*interval, interval, Operation.DERIV);
			t[i].start();
		}
		done=false;
		while(!done)
		{
			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done=true;
			for(int i=0;i<rc.length;i++)
			{
				if(!rc[i].done)
				{
					done=false;
					break;
				}
			}
		}
		ixx=new double[map.length][map[0].length];
		iyy=new double[map.length][map[0].length];
		ixy=new double[map.length][map[0].length];
		for(int i=0;i<rc.length;i++)
		{
			System.arraycopy(rc[i].ixx, 0, ixx, (i*interval), interval);
			System.arraycopy(rc[i].iyy, 0, iyy, (i*interval), interval);
			System.arraycopy(rc[i].ixy, 0, ixy, (i*interval), interval);
		}
		for(int i=0;i<rc.length;i++)
		{
			rc[i].ixx=this.ixx;
			rc[i].iyy=this.iyy;
			rc[i].ixy=this.ixy;
			rc[i].setCommand(Operation.GAUSS);
			t[i]=new Thread(rc[i]);
			t[i].start();
		}
		done=false;
		while(!done)
		{
			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done=true;
			for(int i=0;i<rc.length;i++)
			{
				if(!rc[i].done)
				{
					done=false;
					break;
				}
			}
		}
		result=new int[map.length][map[0].length];
		for(int i=0;i<rc.length;i++)
		{
			System.arraycopy(rc[i].result, 0, result, (i*interval), interval);
		}
		l2=new int[map.length][map[0].length];
		for(int i=0;i<rc.length;i++)
		{
			System.arraycopy(rc[i].l2, 0, l2, (i*interval), interval);
		}
		return RectangleController.filterCorners(result);
	}
	public void setCommand(double[][] map, int start, int interval, Operation op)
	{
		this.op=op;
		this.start=start;
		this.interval=interval;
		this.map=map;
		done=false;
	}
	public void setCommand(Operation op)
	{
		this.op=op;
		done=false;
	}
	public RectangleController()
	{
		rc=null;
		t=null;
	}
	@Override
	public void run()
	{
		if(map!=null)
		{
			switch(op)
			{
				case DERIV:
					findDerivatives();
					break;
				case GAUSS:
					findCorners();
					break;
				case PROCESS:
					cornersFound=false;
					interest=process(map);
					cornersFound=true;
					break;
			}
			
			done=true;
		}
	}
	private void findCorners()
	{
		generateGauss(1,5);
		result=new int[interval][map[0].length];
		l2=new int[interval][map[0].length];
		for (int x = 0; x < map[0].length; x++)
		{
			for (int y = start; y < start+interval; y++)
			{
				//Apply a guassian kernel at our current pixel
				double ixx = conv(this.ixx, gKernel, x, y);
				double iyy = conv(this.iyy, gKernel, x, y);
				double ixy = conv(this.ixy, gKernel, x, y);
				double tr = ixx + iyy;
				double det = (ixx * iyy) - (ixy * ixy);
				double value = det - (0.04f * (tr * tr));
				final double threshold = 1000;
				if (value > threshold)
				{
					result[y-start][x] = (int) value;
				}
				value = (det + (0.04f * (tr * tr)));
				if(value > threshold)
				{
					l2[y-start][x]=(int) value;
				}
			}
		}
	}
	private void findDerivatives()
	{
		final int[][] xMask = { { 3, 2, 1, 0, -1, - 2, - 3 }, { 4, 3, 2, 0, -2, - 3, - 4, }, { 5, 4, 3, 0, -3, - 4, - 5 }, { 6, 5, 4, 0, -4, - 5, - 6 }, { 5, 4, 3, 0, -3, - 4, - 5 }, { 4, 3, 2, 0, -2, - 3, - 4 }, { 3, 2, 1, 0, -1, - 2, - 3 } };
		final int[][] yMask = { { 3, 4, 5, 6, 5, 4, 3 }, { 2, 3, 4, 5, 4, 3, 2 }, { 1, 2, 3, 4, 3, 2, 1 }, { 0, 0, 0, 0, 0, 0, 0 }, { -1, -2, -3, -4, -3, -2, -1 }, { -2, -3, -4, -5, -4, -3, -2 }, { -3, -4, -5, -6, -5, -4, -3 } };
		ix = conv2(map, xMask);
		iy = conv2(map, yMask);
		ixx=new double[interval][map[0].length];
		iyy=new double[interval][map[0].length];
		ixy=new double[interval][map[0].length];
		for(int i=0;i<ix.length;i++)
		{
			for(int j=0;j<ix[0].length;j++)
			{
				ixx[i][j] = ix[i][j] * ix[i][j];
				iyy[i][j] = iy[i][j] * iy[i][j];
				ixy[i][j] = ix[i][j] * iy[i][j];
			}
		}
	}
	private void generateGauss(double sd, int size)
	{
		if (size % 2 == 0)
		{
			size++;
		}
		double[][] kernel = new double[size][size];
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				int x = j - ((size - 1) / 2);
				int y = i - ((size - 1) / 2);
				kernel[i][j] = (1.0 / (2.0 * Math.PI * sd * sd)) * Math.exp(((x * x) + (y * y)) / (-2.0 * sd * sd));
			}
		}
		gKernel = kernel;
	}
	private double[][] conv2(double[][] input, int[][] mask)
	{
		double[][] result = new double[interval][input[0].length];
		for (int i = start; i < interval+start; i++)
		{
			for (int j = 0; j < input[0].length; j++)
			{
				double value = 0;
				for (int k = 0; k < mask.length; k++)
				{
					for (int l = 0; l < mask[0].length; l++)
					{
						int y = i + k - ((mask.length - 1) / 2);
						int x = j + l - ((mask[0].length - 1) / 2);
						if (x >= 0 && y >= 0 && x < input[0].length && y < input.length)
						{
							value = value + (input[y][x] * mask[k][l]);
						}
					}
				}
				result[i-start][j] = value;
			}
		}
		return result;
	}
	private double conv(double[][] input, double[][] mask, int x, int y)
	{
		double value = 0;
		for (int i = 0; i < mask.length; i++)
		{
			for (int j = 0; j < mask[0].length; j++)
			{
				int x1 = x + j - ((mask[0].length - 1) / 2);
				int y1 = y + i - ((mask.length - 1) / 2);
				if (x1 >= 0 && y1 >= 0 && x1 < input[0].length && y1 < input.length)
				{
					value = value + (mask[i][j] * input[y1][x1]);
				}
			}
		}
		return value;
	}
	public static double[][] copyOf(double[][] original)
	{
		double[][] copy = new double[original.length][];
		for (int i = 0; i < original.length; i++)
		{
			copy[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return copy;
	}
	public static ArrayList<Point> filterCorners(int[][] input)
	{
		int[][] in = RectangleController.copyOf(input);
		boolean[][] map2=new boolean[in.length][in[0].length];
		ArrayList<Point> corners=new ArrayList<>();
		for (int i = 0; i < in.length; i++)
		{
			for (int j = 0; j < in[0].length; j++)
			{
				if (in[i][j] > 0)
				{
					//Considers each point and adds only if all adjacent points are below it.
					int value=in[i][j];
					boolean valid=true;
					OuterLoop:
					for(int k=0;k<3;k++)
					{
						for(int l=0;l<3;l++)
						{
							int x=j+l-1;
							int y=i+k-1;
							if(x>=0&&y>=0&&x<in[0].length&&y<in.length)
							{
								if(in[y][x]>value)
								{
									valid=false;
									break OuterLoop;
								}
							}
						}
					}
					if(valid)
					{
						map2[i][j]=true;
						corners.add(new Point(j,i));
					}
				}
			}
		}
		return corners;
	}
	private static int[][] copyOf(int[][] input)
	{
		int[][] copy = new int[input.length][];
		for (int i = 0; i < input.length; i++)
		{
			copy[i] = Arrays.copyOf(input[i], input[i].length);
		}
		return copy;
	}
	public static boolean[][] applySearch(boolean[][] origin, boolean[][] mask, int x, int y)
	{
		for(int i=0;i<mask.length;i++)
		{
			for(int j=0;j<mask[0].length;j++)
			{
				if(mask[i][j])
				{
					int x1=j+x;
					int y1=i+y;
					if(x1>=0&&y1>=0&&x1<origin[0].length&&y1<origin.length)
					{
						origin[y1][x1]=true;
					}
				}
			}
		}
		return origin;
	}
}
