package newVision;

import java.awt.Point;

import visionCore.Particle;

public class Blob
{
	public static final double ZERO=0.1;
	public int x;
	public int y;
	public double[][] map;
	public double count=1;
	public Point[] corners=new Point[4];
	private Point target=null;
	private int tWidth=0;
	private int tHeight=0;
	private double angle=0;
	public Point tLocation=null;
	public Blob(Blob blob)
	{
		this.x=blob.x;
		this.y=blob.y;
		count=0.0;
		this.map=new double[blob.map.length][blob.map[0].length];
	}
	public Blob(int x, int y, double[][] map, double start)
	{
		this.x=x;
		this.y=y;
		this.map=map;
		count=start;
	}
	public double getAngle()
	{
		return angle;
	}
	public void setAngle(double angle)
	{
		this.angle=angle;
	}
	public int getTWidth()
	{
		if(tWidth==0)
		{
			tWidth=getWidth();
		}
		return tWidth;
	}
	public void setTWidth(int w)
	{
		tWidth=w;
	}
	public int getTHeight()
	{
		if(tHeight==0)
		{
			tHeight=getHeight();
		}
		return tHeight;
	}
	public void setTHeight(int h)
	{
		tHeight=h;
	}
	public Point getTarget()
	{
		if(target==null)
		{
			target=new Point(getWidth()/2,getHeight()/2);
		}
		return target;
	}
	public void setTarget(Point point)
	{
		target=point;
	}
	public double getX()
	{
		return x;
	}
	public double getY()
	{
		return y;
	}
	public double[][] getMap()
	{
		return map;
	}
	public double getGlobalValue(int x, int y)
	{
		return map[y-this.y][x-this.x];
	}
	public boolean setGlobalValue(int x, int y, double value)
	{
		setLocalValue(x-this.x,y-this.y,value);
		return true;
	}
	public boolean globalInMap(int x, int y)
	{
		return localInMap(x-this.x,y-this.y);
	}
	public boolean localInMap(int x, int y)
	{
		if(x<0||y<0)
		{
			return false;
		}
		if(x>=getWidth()||y>=getHeight())
		{
			return false;
		}
		return true;
	}
	public double getLocalValue(int x, int y)
	{
		return map[y][x];
	}
	public double localValue(int x,int y)
	{
		if(localInMap(x,y))
		{
			return getLocalValue(x,y);
		}
		else
		{
			return Blob.ZERO;
		}
	}
	public void setLocalValue(int x, int y, double value)
	{
		count = count+value;
		map[y][x]=value;
	}
	public double getValue(int x, int y)
	{
		return map[y][x];
	}
	public int getWidth()
	{
		return map[0].length;
	}
	public int getHeight()
	{
		return map.length;
	}
	public void expandRight()
	{
		double[] row;
		int rowLength=map[0].length;
		for(int i=0;i<map.length;i++)
		{
			row=new double[rowLength+1];
			System.arraycopy(map[i], 0, row, 0, map[i].length);
			map[i]=row;
		}
	}
	public void expandDown()
	{
		double[][] copy=new double[map.length+1][map[0].length];
		System.arraycopy(map, 0, copy, 0, map.length);
		copy[map.length]=new double[map[0].length];
		map=copy;
	}
	public void expandLeft()
	{
		double[] row;
		int rowLength=map[0].length;
		for(int i=0;i<map.length;i++)
		{
			row=new double[rowLength+1];
			System.arraycopy(map[i], 0, row, 1, map[i].length);
			map[i]=row;
		}
		x--;
	}
	public void expandUp()
	{
		double[][] copy=new double[map.length+1][map[0].length];
		System.arraycopy(map, 0, copy, 1, map.length);
		copy[0]=new double[map[0].length];
		map=copy;
		y--;
	}
	public void shorten()
	{
		if(map.length<1)
		{
			return;
		}
		boolean change=true;
		while(change)
		{
			change=false;
			boolean shortenFirst=true;
			boolean shortenLast=true;
			if(map.length<2)
			{
				shortenLast=false;
			}
			for(int i=0;i<map[0].length;i++)
			{
				if(map[0][i]>Blob.ZERO)
				{
					shortenFirst=false;
				}
				if(shortenLast)
				{
					if(map[map.length-1][i]>Blob.ZERO)
					{
						shortenLast=false;
					}
				}
			}
			if(shortenFirst)
			{
				change=true;
				y++;
				double[][] copy=new double[map.length-1][map[0].length];
				System.arraycopy(map, 1, copy, 0, copy.length);
				map=copy;
			}
			if(shortenLast)
			{
				change=true;
				double[][] copy=new double[map.length-1][map[0].length];
				System.arraycopy(map, 0, copy, 0, copy.length);
				map=copy;
			}
			shortenFirst=true;
			shortenLast=true;
			if(map[0].length<2)
			{
				shortenLast=false;
			}
			for(int i=0;i<map.length;i++)
			{
				if(map[i][0]>Blob.ZERO)
				{
					shortenFirst=false;
					if(!shortenLast)
					{
						break;
					}
				}
				if(shortenLast)
				{
					if(map[i][map[0].length-1]>Blob.ZERO)
					{
						shortenLast=false;
						if(!shortenFirst)
						{
							break;
						}
					}
				}
			}
			double[] row;
			if(shortenFirst)
			{
				int rowSize;
				x++;
				if(shortenLast)
				{
					rowSize=map[0].length-2;
					for(int i=0;i<map.length;i++)
					{
						row=new double[rowSize];
						System.arraycopy(map[i],1,row,0,row.length);
						map[i]=row;
					}
				}
				else
				{
					rowSize=map[0].length-1;
					for(int i=0;i<map.length;i++)
					{
						row=new double[rowSize];
						System.arraycopy(map[i],1,row,0,row.length);
						map[i]=row;
					}
				}
			}
			else
			{
				if(shortenLast)
				{
					int rowSize=map[0].length-1;
					for(int i=0;i<map.length;i++)
					{
						row=new double[rowSize];
						System.arraycopy(map[i],0,row,0,row.length);
						map[i]=row;
					}
				}
			}
		}
	}
	public void moveTo(int x, int y)
	{
		int dx=Math.abs(x-this.x);
		int dy=Math.abs(y-this.y);
		while(dx<0)
		{
			expandLeft();
			dx--;
		}
		while(dy<0)
		{
			expandUp();
			dy--;
		}
		this.x=x;
		this.y=y;
	}
}
