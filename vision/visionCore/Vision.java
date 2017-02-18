package visionCore;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;

import newVision.Vision2;
import algorithm.*;

public class Vision
{
	/*
	 * TO DO LIST	
	 * [ ] Clean up boolean map with gaussian mask				
	 * [ ] Understand multiple targets available, select least angled.	
	 */
	// Because some code was copy pas- *ahem* written by me, arrays that
	// represent images are weird as they work as map[y][x]
	// Detail:the reason for this is because the objects were created as
	// [row][column], this is found in getArray where you find [height][width]
	public final double viewAngle = Math.toRadians(67.9446);
	//----------------SCORING MECHANISM----------------------
	//Manually configure this. EQUIV_RECT should always go first.
	final static ScoreType[] SCORE_PATTERN=
		{
			ScoreType.EQUIV_RECT,
			ScoreType.COVERAGE,
			ScoreType.MOMENT,
			ScoreType.PROFILE
		};
	//-------------------HSL------------------------------------------------------------------FIX THIS MOVE TO DIFFERENT THRESHOLD PATTERN
	// Lots of variables to adjust HSL threshold, naming scheme initial of hsl
	// type and min/max threshold, inclusive; Default GRIP tutorial
	//Default Values
	public static int HMIN = 63;
	public static int HMAX = 96;
	public static int SMIN = 48;
	public static int SMAX = 255;// This value not used as it is MAXimum. If changed go to createMap method to add into code
	public static int LMIN = 40;
	public static int LMAX = 161;
	public static int VMIN = 40;
	public static int VMAX = 161;
	//Used variables
	public int hmin = HMIN;
	public int hmax = HMAX;
	public int smin = SMIN;
	public int smax = SMAX;// This value not used as it is maximum. If changed go to createMap method to add into code
	public int lmin = LMIN;
	public int lmax = LMAX;
	public int vmin = VMIN;
	public int vmax = VMAX;
	// End of lots of variables
	//--------------------FIND PARTICLES------------------------------------------------------
	final double totalPercent = 0.0006510416666666666;
	int minimumAlive = 200;// Minimum alive cells in particle to be considered particle. This is default value. Real value based on totalPercent.		
	final double maxRatio=3.5;//Highest simple ratio number can have to be valid
	//Arrays of Thresholds, if larger particle, then proceed to next one
	int[] largeMinAlive={300,500};
	final double[] largePercent={largeMinAlive[0]/(640.0*480.0),largeMinAlive[1]/(640.0*480.0)};
	final int furthestDistance = 30;
	int largeParticleIndex=-1;
	//----------------------XY PROFILE---------------------------------------------------
	
	
	//-----------------------COVERAGE AREA----------------------------------------
	final double dev=0.03715117993112262*5.0;
	final double ideal=0.27858733495702565;
	//-------------------------EQUIVALENT RECTANGLE------------------
	final double difference=1.0/25.0;
	final double angleU=		Math.toRadians(140);//Angle range for corner
	final double angleL=		Math.toRadians(20);
	final double maxDistance=10;//Minimum distance for boundaries between top/right/bottom/left
	final Point[] allSurround = { 
			new Point(0, -1), new Point(1, -1), new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(-1, 1), new Point(-1, 0), new Point(-1, -1) };
	//---------------------GLOBAL VARIABLE 
	
