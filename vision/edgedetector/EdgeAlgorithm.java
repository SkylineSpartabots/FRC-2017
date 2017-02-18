package edgedetector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import code2017.Particle;

public class EdgeAlgorithm
{
	final static double THRESHOLD_LOW=40;
	final static double THRESHOLD_HIGH=80;
	final static int[][] PASS_THINNING_LIST=
		{
			{0, -1},
			{0, 1},
			{-1, 0},
			{1, 0}
		};
	public static ArrayList<Particle> findEdges(double[][] raw)
	{
		double[][] map=EdgeAlgorithm.copyOf(raw);
		Rectangle image=new Rectangle(raw[0].length, raw.length);
		
		ArrayList<Particle> toReturn = new ArrayList<Particle>();
		//Ignore edge of image, likely to have edge if image is bright
		int iStart = 1, jStart = 1, iMax = 0, jMax = 0;
		iMax = map[0].length-2;
		jMax = map.length-2;
		for (int i = iStart; i < iMax; i++)
		{
			for (int j = jStart; j < jMax; j++)
			{
				if (map[j][i]>=EdgeAlgorithm.THRESHOLD_HIGH)
				{
					map[j][i]=0;
					Particle particle = new Particle(i, j, new boolean[1][1]);
					particle.map[0][0] = true;
					boolean change = true;
					Particle expansion = new Particle((int) (particle.getX()), (int) (particle.getY()), new boolean[1][1]);
					expansion = EdgeAlgorithm.expandAround(i, j, expansion, image);
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
								// Compare to picture map values to determine expansion
								if (expansion.getLocalValue(k, l))
								{
									x = (int) (k + expansion.getX());
									y = (int) (l + expansion.getY());
									if (map[y][x]>EdgeAlgorithm.THRESHOLD_LOW)
									{
										map[y][x]=0;
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
										next=EdgeAlgorithm.expandAround(x, y, next, image);
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
		return toReturn;
	}
	public static boolean evaluateEdgePoint(boolean[][] local, boolean neighborPass)//Returns true when point is valid
	{
		if(neighborPass)
		{
			return true;
		}
		int neighbors=0;
		//N, S, W, E; NW, NE, SW, SE
		if(local[0][1])
		{
			neighbors++;
		}
		if(local[2][1])
		{
			neighbors++;
		}
		if(local[1][0])
		{
			neighbors++;
		}
		if(local[1][2])
		{
			neighbors++;
		}
		//Diags
		if(local[0][0])
		{
			neighbors++;
		}
		if(local[0][2])
		{
			neighbors++;
		}
		if(local[2][0])
		{
			neighbors++;
		}
		if(local[2][2])
		{
			neighbors++;
		}
		if(neighbors==0)
		{
			return false;
		}
		if(neighbors==1)
		{
			return true;
		}
		if(neighbors==7)
		{
			return false;
		}
		boolean[] linear=new boolean[8];
		linear[0]=local[0][0];
		linear[1]=local[0][1];
		linear[2]=local[0][2];
		linear[3]=local[1][2];
		linear[4]=local[2][2];
		linear[5]=local[2][1];
		linear[6]=local[2][0];
		linear[7]=local[1][0];
		boolean searchFor=true;
		if(linear[0])
		{
			if(linear[7])
			{
				searchFor=false;
			}
		}
		int i=0;
		boolean foundFalse=false;
		boolean foundNextTrue=false;
		for(;i<8;i++)
		{
			if(linear[i]!=searchFor)
			{
				if(foundFalse)
				{
					foundNextTrue=true;
				}
			}
			else
			{
				if(foundNextTrue)
				{
					return true;
				}
				else
				{
					foundFalse=true;
				}
			}
		}
		return false;
	}
	public static Particle cardinalThinPass(Particle particle, int dx, int dy)
	{
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					boolean pass=particle.localValue(x+dx, y+dy);
					if(!evaluateEdgePoint(EdgeAlgorithm.getLocalArray(particle, x, y), pass))
					{
						particle.setLocalValue(x, y, false);
					}
				}
			}
		}
		return particle;
	}
	public static Particle thinEdge(Particle particle)
	{
		for(int i=0;i<PASS_THINNING_LIST.length;i++)
		{
			particle=EdgeAlgorithm.cardinalThinPass(particle, PASS_THINNING_LIST[i][0], PASS_THINNING_LIST[i][1]);
		}
		particle.shorten();
		return particle;
	}
	public static ArrayList<Particle> thinEdge(ArrayList<Particle> particles)
	{
		for(int i=0;i<particles.size();i++)
		{
			particles.set(i, thinEdge(particles.get(i)));
		}
		return particles;
	}
	public static boolean[][] getLocalArray(Particle particle, int x, int y)
	{
		boolean[][] local=new boolean[3][3];
		for(int i=0;i<3;i++)
		{
			for(int j=0;j<3;j++)
			{
				int x1=x+j-1;
				int y1=y+i-1;
				local[i][j]=particle.localValue(x1, y1);
			}
		}
		return local;
	}
	public static double[][] copyOf(double[][] original) 
	{
		double[][] copy = new double[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        copy[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return copy;
	}
	public static Particle expandAround(int x, int y, Particle expansion, Rectangle image)
	{
		//8 connectivity
		//N, S, W, E; NW, NE, SW, SE
		short[] diag= new short[4];
		//N
		if(image.contains(x, y-1))
		{
			if(!expansion.globalInMap(x, y-1))
			{
				expansion.expandUp();				
			}
			expansion.setGlobalValue(x, y-1, true);
			diag[0]++;
			diag[1]++;
		}
		//S
		if(image.contains(x, y+1))
		{
			if(!expansion.globalInMap(x, y+1))
			{
				expansion.expandDown();				
			}
			expansion.setGlobalValue(x, y+1, true);
			diag[2]++;
			diag[3]++;
		}
		//W
		if(image.contains(x-1, y))
		{
			if(!expansion.globalInMap(x-1, y))
			{
				expansion.expandLeft();				
			}
			expansion.setGlobalValue(x-1, y, true);
			diag[0]++;
			diag[2]++;
		}
		//E
		if(image.contains(x+1, y))
		{
			if(!expansion.globalInMap(x+1, y))
			{
				expansion.expandRight();				
			}
			expansion.setGlobalValue(x+1, y, true);
			diag[1]++;
			diag[3]++;
		}
		//NW
		if(diag[0]==2)
		{
			expansion.setGlobalValue(x-1, y-1, true);
		}
		//NE
		if(diag[1]==2)
		{
			expansion.setGlobalValue(x+1, y-1, true);
		}
		//SW
		if(diag[2]==2)
		{
			expansion.setGlobalValue(x-1, y+1, true);
		}
		//SE
		if(diag[3]==2)
		{
			expansion.setGlobalValue(x+1, y+1, true);
		}
		return expansion;
	}
}
