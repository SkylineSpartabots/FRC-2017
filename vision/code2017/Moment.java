package code2017;

import java.awt.Point;

public class Moment
{
	public static long rawMoment(int p, int q, Particle particle)
	{
		long m=0;
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					m=(long) (m+((Math.pow(x, p))*(Math.pow(y, q))));
				}
			}
		}
		return m;
	}
	public static long centMoment(int p, int q, Particle particle, Point centroid)
	{
		long u=0;
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					u=(long) (u+((Math.pow(x-centroid.x, p))*(Math.pow(y-centroid.y,q))));
				}
			}
		}
		return u;
	}
	public static double scaleInvariant(long uij, int i, int j, long u00)
	{
		double nj=0;
		nj=uij/(Math.pow(u00, 1+((i+j)/2)));
		return nj;
	}
	public static double moi(Particle particle, Point centroid)
	{
		double moment=0;
		int m00=particle.count;
		long u20=centMoment(2, 0, particle, centroid);
		long u02=centMoment(0, 2, particle, centroid);
		
		long m20=rawMoment(2, 0, particle);
		long m02=rawMoment(0, 2, particle);
		long m10=rawMoment(1, 0, particle);
		long m01=rawMoment(0, 1, particle);
		long m11=rawMoment(1, 1, particle);
		long U20=(long) (m20-(m10*m10/(m00*1.0)));
		long U02=(long) (m02-(m01*m01/(m00*1.0)));
		long u11=m11-(m10*m01/m00);
		//*
		u20=U20;
		u02=U02;
		//*/
		
		double n20=scaleInvariant(u20, 2, 0, m00);
		double n02=scaleInvariant(u02, 0, 2, m00);
		double n11=scaleInvariant(u11, 1, 1, m00);
		//moment=n20+n02;
		moment=n20-n02;
		moment=moment*moment;
		moment=moment+(4*n11*n11);
		
		return moment;
	}
	public static double distance(Point p, Point p2)
	{
		return Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
	}
}