	//-------------------------OTHER-----------------
	double distanceNumerator=0;
	//------------------------DEBUGGING VARIABLES--------------------------
	public Particle bestParticle=null;
	public ArrayList<Point> corners_display=new ArrayList<Point>();
	public double[] process(BufferedImage im)
	{
		System.out.println("----------------------NEW IMAGE-------------------------");
		long t1=System.nanoTime();
		boolean[][] map=createMap(im);// ~ 80 ms (Multi - Threadable)
		double[] target=core(map); // ~ 60 ms (Acceptable Time)
		System.out.printf("Total Time: [%d]\n", (int) ((System.nanoTime()-t1)/1000000));
		return target;
	}
	public double[] core(boolean[][] map)
	{
		long start=System.currentTimeMillis();
		for(int i=0;i<largePercent.length;i++)
		{
			largeMinAlive[i]=(int) (largePercent[i]*map.length*map[0].length);
		}
		double[] target = new double[4];
		// Returns center of mass, x returns 2 when none detected
		// toReturn[0] x position of target, in coordinate system of -1.0 to 1.0 left to right
		// toReturn[1] y position of target, in coordinate system of -1.0 to 1.0 bottom to top
		// toReturn[2] distance to target, units should be inches
		// toReturn[3] angle of the target, in radians
		ArrayList<Particle> particles = null;
		particles = findParticles(Vision.copyOf(map));
		if (particles.size() == 0)// No targets detected
		{
			target[0] = 2.0;
			target[1] = 2.0;
			target[2] = 0.0;
			target[3] = 100.0;
			bestParticle=null;
			return target;
		}
		System.out.println("----------------------Particles------------------");
		for (int i = 0; i < particles.size(); i++)
		{
			int[] Score=new int[SCORE_PATTERN.length];
			int totalScore=0;
			for(int j=0;j<SCORE_PATTERN.length;j++)
			{
				Score s = null;
				switch(SCORE_PATTERN[j])
				{
					case EQUIV_RECT:
						s=diagRect(particles.get(i));
						break;
					case COVERAGE:
						s=coverage(particles.get(i));
						break;
					case MOMENT:
						s=moment(particles.get(i));
						break;
					case PROFILE:
						s=profile(particles.get(i));
						break;
				}
				s.evaluateScore();
				Score[j]=s.getScore();
				totalScore=totalScore+Score[j];
				System.out.printf("Score [%d]: [%d]\n",j,s.getScore());
			}
			particles.get(i).score=totalScore;
			System.out.println("Position: ("+particles.get(i).getX()+", "+particles.get(i).getY()+")");
			System.out.println("Count: " +particles.get(i).count);
			System.out.println("Score: "+totalScore);
			System.out.println("-------------------------------------------------");
		}
		boolean impossibleTarget = true;
		int impossCount=0;
		generateDistanceNumerator(map[0].length);
		while (impossibleTarget)
		{
			if(impossCount>=particles.size())
			{
				target[0] = 2.0;
				target[1] = 2.0;
				target[2] = 0.0;
				target[3] = 100.0;
				bestParticle=null;
				return target;
			}
			int recordIndex = 0;
			int record = 99999;// Absurd number that is easy to beat
			for (int i = 0; i < particles.size(); i++)
			{
				if (particles.get(i).score < record)
				{
					recordIndex = i;
					record = particles.get(i).score;
				}
			}
			// Distance calculation
			//target[2]= 1.66 * (map.length / (2 * particles.get(recordIndex).getTWidth() * Math.tan(viewAngle)));
			target[2]=findDistance(particles.get(recordIndex));
			if (target[2] < furthestDistance)
			{
				impossibleTarget = false;
				Particle particle = particles.get(recordIndex);
				if(particle.tLocation==null)
				{
					particle.tLocation=new Point(particle.getWidth()/2,0);
				}
				//Sets image coordinates
				target[0] = particle.tLocation.getX();
				target[1] = particle.tLocation.getY();
				//Converts coordinates
				target[0] = ((target[0]) - (map[0].length / 2.0)) / (map[0].length / 2.0);
				target[1] = -1.0 * ((target[1]) - (map.length / 2.0)) / (map.length / 2.0);
				target[3] = particle.getAngle();
				bestParticle=particle;
				System.out.printf("FINAL DISTANCE--------------------------------------------------------------[%f]-----\n",target[2]);
				System.out.printf("Particle Size: [%d]\n",particle.count);
			} 
			else
			{
				particles.get(recordIndex).score=9999;
				impossCount++;
			}
		}
		System.out.println("Core Time: "+(System.currentTimeMillis()-start));
		return target;
	}
	public void sideProfileDeriv(Particle particle)
	{
		//Using derivatives, finding corners becomes incredibly easy by analyzing the profiles of the side and transforming them into one dimension
		/*
		 * Corner order for reference
		 * 		0	1
		 * 
		 * 		2	3
		 * Profile Naming Scheme:
		 * profL = Profile Left
		 * left profile is from the left side
		 * l/r ordered top to down
		 * u/d ordered left to right
		 */
		int[] profL = new int[particle.getHeight()];
		int[] profR = new int[profL.length];
		int[] profU = new int[particle.getWidth()];
		int[] profD = new int[profU.length];
		for(int i=0;i<profL.length;i++)
		{
			boolean found=false;
			for(int d = 0; d<particle.getWidth();d++)
			{
				if(particle.getLocalValue(d, i))
				{
					profL[i]=d;
					found = true;
					break;
				}
			}
			if(!found)
			{
				System.err.printf("Critical: Found empty ROW during LEFT profile analysis at [%d]. ", i);
				System.exit(1);
			}
			found=false;
			for(int d = 0; d<particle.getWidth();d++)
			{
				if(particle.getLocalValue((particle.getWidth()-1)-d, i))
				{
					profR[i]=d;
					found = true;
					break;
				}
			}
			if(!found)
			{
				System.err.printf("Critical: Found empty ROW during RIGHT profile analysis at [%d]. ", i);
				System.exit(1);
			}
		}
		for(int i=0;i<profU.length;i++)
		{
			boolean found=false;
			for(int d = 0; d<particle.getHeight();d++)
			{
				if(particle.getLocalValue(i, d))
				{
					profU[i]=d;
					found = true;
					break;
				}
			}
			if(!found)
			{
				System.err.printf("Critical: Found empty COLUMN during UP profile analysis at [%d]. ", i);
				System.exit(1);
			}
			found=false;
			for(int d = 0; d<particle.getHeight();d++)
			{
				if(particle.getLocalValue(i, (particle.getHeight()-1)-d))
				{
					profD[i]=d;
					found = true;
					break;
				}
			}
			if(!found)
			{
				System.err.printf("Critical: Found empty COLUMN during DOWN profile analysis at [%d]. ", i);
				System.exit(1);
			}
		}
		//Analysis
		System.out.println("Completed profile collection... Analyzing...");
		int[] derivL = new int[profL.length];
		int[] derivR = new int[profR.length];
		int[] derivU = new int[profU.length];
		int[] derivD = new int[profD.length];
		int LRMAX = (particle.getWidth());
		int UDMAX = (particle.getHeight());
		final int[] mask = { -1 , 0, 1};
		//L
		derivL[0]=(mask[0]*(LRMAX-1))+(mask[2]*profL[1]);
		for(int i = 1; i < derivL.length-1; i++)
		{
			derivL[i]= (mask[0]*(profL[i-1]))+(mask[2]*(profL[i+1]));
		}
		derivL[derivL.length-1]=(mask[0]*profL[derivL.length-2])+(mask[2]*LRMAX);
		//R
		derivR[0]=(mask[0]*(LRMAX-1))+(mask[2]*profR[1]);
		for(int i = 1; i < derivR.length-1; i++)
		{
			derivR[i]= (mask[0]*(profR[i-1]))+(mask[2]*(profR[i+1]));
		}
		derivR[derivR.length-1]=(mask[0]*profR[derivR.length-2])+(mask[2]*LRMAX);
		//U
		derivU[0]=(mask[0]*(UDMAX-1))+(mask[2]*profU[1]);
		for(int i = 1; i < derivU.length-1; i++)
		{
			derivU[i]= (mask[0]*(profU[i-1]))+(mask[2]*(profU[i+1]));
		}
		derivU[derivU.length-1]=(mask[0]*profU[derivU.length-2])+(mask[2]*UDMAX);
		//D
		derivD[0]=(mask[0]*(UDMAX-1))+(mask[2]*profD[1]);
		for(int i = 1; i < derivD.length-1; i++)
		{
			derivD[i]= (mask[0]*(profD[i-1]))+(mask[2]*(profD[i+1]));
		}
		derivD[derivD.length-1]=(mask[0]*profD[derivD.length-2])+(mask[2]*UDMAX);
		System.out.println("Derivatives Generated");
		System.out.println("Generating 2nd Derivatives");
		int[] deriv2L = new int[profL.length];
		int[] deriv2R = new int[profR.length];
		int[] deriv2U = new int[profU.length];
		int[] deriv2D = new int[profD.length];
		//L
		deriv2L[0] = (mask[0] * derivL[0]) + (mask[2] * derivL[1]);
		for (int i = 1; i < deriv2L.length - 1; i++)
		{
			deriv2L[i] = (mask[0] * (derivL[i - 1])) + (mask[2] * (derivL[i + 1]));
		}
		deriv2L[deriv2L.length - 1] = (mask[0] * derivL[deriv2L.length - 2]) + (mask[2] * derivL[deriv2L.length-1]);
		//R
		deriv2R[0] = (mask[0] * derivR[0]) + (mask[2] * derivR[1]);
		for (int i = 1; i < deriv2R.length - 1; i++)
		{
			deriv2R[i] = (mask[0] * (derivR[i - 1])) + (mask[2] * (derivR[i + 1]));
		}
		deriv2R[deriv2R.length - 1] = (mask[0] * derivR[deriv2R.length - 2]) + (mask[2] * derivR[deriv2R.length-1]);
		//U
		deriv2U[0] = (mask[0] * derivU[0]) + (mask[2] * derivU[1]);
		for (int i = 1; i < deriv2U.length - 1; i++)
		{
			deriv2U[i] = (mask[0] * (derivU[i - 1])) + (mask[2] * (derivU[i + 1]));
		}
		deriv2U[deriv2U.length - 1] = (mask[0] * derivU[deriv2U.length - 2]) + (mask[2] * derivU[deriv2U.length-1]);
		//D
		deriv2D[0] = (mask[0] * derivD[0]) + (mask[2] * derivD[1]);
		for (int i = 1; i < deriv2D.length - 1; i++)
		{
			deriv2D[i] = (mask[0] * (derivD[i - 1])) + (mask[2] * (derivD[i + 1]));
		}
		deriv2D[deriv2D.length - 1] = (mask[0] * derivD[deriv2D.length - 2]) + (mask[2] * derivD[deriv2D.length-1]);
		System.out.println("2nd Derivatives Generated");
		/*
		 * Notes:
		 * 2nd Derivative Useless, data becomes too obscure
		 * Use 1st Derivative, start from middle, find deviations from 0. From top view, use 2nd deviation in both directions
		 * Deviations are >3, two types of deviations: Peaks and Slopes. 
		 * Peaks are obvious, sharp upwards movements in a small period of space
		 * Slopes trickier, slow, but significant upwards movement that continues moving. Represent less sharp angles. 
		 */
		final int SMALL_THRESHOLD=3;
		final int BIG_THRESHOLD=7;
		int norm;
		//Left, finding coordinate
		norm=derivL[derivL.length/2];
		for(int i=(derivL.length)/2;i>=0;i--)
		{
			int max=Math.abs(norm-derivL[i]);
			if(max>SMALL_THRESHOLD)
			{
				for(int j=i-1;j>=0;j--)
				{
					int dev=Math.abs(norm-derivL[j]);
					if(dev>max)
					{
						max=dev;
					}
					else
					{
						break;
					}
				}
			}
			if(max>BIG_THRESHOLD)
			{
				particle.corners[0].y=i;
			}
		}
	}
	public Score diagRect(Particle particle)
	{
		/*
		 * Corner order for reference
		 * 		0	1
		 * 
		 * 		2	3
		 */
		double width;
		double height;
		
		//Two options, measure diagonally or just all at once sum of x y. Second option here.
		int[] smallestSums = {9999, 9999, 9999, 9999};
		Point[] local_fixed= {new Point(0,0), new Point(particle.getWidth()-1, 0), new Point(0, particle.getHeight()-1), new Point(particle.getWidth()-1, particle.getHeight()-1)};
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					for(int i=0;i<smallestSums.length;i++)
					{
						int sum=Math.abs(local_fixed[i].x-x) + Math.abs(local_fixed[i].y-y);
						if(sum < smallestSums[i])
						{
							smallestSums[i]=sum;
							particle.corners[i]=new Point(x,y);
						}
					}
				}
			}
		}
		//Translate corners from local values to global values
		for(int i=0;i<particle.corners.length;i++)
		{
			particle.corners[i].translate((int) (particle.getX()), (int) (particle.getY()));
		}
		
		width=(distance(particle.corners[0],particle.corners[1])+distance(particle.corners[2],particle.corners[3]))/2.0;
		height=(distance(particle.corners[0],particle.corners[2])+distance(particle.corners[1],particle.corners[3]))/2.0;
		particle.tLocation=new Point((particle.corners[0].x+particle.corners[1].x)/2,(particle.corners[0].y+particle.corners[1].y)/2);
		particle.setTWidth((int) width);
		particle.setTHeight((int) height);
		//particle.setAngle(Math.atan((particle.corners[2].y-particle.corners[3].y)/(1.0*particle.corners[2].x-particle.corners[3].x)));
		particle.setAngle(Math.atan((particle.corners[2].y-particle.corners[3].y)/(1.0*(particle.corners[3].x-particle.corners[2].x))));
		//particle.setAngle(Math.atan2(particle.corners[3].x-particle.corners[2].x,particle.corners[2].y-particle.corners[3].y));
		double ratio = (width * 1.0) / (height * 1.0) * 1.0;
		return new Score(ratio, ScoreType.EQUIV_RECT);
	}
	public Score equivRect(Particle particle, ArrayList<Point> interest)
	{
		final int searchSide=3;
		/*
		 * Corner order for reference
		 * 		0	1
		 * 
		 * 		2	3
		 */
		Point[] fixed=
			{
				new Point((int)(particle.getX()),(int)(particle.getY())),
				new Point((int)(particle.getX()+particle.getWidth()-1),(int)(particle.getY())),
				new Point((int)(particle.getX()),(int)(particle.getY()+particle.getHeight()-1)),
				new Point((int)(particle.getX()+particle.getWidth()-1),(int)(particle.getY()+particle.getHeight()-1))
			};
		Point[] default_corner=new Point[4];
		double[] distances=new double[4];
		for(int i=0;i<distances.length;i++)
		{
			distances[i]=99999;
		}
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					for(int i=2;i<default_corner.length;i++)
					{
						Point current=new Point(x,y);
						current.translate(particle.x, particle.y);
						double distance=distance(current,fixed[i]);
						if(distance<distances[i])
						{
							distances[i]=distance;
							default_corner[i]=current;
						}
					}
				}
			}
		}
		//Default corner for 0 & 1
		int peak=9999;
		int peak1=9999;
		for(int x=0;x<particle.getWidth()/2;x++)
		{
			for(int y=0;y<particle.getHeight()/2;y++)
			{
				if(particle.getLocalValue(x, y))
				{
					if(y<peak)
					{
						peak=y;
						Point current=new Point(x,y);
						current.translate(particle.x, particle.y);
						default_corner[0]=current;
					}
				}
			}
		}
		for(int x=particle.getWidth()/2;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight()/2;y++)
			{
				if(particle.getLocalValue(x, y))
				{
					if(y<peak1)
					{
						peak1=y;
						Point current=new Point(x,y);
						current.translate(particle.x, particle.y);
						default_corner[1]=current;
					}
				}
			}
		}
		for(int i=0;i<default_corner.length;i++)
		{
			if(default_corner[i]==null)
			{
				default_corner[i]=fixed[i];
			}
		}
		//Experimental code
		ArrayList<Point> candidates=new ArrayList<Point>();
		for(Point point: interest)
		{
			if(new Rectangle(particle.x-searchSide,particle.y-searchSide,particle.getWidth()+(2*searchSide),particle.getHeight()+(2*searchSide)).contains(point))
			{
				candidates.add(point);
			}
		}
		//Golf scores, less is better
		int[] score=new int[candidates.size()];
		byte[] quadrant=new byte[candidates.size()];
		for(int i=0;i<candidates.size();i++)
		{
			Point point=new Point(candidates.get(i).x,candidates.get(i).y);
			point.translate(-1*particle.x, -1*particle.y);
			//Quadrant #
			byte quad=0;
			if(point.x>particle.getWidth()/2)
			{
				quad=1;
			}
			if(point.y>particle.getHeight()/2)
			{
				quad=(byte) (quad+2);
			}
			quadrant[i]=quad;
			//Distance from corner initial score 
			double distance=distance(candidates.get(i), fixed[quadrant[i]]);
			score[i]=(int)(distance*10);
			double neighbors=0;
			for(Point n: candidates)
			{
				if(!n.equals(candidates.get(i)))
				{
					double nd=distance(n, candidates.get(i));
					nd=Vision.gauss(nd, 15, 0);
					neighbors=neighbors+nd;
				}
			}
			score[i]=(int) (score[i]+(neighbors*20));
		}
		double[] score_top=new double[4];
		for(int i=0;i<score_top.length;i++)
		{
			score_top[i]=Double.MAX_VALUE;
		}
		for(int i=0;i<candidates.size();i++)
		{
			if(score[i]<score_top[quadrant[i]])
			{
				score_top[quadrant[i]]=score[i];
				particle.corners[quadrant[i]]=candidates.get(i);
			}
		}
		//Other code
		/*
		ArrayList<Point> candidates=new ArrayList<Point>();
		for(Point point: interest)
		{
			if(new Rectangle(particle.x-searchSide,particle.y-searchSide,particle.getWidth()+(2*searchSide),particle.getHeight()+(2*searchSide)).contains(point))
			{
				candidates.add(point);
			}
		}
		Point runner0=null;
		Point runner1=null;
		Point best0=null;
		Point best1 = null;
		int best0_s=9999;
		int best1_s=9999;
		int runner0_s=9999;
		int runner1_s=9999;
		
		Point runner2=null;
		Point runner3=null;
		Point best2=null;
		Point best3=null;
		int best2_s=9999;
		int best3_s=9999;
		int runner2_s=9999;
		int runner3_s=9999;
		for(Point point : candidates)
		{
			//Decide on quadrant
			if(point.y>particle.y+(particle.getHeight()/2))
			{
				// 2 or 3
				if(point.x>particle.x+(particle.getWidth()/2))
				{
					//3
					int distance=(particle.y+particle.getHeight()-1)-point.y;
					if(distance<best3_s)
					{
						if(best3!=null)
						{
							runner3=new Point(best3.x,best3.y);
						}
						else
						{
							runner3=null;
						}
						best3=point;
						best3_s=distance;
					}
					else
					{
						if(distance<runner3_s)
						{
							runner3=point;
							runner3_s=distance;
						}
					}
				}
				else
				{
					//2
					int distance=(particle.y+particle.getHeight()-1)-point.y;
					if(distance<best2_s)
					{
						if(best2!=null)
						{
							runner2=new Point(best2.x,best2.y);
						}
						else
						{
							runner2=null;
						}
						best2=point;
						best2_s=distance;
					}
					else
					{
						if(distance<runner2_s)
						{
							runner2=point;
							runner2_s=distance;
						}
					}
				}
			}
			else
			{
				//0 or 1
				if(point.x>particle.x+(particle.getWidth()/2))
				{
					//1
					int distance=point.y-particle.y;
					if(distance<best1_s)
					{
						if(best1!=null)
						{
							runner1=new Point(best1.x,best1.y);
						}
						else
						{
							runner1=null;
						}
						best1=point;
						best1_s=distance;
					}
					else
					{
						if(distance<runner1_s)
						{
							runner1=point;
							runner1_s=distance;
						}
					}
				}
				else
				{
					//0
					int distance=point.y-particle.y;
					if(distance<best0_s)
					{
						if(best0!=null)
						{
							runner0=new Point(best0.x,best0.y);
						}
						else
						{
							runner0=null;
						}
						best0=point;
						best0_s=distance;
					}
					else
					{
						if(distance<runner0_s)
						{
							runner0=point;
							runner0_s=distance;
						}
					}
				}
			}
		}
		if(runner0!=null)
		{
			if(best0.x<runner0.x)
			{
				particle.corners[0]=best0;
			}
			else
			{
				particle.corners[0]=runner0;
			}
		}
		else
		{
			particle.corners[0]=best0;
		}

		if(runner1!=null)
		{
			if(best1.x>runner1.x)
			{
				particle.corners[1]=best1;
			}
			else
			{
				particle.corners[1]=runner1;
			}
		}
		else
		{
			particle.corners[1]=best1;
		}

		if(runner2!=null)
		{
			if(best2.y>runner2.y)
			{
				particle.corners[2]=best2;
			}
			else
			{
				particle.corners[2]=runner2;
			}
		}
		else
		{
			particle.corners[2]=best2;
		}

		if(runner3!=null)
		{
			if(best3.y>runner3.y)
			{
				particle.corners[3]=best3;
			}
			else
			{
				particle.corners[3]=runner3;
			}
		}
		else
		{
			particle.corners[3]=best3;
		}
		*/
		//Finishing code, always keep
		double width;
		double height;
		//Defaults corner if no valid ones detected
		for(int i=0;i<particle.corners.length;i++)
		{
			if(particle.corners[i]==null)
			{
				particle.corners[i]=default_corner[i];
			}
		}
		//Verify point in correct quadrant, otherwise set to fixed corner
		if(particle.corners[0].x>(particle.getX()+(particle.getWidth()/2)) || particle.corners[0].y > (particle.getY()+(particle.getHeight()/2)))
		{
			particle.corners[0]=default_corner[0];
		}
		if(particle.corners[1].x<(particle.getX()+(particle.getWidth()/2)) || particle.corners[1].y > (particle.getY()+(particle.getHeight()/2)))
		{
			particle.corners[1]=default_corner[1];
		}
		if(particle.corners[2].x>(particle.getX()+(particle.getWidth()/2)) || particle.corners[2].y < (particle.getY()+(particle.getHeight()/2)))
		{
			particle.corners[2]=default_corner[2];
		}
		if(particle.corners[3].x<(particle.getX()+(particle.getWidth()/2)) || particle.corners[3].y < (particle.getY()+(particle.getHeight()/2)))
		{
			particle.corners[3]=default_corner[3];
		}
		width=(distance(particle.corners[0],particle.corners[1])+distance(particle.corners[2],particle.corners[3]))/2.0;
		height=(distance(particle.corners[0],particle.corners[2])+distance(particle.corners[1],particle.corners[3]))/2.0;
		particle.tLocation=new Point((particle.corners[0].x+particle.corners[1].x)/2,(particle.corners[0].y+particle.corners[1].y)/2);
		particle.setTWidth((int) width);
		particle.setTHeight((int) height);
		//particle.setAngle(Math.atan((particle.corners[2].y-particle.corners[3].y)/(1.0*particle.corners[2].x-particle.corners[3].x)));
		particle.setAngle(Math.atan((particle.corners[2].y-particle.corners[3].y)/(1.0*(particle.corners[3].x-particle.corners[2].x))));
		//particle.setAngle(Math.atan2(particle.corners[3].x-particle.corners[2].x,particle.corners[2].y-particle.corners[3].y));
		double ratio = (width * 1.0) / (height * 1.0) * 1.0;
		return new Score(ratio, ScoreType.EQUIV_RECT);
	}
	public Score coverage(Particle particle)
	{
		double ratio=particle.count/(particle.getTWidth()*particle.getTHeight()*1.0);
		return new Score(ratio, ScoreType.COVERAGE);
	}
	public Score profile(Particle particle)
	{
		Point[] corners=new Point[4];
		for(int i=0;i<corners.length;i++)
		{
			corners[i]=new Point(particle.corners[i].x, particle.corners[i].y);
			corners[i].translate(particle.x*-1, particle.y*-1);
		}
		double x1=((particle.corners[2].x-particle.corners[0].x)*1.0)/100.0;
		double y1=((corners[2].y-corners[0].y)*1.0)/100.0;
		double x2=((corners[3].x-corners[1].x)*1.0)/100.0;
		double y2=((corners[3].y-corners[1].y)*1.0)/100.0;
		double[] xprofile=new double[100];
		double[] yprofile=new double[100];
		//First y profile
		for(int i=0;i<100;i++)
		{
			Point p1=new Point((int)(corners[0].x+(i*x1)),(int)(corners[0].y+(i*y1)));
			Point p2=new Point((int)(corners[1].x+(i*x2)),(int)(corners[1].y+(i*y2)));
			double slope=(p1.y-p2.y)/(p1.x-p2.x*1.0);
			int count=0;
			int alive=0;
			for(int x=p1.x;x<p2.x;x++)
			{
				int y=(int) (slope*x)+p1.y;
				if(particle.localInMap(x, y))
				{
					if(particle.getLocalValue(x, y))
					{
						alive++;
					}
					count++;
				}
			}
			if(count==0)
			{
				yprofile[i]=alive/(1e-10);
			}
			else
			{
				yprofile[i]=alive/(count*1.0);
			}
		}
		x1=((corners[1].x-corners[0].x)*1.0)/100;
		y1=((corners[1].y-corners[0].y)*1.0)/100;
		x2=((corners[3].x-corners[2].x)*1.0)/100;
		y2=((corners[3].y-corners[2].y)*1.0)/100;
		//Next x profile
		for(int i=0;i<100;i++)
		{
			Point p1=new Point((int)(corners[0].x+(i*x1)),(int)(corners[0].y+(i*y1)));
			Point p2=new Point((int)(corners[2].x+(i*x2)),(int)(corners[2].y+(i*y2)));
			double slope=(p1.x-p2.x)/(p1.y-p2.y*1.0);
			int count=0;
			int alive=0;
			for(int y=p1.y;y<p2.y;y++)
			{
				int x=(int) (slope*y)+p1.x;
				if(particle.localInMap(x, y))
				{
					if(particle.getLocalValue(x, y))
					{
						alive++;
					}
					count++;
				}
			}
			if(count==0)
			{
				xprofile[i]=alive/(1e-10);
			}
			else
			{
				xprofile[i]=alive/(count*1.0);
			}
		}
		return new Score(xprofile, yprofile);
	}
	public Score moment(Particle particle)
	{
		Point centroid=new Point(0,0);
		long m20=0;
		long m02=0;
		long m11=0;
		long m10=0;
		long m01=0;
		int m00=0;
		for(int x=0;x<particle.getWidth();x++)
		{
			for(int y=0;y<particle.getHeight();y++)
			{
				if(particle.getLocalValue(x, y))
				{
					m20=m20+(x*x);
					m02=m02+(y*y);
					m11=m11+(x*y);
					m10=m10+(x);
					m01=m01+(y);
					centroid.x=centroid.x+x;
					centroid.y=centroid.y+y;
					m00++;
				}
			}
		}
		centroid.x=centroid.x/particle.count;
		centroid.y=centroid.y/particle.count;
		
		long u00=particle.count;
		long u11=m11-(centroid.x*m01);
		long u20=m20-(centroid.x*m10);
		long u02=m02-(centroid.y*m01);
		/*
		System.out.print("Central Moments:\nGot:\n");
		System.out.printf("u00: [%d]\tu11: [%d]\tu20: [%d] u02: [%d]\n",u00,u11,u20,u02);
		System.out.println("Calculated: ");
		System.out.printf("u00: [%d]\tu11: [%d]\tu20: [%d] u02: [%d]\n",Moment.centMoment(0, 0, particle, centroid),Moment.centMoment(1, 1, particle, centroid),Moment.centMoment(2, 0, particle, centroid),Moment.centMoment(0, 2, particle, centroid));
		u11=(long) Moment.centMoment(1, 1, particle, centroid);
		
		double up20=(m20/(particle.count*1.0))-(centroid.x*centroid.x);
		double up02=(m02/(particle.count*1.0))-(centroid.y*centroid.y);
		double up11=(m11/(particle.count*1.0))-(centroid.x*centroid.y);
		
		System.out.println("From Raw:");
		System.out.printf("u\'20: [%f]\tu\'02: [%f]\tu\'11: [%f]\n", up20, up02, up11);
		System.out.println("From Central:");
		System.out.printf("u\'20: [%d]\tu\'02: [%d]\tu\'11: [%d]\n", u20/u00, u02/u00, u11/u00);
		
		if(up20==up02)
		{
			up20=up20+1e-10;
		}
		double theta=0.5*Math.atan((2*up11)/(up20-up02));
		
		*/
		double momentOfInertia=Moment.moi(particle, centroid);
		System.out.printf("Moment of inertia: [%f]\n",momentOfInertia);
		//System.out.printf("Mx: [%f]\tMy: [%f]\t Md: [%f]\t Mz: [%f]\n",mx/(particle.count*1.0),my/(particle.count*1.0),md/(particle.count*1.0), mz/(particle.count*1.0));
		return new Score(momentOfInertia,ScoreType.MOMENT);
		//return null;
	}
	public void generateDistanceNumerator(int mapWidth)
	{
		double targetFeet=1.66;
		double FOVpixel=mapWidth*1.0;
		double tanTheta=Math.tan(1.185857);
		//distanceNumerator=(targetFeet*FOVpixel)/(2*tanTheta);
		distanceNumerator=1151;
		System.out.printf("Distance Numerator: [%f]\n",distanceNumerator);
	}
	public double findDistance(Particle particle)
	{
		return distanceNumerator/(particle.getTWidth()*1.0);
	}
	public Cell[] allSurroundLocal(int x, int y, Particle toCheck)
	{
		Cell[] surrounding=new Cell[8];
		//Top, clockwise
		for(byte i=0;i<allSurround.length;i++)
		{
			int dx=x+allSurround[i].x;
			int dy=y+allSurround[i].y;
			if(toCheck.localInMap(dx, dy))
			{
				if(toCheck.getLocalValue(dx, dy))
				{
					surrounding[i]=Cell.TRUE;
				}
				else
				{
					surrounding[i]=Cell.FALSE;
				}
			}
			else
			{
				surrounding[i]=Cell.NULL;
			}
		}
		return surrounding;
	}
	private Cell[] checkSurroundingLocal(int x, int y, Particle toCheck)
	{
		Cell[] surrounding=new Cell[4];
		//Top, right, bottom, left
		if(toCheck.localInMap(x, y+1))
		{
			if(toCheck.getLocalValue(x, y+1))
			{
				surrounding[0]=Cell.TRUE;
			}
			else
			{
				surrounding[0]=Cell.FALSE;
			}
		}
		else
		{
			surrounding[0]=Cell.NULL;
		}
		if(toCheck.localInMap(x+1, y))
		{
			if(toCheck.getLocalValue(x+1, y))
			{
				surrounding[1]=Cell.TRUE;
			}
			else
			{
				surrounding[1]=Cell.FALSE;
			}
		}
		else
		{
			surrounding[1]=Cell.NULL;
		}
		if(toCheck.localInMap(x, y-1))
		{
			if(toCheck.getLocalValue(x, y-1))
			{
				surrounding[2]=Cell.TRUE;
			}
			else
			{
				surrounding[2]=Cell.FALSE;
			}
		}
		else
		{
			surrounding[2]=Cell.NULL;
		}
		if(toCheck.localInMap(x-1, y))
		{
			if(toCheck.getLocalValue(x-1, y))
			{
				surrounding[3]=Cell.TRUE;
			}
			else
			{
				surrounding[3]=Cell.FALSE;
			}
		}
		else
		{
			surrounding[3]=Cell.NULL;
		}
		return surrounding;
	}
	public Cell[] checkSurroundingGlobal(int x, int y, Particle toCheck)
	{
		return checkSurroundingLocal(x-toCheck.x,y-toCheck.y,toCheck);
	}
	public Particle[] findContour(Particle particle)
	{
		Particle contour=new Particle(particle);
		//Shell, alive cells in shell represents dead cells around the particle, this avoids a contour with holes within
		Particle shell=new Particle(particle.x-1,particle.y-1,new boolean[particle.map.length+2][particle.map[0].length+2]);
		//Represents previous tiles that changed, used to determine which tiles to check for expansion;
		Particle expanded=new Particle(shell);
		//Starting "seed", guaranteed to be false in particle
		for(int i=0;i<shell.getWidth();i++)
		{
			shell.setLocalValue(i, 0, true);
			shell.setLocalValue(i, shell.getHeight()-1, true);
			expanded.setLocalValue(i, 0, true);
			expanded.setLocalValue(i, shell.getHeight()-1, true);
		}
		for(int i=0;i<shell.getHeight();i++)
		{
			shell.setLocalValue(0,i,true);
			shell.setLocalValue(shell.getWidth()-1,i,true);
			expanded.setLocalValue(0,i,true);
			expanded.setLocalValue(shell.getWidth()-1,i,true);
		}
		expanded.setLocalValue(0, 0, false);
		expanded.setLocalValue(0, expanded.getHeight()-1, false);
		expanded.setLocalValue(expanded.getWidth()-1,0,false);
		expanded.setLocalValue(expanded.getWidth()-1,expanded.getHeight()-1,false);
		boolean change=true;
		while(change)
		{
			//Represents expansion in this cycle
			Particle expanding=new Particle(shell);
			change=false;
			for(int i=0;i<expanded.getHeight();i++)
			{
				for(int j=0;j<expanded.getWidth();j++)
				{
					if(expanded.getLocalValue(j, i))
					{
						int x=j+expanded.x;
						int y=i+expanded.y;
						//Look at all surrounding tiles for alive squares
						Cell[] around=checkSurroundingGlobal(x,y,particle);
						if(around[0].equals(Cell.FALSE))
						{
							if(!shell.getGlobalValue(x, y+1))
							{
								change=true;
								expanding.setGlobalValue(x, y+1, true);
								shell.setGlobalValue(x, y+1, true);
							}
						}
						if(around[1].equals(Cell.FALSE))
						{
							if(!shell.getGlobalValue(x+1, y))
							{
								change=true;
								expanding.setGlobalValue(x+1, y, true);
								shell.setGlobalValue(x+1, y, true);
							}
						}
						if(around[2].equals(Cell.FALSE))
						{
							if(!shell.getGlobalValue(x, y-1))
							{
								change=true;
								expanding.setGlobalValue(x, y-1, true);
								shell.setGlobalValue(x, y-1, true);
							}
						}
						if(around[3].equals(Cell.FALSE))
						{
							if(!shell.getGlobalValue(x-1, y))
							{
								change=true;
								expanding.setGlobalValue(x-1, y, true);
								shell.setGlobalValue(x-1, y, true);
							}
						}
					}
				}
			}
			/*
			 * "I'm fifty percent sure this is useless and caused a bug, and I'm fifty percent sure that this is needed."
			 * --Best Programmer in the World
			 */
			
			/*
			//Clean up, avoid points already in shell
			for(int i=0;i<expanding.map.length;i++)
			{
				for(int j=0;j<expanding.map[0].length;j++)
				{
					if(expanding.getLocalValue(j, i))
					{
						if(shell.getLocalValue(j, i))
						{
							expanding.setLocalValue(j, i, false);
						}
					}
				}
			}
			*/
			expanded=expanding;
		}
		for(int x=0;x<shell.getWidth();x++)
		{
			for(int y=0;y<shell.getHeight();y++)
			{
				if(!shell.getLocalValue(x,y))
				{
					Cell[] around=checkSurroundingLocal(x,y,shell);
					boolean valid=false;
					for(Cell value:around)
					{
						if(value.equals(Cell.TRUE))
						{
							valid=true;
							break;
						}
					}
					if(valid)
					{
						contour.setGlobalValue((int)(x+shell.getX()), (int)(y+shell.getY()), true);
					}
				}
			}
		}
		Particle[] toReturn=new Particle[2];
		toReturn[0]=shell;
		toReturn[1]=contour;
		return toReturn;
	}
	public boolean[][] generateCircle(boolean[][] scan, int radius)
	{
		int x=0;
		int y = radius;
		int dp=1-radius;
		do
		{
			if(dp < 0)
			{
				dp = dp + 2 * (x++) + 3;
			}
			else
			{
				dp = dp + 2 * (x++) -2 * (y--) + 5;
			}
			
			scan[radius+y][radius+x]=true;
			scan[radius+y][radius-x]=true;
			scan[radius-y][radius+x]=true;
			scan[radius-y][radius-x]=true;
			scan[radius+x][radius+y]=true;
			scan[radius+x][radius-y]=true;
			scan[radius-x][radius+y]=true;
			scan[radius-x][radius-y]=true;
		}
		while (x < y);
		scan[radius][0]=true;
		scan[radius][scan[0].length-1]=true;
		scan[0][radius]=true;
		scan[scan[0].length-1][radius]=true;
		for(int i=0;i<scan.length;i++)
		{
			int start=-1;
			int end=0;
			for(int j=0;j<scan[0].length;j++)
			{
				if(scan[i][j])
				{
					if(start==-1)
					{
						start=j+0;
					}
					end=j+0;
				}
			}
			for(int k=start;k<end;k++)
			{
				scan[i][k]=true;
			}
		}
		return scan;
	}
	public static double distance(Point p, Point p2)
	{
		return Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
	}
	/*
	private int xyprofile(Particle particle)
	{
		int score = 200;
		double xHun = particle.getWidth() / 100.0;
		double yHun = particle.getHeight() / 100.0;
		double[] xProfile = new double[100];
		double[] yProfile = new double[100];
		int cellCount = 0;
		for (int x = 0; x < 100; x++)
		{
			for (int i = 0; i < Math.ceil(xHun); i++)
			{
				for (int j = 0; j < particle.getHeight(); j++)
				{
					if (particle.getLocalValue((int) ((x * xHun) + i), j))
					{
						xProfile[x]++;
					}
					cellCount++;
				}
			}
			xProfile[x] = xProfile[x] / (cellCount * 1.0);
			cellCount = 0;
		}
		for (int y = 0; y < 100; y++)
		{
			for (int j = 0; j < Math.ceil(yHun); j++)
			{
				for (int i = 0; i < particle.getWidth(); i++)
				{
					if (particle.getLocalValue(i, (int) ((y * yHun) + j)))
					{
						yProfile[y]++;
					}
					cellCount++;
				}
			}
			yProfile[y] = yProfile[y] / (cellCount * 1.0);
			cellCount = 0;
		}
		for (int i = 0; i < 100; i++)
		{
			if (xProfile[i] < profileMaxX[i] && xProfile[i] > profileMinX[i])
			{
				score--;
			}
			if (yProfile[i] < profileMaxY[i] && yProfile[i] > profileMinY[i])
			{
				score--;
			}
		}
		return score;
	}
	*/
	/*
	 * private int moment(Particle particle, int location) { int score=0; Point
	 * centerMass=COMasses[location]; double moment=0; for(int
	 * y=0;y<particle.getHeight();y++) { for(int x=0;x<particle.getWidth();x++)
	 * { if(particle.getLocalValue(x,y)) { moment=moment+(distance(new
	 * Point(x,y), centerMass)); } } } double mass=80.0/(particle.count*1.0);
	 * moment=moment*mass; double ratio=moment/(particle.count*1.0); double
	 * ideal=2.092030503536954;
	 * score=(int)(100.0*(Math.abs(ratio-ideal)/(ideal))); if(score>100) {
	 * score=100; } return score; }
	 */
	/*
	private Point centerMass(Particle particle, int location)
	{
		int xTotal = 0;
		int yTotal = 0;
		for (int y = 0; y < particle.getHeight(); y++)
		{
			for (int x = 0; x < particle.getWidth(); x++)
			{
				if (particle.getLocalValue(x, y))
				{
					xTotal = xTotal + x;
					yTotal = yTotal + y;
				}
			}
		}
		Point toReturn = new Point((int) (xTotal / (particle.count * 1.0)),
				(int) (yTotal / (particle.count * 1.0)));
		COMasses[location] = toReturn;
		return toReturn;
	}
	*/
	private int coverageArea(Particle particle)
	{
		int toReturn=0;
		double ratio=(particle.count*1.0)/((particle.getWidth()*particle.getHeight())*1.0);//Random 1.0's to ensure quotient is a double
		toReturn=(int)(100.0*(Math.abs(ratio-ideal)/(dev)));
		if(toReturn>100)
		{
			toReturn=100;
		}
		return toReturn;
	}

	public ArrayList<Particle> findParticles(boolean[][] map)// Generates rectangles for every point
	{
		long total=0;
		largeParticleIndex=-1;
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
					map[j][i]=false;
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
					if (particle.count >= minimumAlive)
					{
						//Special ratio check
						if(particle.getHeight()/particle.getWidth()<maxRatio)
						{
							if(particle.getWidth()/particle.getHeight()<maxRatio)
							{
								for(int k=largeParticleIndex;k<largeMinAlive.length;k++)
								{
									if(k>=0)
									{
										if(particle.count>largeMinAlive[k])
										{
											largeParticleIndex=k;
										}
										else
										{
											break;
										}
									}
								}
								toReturn.add(particle);
							}
						}
					}
				}
			}
		}
		if(largeParticleIndex>-1)
		{
			ArrayList<Particle> toRemove=new ArrayList<Particle>();
			for(Particle particle: toReturn)
			{
				if(particle.count<largeMinAlive[largeParticleIndex])
				{
					toRemove.add(particle);
				}
			}
			for(Particle particle: toRemove)
			{
				toReturn.remove(particle);
			}
		}
		System.out.println("Total \t"+total);
		return toReturn;
	}

	public static int[][][] getArray(BufferedImage image)
	{

		final byte[] pixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][][] result = new int[height][width][4];
		if (hasAlphaChannel)
		{
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
				/*
				 * int argb = 0; argb += (((int) pixels[pixel] & 0xff) << 24);
				 * // alpha argb += ((int) pixels[pixel + 1] & 0xff); // blue
				 * argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				 * argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				 */// Code from where I copy pas- *ahem* made myself
					// argb = (int) pixels[pixel + color];

				// Order goes in red, green, blue, alpha
				result[row][col][0] = (int) pixels[pixel + 3] & 0xff;// red
				result[row][col][1] = (int) pixels[pixel + 2] & 0xff;// green
				result[row][col][2] = (int) pixels[pixel + 1] & 0xff;// blue
				result[row][col][3] = (int) (pixels[pixel]) & 0xff;// alpha
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		} else
		{
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
				// int argb = 0;
				/*
				 * argb += -16777216; // 255 alpha argb += ((int) pixels[pixel]
				 * & 0xff); // blue argb += (((int) pixels[pixel + 1] & 0xff) <<
				 * 8); // green argb += (((int) pixels[pixel + 2] & 0xff) <<
				 * 16); // red
				 */
				// argb=(int) pixels[pixel+(color-1)];//code for specific color,
				// replace with rgb

				// Order goes in red, green, blue, alpha
				result[row][col][0] = (int) pixels[pixel + 2] & 0xff;// red
				result[row][col][1] = (int) pixels[pixel + 1] & 0xff;// green
				result[row][col][2] = (int) pixels[pixel] & 0xff;// blue
				result[row][col][3] = 255;// alpha
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		}

		return result;
	}

	public boolean inMap(boolean[][] map, int x, int y)
	{
		if (x < 0 || y < 0)
		{
			return false;
		}
		if (y < map.length && x < map[0].length)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static boolean[][] createMap(BufferedImage picture)// Because x & y are irrelevant here, do not bother changing i & j places in map array
	{
		int[][][] image = getArray(picture);
		boolean[][] map = new boolean[image.length][image[0].length];
		//map = useHsl(map, image, hmin, hmax, smin, lmin, lmax);
		map=useHsv(map, image);
		//map=advancedHSV(map, image);
		// LightHSL is experimental idea, more lenient towards pixels surrounded by alive cells
		// map=lightHsl(map,image, hmin, hmax, smin, lmin, lmax);
		return map;
	}
	private boolean[][] advancedHSV(boolean[][] origin, int[][][] image)
	{
		/*
		 * Looks through map, if there is a pixel within a 5 square or circle of an alive pixel,
		 * then it uses another test to determine if it is "alive"
		 */
		final int searchBoxSize=4;
		boolean[][] map = origin.clone();
		for (int X=0; X < image[0].length; X++)
		{
			for(int Y=0; Y < image.length; Y++)
			{
				if(!origin[Y][X])
				{
					boolean nearby=false;
					NearbySearch:
					for(int i=0;i<searchBoxSize;i++)
					{
						for (int j=0;j<searchBoxSize;j++)
						{
							int x=i+X-(searchBoxSize/2);
							int y=j+Y-(searchBoxSize/2);
							if(x>=0&&y>=0)
							{
								if(x<origin[0].length&&y<origin.length)
								{
									if(distance(new Point(x,y),new Point(X,Y))<=5)//Optional code, makes it a sphere
									{
										if(origin[y][x])
										{
											nearby=true;
											break NearbySearch;
										}
									}
								}
							}
						}
					}
					if(nearby)
					{
						int[] pixel=image[Y][X];
						final int bgMinimum=100;
						if(pixel[1]<bgMinimum||pixel[2]<bgMinimum)
						{
							nearby=false;
						}
						if(nearby)
						{
							if(((pixel[1]+pixel[2])/2)-pixel[0]<40)
							{
								nearby=false;
							}
						}
						if(nearby)
						{
							if(Math.abs(pixel[2]-pixel[1])<50)
							{
								map[Y][X]=true;
							}
						}
					}
				}
			}
		}
		return map;
	}
	public static boolean[][] useHsv(boolean[][] map, int[][][] image)
	{
		//Modified code from useHsl
		for (int i = 0; i < image.length; i++)
		{
			for (int j = 0; j < image[0].length; j++)
			{
				boolean valid = true;
				int red = image[i][j][0];
				int green = image[i][j][1];
				int blue = image[i][j][2];
				int[] hsv = getHSV(red, green, blue);
				int allowance=(int) ((-7.0/10.0)*hsv[1]+100);
				//allowance=Math.max(allowance, (hsv[2]-50)*10);
				allowance=Math.max(allowance, 15);
				//While loop stupid way to exit checker when done
				while(true)
				{
					if(Math.abs(hsv[0]-165)>allowance)
					{
						valid=false;
						break;
					}
					if(hsv[2]<30)
					{
						valid=false;
						break;
					}
					break;
				}
				map[i][j] = valid;
			}
		}
		return map;
	}
	public static boolean[][] useHsl(boolean[][] map, int[][][] image, int hmin, int hmax, int smin, int lmin, int lmax)
	{
		for (int i = 0; i < image.length; i++)// Converts array into map of reflected light, based on color threshold
		{
			for (int j = 0; j < image[0].length; j++)
			{
				boolean valid = false;
				// Place analysis per pixel here
				int red = image[i][j][0];
				int green = image[i][j][1];
				int blue = image[i][j][2];
				// Option 1: Basic Color Scan, compares value of determined color to predetermined threshold
				/*
				 * if(image[i][j][1]>=colorThreshold&&image[i][j][2]>=colorThreshold
				 * &&
				 * image[i][j][0]<=colorThreshold&&(image[i][j][2]+image[i][j][
				 * 1])/2.0-image[i][j][0]>colorDifference) { valid=true; }
				 */// Option 2: Replicates method from grip tutorial.
				int[] hsl = getHSL(red, green, blue);
				// Adjust values as necessary at top of class
				if (hsl[0] >= hmin && hsl[0] <= hmax)// Hue. Using nested if statements for clarity of reading. Don't change it.
				{
					if (hsl[1] >= smin)// Saturation. Notice that there is no max value detection as we are only reading for min. Change as needed.
					{
						if (hsl[2] >= lmin && hsl[2] <= lmax)// Luminance. All the other ones have long comments so I'm typing here too
						{
							valid = true;
						}
					}
				}
				// Not actually an option. Just code that everyone uses.
				map[i][j] = valid;
			}
		}
		return map;
	}
	//private boolean[][] lightHsl(boolean[][] map, int[][][] image, int hmin,int hmax,int smin,int lmin,int lmax){for(int i=0;i<image.length;i++){for(int j=0;j<image[0].length;j++){boolean adjacent=false;if(j>0){if(map[i][j-1]){adjacent=true;}}if(i>0){if(map[i-1][j]){adjacent=true;}}if(j+1<image[0].length){if(map[i][j+1]){adjacent=true;}}if(i+1<image.length){if(map[i+1][j]){adjacent=true;}}if(adjacent){boolean valid=false;int red=image[i][j][0];int green=image[i][j][1];int blue = image[i][j][2];int[] hsl=getHSL(red, green, blue);if(hsl[0]>=hmin&&hsl[0]<=hmax){if(hsl[1]>=smin){if(hsl[2]>=lmin&&hsl[2]<=lmax){valid=true;}}}map[i][j]=valid;}}}return map;}
	public static int[] getHSL(int red, int green, int blue)
	{
		int[] hsl=new int[3];//Self explanatory, array with elements in order of hue, saturation, and luminance
		//ranges noted below of hsl, inclusively
		double r=(red*1.0)/255.0;
		double g=(green*1.0)/255.0;
		double b=(blue*1.0)/255.0;
		double min=Math.min(r, g);
		min=Math.min(min, b);//Because math only compares 1 number at a time, repeated operation for blue as well
		double max=Math.max(r, g);
		max=Math.max(max, b);
		hsl[2]=(int) (((1.0*(min+max))/2.0)*255);//Luminance Calculation 0-255
		if(min==max)//Certain circumstance for saturation
		{
			hsl[1]=0;
			hsl[0]=0;//Hue as well to avoid dividing by zero;
		}
		else
		{
			//Magical saturation equation, don't ask questions unless I'm wrong, which i'm not, cause
			//http://www.niwa.nu/2013/05/math-behind-colorspace-conversions-rgb-hsl/ says so
			if(hsl[2]<128)
			{
				hsl[1]=(int) ((((max-min)*1.0)/((max+min)*1.0))*255.0);
			}
			else
			{
				hsl[1]=(int) (((max-min)/(2.0-max-min))*255.0);
			}
			//Finally, the hue calculation
			if(r==max)
			{
				hsl[0]=(int) (((g-b)/(max-min))*30.0);
			}
			else
			{
				if(g==max)
				{
					hsl[0]=(int) ((2.0+((b-r)/(max-min)))*30.0);
				}
				else
				{
					//b is max
					hsl[0]=(int) ((4.0+((r-g)/(max-min)))*30.0);
				}
			}
		}
		return hsl;
	}
	public static int[] getHSV(int red, int green, int blue)
	{
		//Calculations based of this website http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
		int[] hsv=new int[3];
		RGB maxType=RGB.RED;
		double r=(red*1.0)/255.0;
		double g=(green*1.0)/255.0;
		double b=(blue*1.0)/255.0;
		double max=r;
		if(g>max)
		{
			max=g;
			maxType=RGB.GREEN;
		}
		if(b>max)
		{
			max=b;
			maxType=RGB.BLUE;
		}
		double min=Math.min(Math.min(r,g), b);
		double delta=max-min;
		if(delta==0)
		{
			hsv[0]=0;
		}
		else
		{
			switch(maxType)
			{
				case RED:
					hsv[0]=(int) (60.0*(((g-b)/delta)%6));
					break;
				case GREEN:
					hsv[0]=(int) (60.0*(((b-r)/delta)+2));
					break;
				case BLUE:
					hsv[0]=(int) (60.0*(((r-g)/delta)+4));
					break;
				default:
					assert false;//OOH! Fancy Keywords! But realisticly, if it gets here, the program is messed up. A lot.
			}
		}
		if(max==0)
		{
			hsv[1]=0;
		}
		else
		{
			hsv[1]=(int) Math.round(100.0*delta/max);
		}
		hsv[2]=(int) Math.round(100.0*max);
		return hsv;
	}
	public static boolean[][] copyOf(boolean[][] original) 
	{
		boolean[][] copy = new boolean[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        copy[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return copy;
	}
	public static double gauss(double x, double sd, double u)
	{
		return (1/Math.sqrt(2*sd*sd*Math.PI))*Math.pow(Math.E, -1*(x-u)*(x-u)/(2*sd*sd));
	}
}