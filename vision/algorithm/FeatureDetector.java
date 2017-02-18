package algorithm;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import visionCore.Particle;

public class FeatureDetector implements Runnable
{
	final static int[][] SOBEL_3={ { 3, 2, 1, 0, -1, - 2, - 3 }, { 4, 3, 2, 0, -2, - 3, - 4, }, { 5, 4, 3, 0, -3, - 4, - 5 }, { 6, 5, 4, 0, -4, - 5, - 6 }, { 5, 4, 3, 0, -3, - 4, - 5 }, { 4, 3, 2, 0, -2, - 3, - 4 }, { 3, 2, 1, 0, -1, - 2, - 3 } };
	final static int[][] SOBEL_2=
		{
			{-1, 0, 1},
			{-2, 0, 2},
			{-1, 0, 1},
		};
	final static int[][] GAUSS_FILTER=
		{
			{2,	4,	5,	4,	2},
			{4,	9,	12,	9,	4},
			{5,	12,	15,	12,	5},
			{4,	9,	12,	9,	4},
			{2,	4,	5,	4,	2}
		};
	final static int GAUSS_DIV=159;
	final static int threshold=1000;//80000;
	//End of fine tuning variables
	int[][] xDeriv;
	int[][] yDeriv;
	private double[][] ix=null;
	private double[][] iy=null;
	private int threads;
	private Thread[] t;
	private Masker[] maskers;
	private double[][] map;
	public ArrayList<Point> corners=null;
	private boolean findCorners=false;
	//Variable(s) for testing purposes. Can be removed in final code.
	boolean done=false;
	//Variable for algorithm producing purposes, still needed for program to run
	public int[][] cornerScore;
	public FeatureDetector(int threads)
	{
		this.threads=threads;
		generateKernels();
		t=new Thread[threads];
		maskers=new Masker[threads];
		for(int i=0;i<t.length;i++)
		{
			maskers[i]=new Masker();
			t[i]=new Thread(maskers[i]);
		}
	}
	public void processDeriv(double[][] input)
	{
		int rectangleHeight=input.length/threads;
		for(int i=0;i<t.length;i++)
		{
			//Initializing maskers
			maskers[i].setMap(input);
			maskers[i].setRange(new Rectangle(0, rectangleHeight*i, input[0].length,rectangleHeight));
		}
		//Begins first operation, Gaussian filter, reduces noise of picture
		double[][] filtered=runMaskers(input, GAUSS_FILTER, GAUSS_DIV);
		//X-Derivative Operation, same thing, but different divisor and mask
		ix=runMaskers(filtered, xDeriv);
		//Y-Derivative Operation, you should get the pattern
		iy=runMaskers(filtered, yDeriv);
		//Usage of the derivatives depends on the operation, which will be in another method
	}
	private double[][] runMaskers(double[][] input, int[][] mask)
	{
		return runMaskers(input, mask, 1);
	}
	private double[][] runMaskers(double[][] input, int[][] mask, int div)
	{
		int rectangleHeight=input.length/threads;
		for(int i=0;i<t.length;i++)
		{
			//Begins Operation
			maskers[i].setOperation(Operation.CONV);
			maskers[i].setMask(mask);
			maskers[i].setDivisor(div);
		}
		for(int i=0;i<t.length;i++)
		{
			//Initializes operation
			t[i]=new Thread(maskers[i]);
			t[i].start();
		}
		//Waits for all threads to end
		for(int i=0;i<t.length;i++)
		{
			try
			{
				t[i].join();
			} 
			catch (InterruptedException e)
			{
				System.err.println("Thread interrupted, result likely invalid.");
				e.printStackTrace();
			}
		}
		for(Thread thread:t)
		{
			if(thread.isAlive())
			{
				System.out.println("Dun goofed");
			}
		}
		double[][] result=new double[map.length][map[0].length];
		for(int a=0;a<t.length;a++)
		{
			//Combines results into main map
			System.arraycopy(maskers[a].result, 0, result, a*rectangleHeight, rectangleHeight);
		}
		return result;
	}
	private int[][] runCorners(double[][][] derivs)
	{
		int rectangleHeight=derivs[0].length/threads;
		for(int i=0;i<t.length;i++)
		{
			//Begins Operation
			maskers[i].setOperation(Operation.CORNER);
			maskers[i].setDerivs(derivs);
			
		}
		for(int i=0;i<t.length;i++)
		{
			//Initializes operation
			t[i]=new Thread(maskers[i]);
			t[i].start();
		}
		//Waits for all threads to end
		for(int i=0;i<t.length;i++)
		{
			try
			{
				t[i].join();
			} 
			catch (InterruptedException e)
			{
				System.err.println("Thread interrupted, result likely invalid.");
				e.printStackTrace();
			}
		}
		int[][] result=new int[map.length][map[0].length];
		for(int a=0;a<t.length;a++)
		{
			//Combines results into main map
			System.arraycopy(maskers[a].corners, 0, result, a*rectangleHeight, rectangleHeight);
		}
		return result;
	}
	public ArrayList<Point> processCorners()
	{
		if(ix==null || iy==null)
		{
			return null;
		}
		ArrayList<Point> corners=new ArrayList<>();
		double[][] ixx=new double[ix.length][ix[0].length];
		double[][] iyy=new double[ix.length][ix[0].length];
		double[][] ixy=new double[ix.length][ix[0].length];
		//Very simple operation, no need of multi-threading
		for(int i=0;i<ix.length;i++)
		{
			for(int j=0;j<ix[0].length;j++)
			{
				ixx[i][j]=ix[i][j]*ix[i][j];
				iyy[i][j]=iy[i][j]*iy[i][j];
				ixy[i][j]=ix[i][j]*iy[i][j];
			}
		}
		//Now for convolutions. Those are resource intensive and need multi-threading
		double[][][] derivs=new double[3][][];
		derivs[0]=ixx;
		derivs[1]=iyy;
		derivs[2]=ixy;
		cornerScore=runCorners(derivs);
		corners=filterCorners(cornerScore);
		return corners;
	}
	public static ArrayList<Point> filterCorners(int[][] input)
	{
		int[][] in = copyOf(input);
		boolean[][] map=new boolean[in.length][in[0].length];
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
						map[i][j]=true;
						corners.add(new Point(j,i));
					}
				}
			}
		}
		return corners;
	}
	public void setMap(double[][] map)
	{
		this.map=copyOf(map);
	}
	public void setFindCorners(boolean find)
	{
		findCorners=find;
	}
	private void generateKernels()
	{
		xDeriv=SOBEL_2;
		yDeriv=new int[SOBEL_2.length][SOBEL_2[0].length];
		for(int i=0;i<SOBEL_2.length;i++)
		{
			for(int j=0;j<SOBEL_2[0].length;j++)
			{
				yDeriv[j][i]=SOBEL_2[i][j];
			}
		}
	}
	public static double conv(double[][] input, int[][] mask, int x, int y)
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
	public static double[][] conv2(double[][] input, int[][] mask, Rectangle r)
	{
		double[][] result = new double[(int) r.getHeight()][(int) r.getWidth()];
		for (int i = r.y; i < r.getMaxY(); i++)
		{
			for (int j = r.x; j < r.getMaxX(); j++)
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
				result[(int) (i-r.getY())][(int) (j-r.getX())] = value;
			}
		}
		return result;
	}
	public static double[][] conv2(double[][] input, int[][] mask)
	{
		return conv2(input, mask, new Rectangle(0,0,input[0].length,input.length));
	}
	public static double[][] divide2(double[][] input, int div)
	{
		if(input.length<1)
		{
			return new double[0][];
		}
		double[][] result=new double[input.length][input[0].length];
		for(int i=0;i<input.length;i++)
		{
			for(int j=0;j<input[0].length;j++)
			{
				result[i][j]=input[i][j]/(1.0*div);
			}
		}
		return result;
	}
	public static int[][] corner(double[][] Ixx, double[][] Iyy, double[][] Ixy, Rectangle r)
	{
		int[][] result = new int[(int) r.getHeight()][(int) r.getWidth()];
		for (int i = r.y; i < r.getMaxY(); i++)
		{
			for (int j = r.x; j < r.getMaxX(); j++)
			{
				int value = 0;
				//Note: Big I (compared to i) represents a map. Lowercase for a single value.
				double ixx=conv(Ixx, GAUSS_FILTER, j, i)/GAUSS_DIV;
				double iyy=conv(Iyy, GAUSS_FILTER, j, i)/GAUSS_DIV;
				double ixy=conv(Ixy, GAUSS_FILTER, j, i)/GAUSS_DIV;
				double tr = ixx + iyy;
				double det = (ixx * iyy) - (ixy * ixy);
				value = (int) (det - (0.04f * (tr * tr)));
				if (value > threshold)
				{
					result[(int) (i-r.getY())][(int) (j-r.getX())] = value;
				}
			}
		}
		return result;
	}
	public static ArrayList<Particle> findParticles(boolean[][] map)// Generates rectangles for every point
	{
		long total=0;
		ArrayList<Particle> toReturn = new ArrayList<Particle>();
		int iStart = 0, jStart = 0, iMax = 0, jMax = 0;
		iMax = map[0].length;
		jMax = map.length;
		for (int i = iStart; i < iMax; i++)
		{
			for (int j = jStart; j < jMax; j++)
			{
				if (map[j][i])
				{
					Particle particle = new Particle(i, j, new boolean[1][1]);
					particle.map[0][0] = true;
					boolean change = true;
					Particle expansion = new Particle(
							(int) (particle.getX()),
							(int) (particle.getY()), new boolean[1][1]);
					if (particle.getX() > 0)
					{
						expansion.expandLeft();
						expansion.setGlobalValue(
								(int) (particle.getX() - 1),
								(int) (particle.getY()), true);
					}
					if (particle.getY() > 0)
					{
						expansion.expandUp();
						expansion.setGlobalValue((int) (particle.getX()),
								(int) (particle.getY() - 1), true);
					}
					if (particle.getX() < map[0].length - 1)
					{
						expansion.expandRight();
						expansion.setGlobalValue(
								(int) (particle.getX() + 1),
								(int) (particle.getY()), true);
					}
					if (particle.getY() < map.length - 1)
					{
						expansion.expandDown();
						expansion.setGlobalValue((int) (particle.getX()),
								(int) (particle.getY() + 1), true);
					}
					int x;
					int y;
					while (change)
					{
						Particle next = new Particle((int) (expansion.getX()),(int) (expansion.getY()),new boolean[expansion.map.length][expansion.map[0].length]);
						change = false;
						for (int k = 0; k < expansion.getWidth(); k++)
						{
							for (int l = 0; l < expansion.getHeight(); l++)
							{
								// Compare to picture map values to
								// determine expansion
								if (expansion.getLocalValue(k, l))
								{
									x = (int) (k + expansion.getX());
									y = (int) (l + expansion.getY());
									if (map[y][x])
									{
										map[y][x]=false;
										change = true;
										// Expand the particle into that square
										// Determines if size increase of particle required
										if (x - particle.getX() < 0)
										{
											particle.expandLeft();
										}
										if (x - particle.getX() >= particle.getWidth())
										{
											particle.expandRight();
										}
										if (y - particle.getY() < 0)
										{
											particle.expandUp();
										}
										if (y - particle.getY() >= particle.getHeight())
										{
											particle.expandDown();
										}
										// Sets particle value
										particle.setGlobalValue(x, y, true);
										// Prepares expansion for next cycle
										// Make surrounding position of new
										// particle true
										// Top Side
										// Check if space in global map
										if (y > 0)
										{
											// Check if expansion neccessary
											while (y - 1 - next.getY() < 0)
											{
												next.expandUp();
											}
											next.setGlobalValue(x, y - 1,
													true);
										}
										// Left side
										if (x > 0)
										{
											// Check if expansion neccessary
											while (x - 1 - next.getX() < 0)
											{
												next.expandLeft();
											}
											next.setGlobalValue(x - 1, y,
													true);
										}
										// Bottom side
										if (y + 1 < map.length)
										{
											// Check if expansion neccessary
											while (y + 1 - next.getY() >= next
													.getHeight())
											{
												next.expandDown();
											}
											next.setGlobalValue(x, y + 1,
													true);
										}
										// Right Side
										if (x + 1 < map[0].length)
										{
											// Check if expansion neccessary
											while (x + 1 - next.getX() >= next.getWidth())
											{
												next.expandRight();
											}
											next.setGlobalValue(x + 1, y,true);
										}
									}
								}
							}
						}
						if (change)
						{
							next.shorten();
							expansion = next;
						}
						
					}
					toReturn.add(particle);
				}
			}
		}
		System.out.println("Total \t"+total);
		return toReturn;
	}
	@Override
	public void run()
	{
		done=false;
		long t1=System.currentTimeMillis();
		processDeriv(map);
		System.out.printf("Processed Derivs. Time: [%f]s\n", (System.currentTimeMillis()-t1)/1000.0);
		if(findCorners)
		{
			t1=System.currentTimeMillis();
			corners=processCorners();
			System.out.printf("Found Corners. Time: [%f]s\n", (System.currentTimeMillis()-t1)/1000.0);
		}
		done =true;
	}
	public boolean isDone()
	{
		if(done)
		{
			done=false;
			return true;
		}
		else
		{
			return false;
		}
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
	public static int[][] copyOf(int[][] original)
	{
		int[][] copy = new int[original.length][];
		for (int i = 0; i < original.length; i++)
		{
			copy[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return copy;
	}
}
