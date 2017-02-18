package edgedetector;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import code2017.Particle;
import code2017.Vision17;

public class EdgeDetector
{
	double[][] map=null;
	Tasker[] taskers;
	Thread[] threads;
	Rectangle[] regions=null;
	private boolean initialized;
	private ArrayList<Particle> edges=null;
	private double[][] mag=null;
	public EdgeDetector(int threads)
	{
		this.threads=new Thread[threads];
		initialized=false;
	}
	public void init(double[][] map)
	{
		this.map=map;
		regions=Conv.divideRegions(map, threads.length);
		initialized=true;
	}
	public void exec()
	{
		if(!initialized)
		{
			System.out.println("WARNING: EdgeDetector not initialized. Imminent errors.");
		}
		prepareThreads(Assignment.SMOOTHEN, map);
		runThreads();
		waitForThreads();
		double[][] map_smoothed=getResult();
		
		prepareThreads(Assignment.XDERIV, map_smoothed);
		runThreads();
		waitForThreads();
		double[][] dx=getResult();
		
		prepareThreads(Assignment.YDERIV, map_smoothed);
		runThreads();
		waitForThreads();
		double[][] dy=getResult();
		
		prepareThreads(Assignment.MAGNITUDE, dx, dy);
		runThreads();
		waitForThreads();
		mag=getResult();
		
		ArrayList<Particle> edges=EdgeAlgorithm.findEdges(mag);
		//edges=EdgeAlgorithm.thinEdge(edges);
		edges=EdgeThinner.thinEdge(edges, mag, dx, dy);
		this.edges=edges;
	}
	private void prepareThreads(Assignment assignment, double[][] input)
	{
		prepareThreads(assignment, input, null);
	}
	private void prepareThreads(Assignment assignment, double[][] input, double[][] input2)
	{
		taskers=new Tasker[threads.length];
		for(int i=0;i<threads.length;i++)
		{
			taskers[i]=new Tasker(assignment, regions[i]);
			taskers[i].input=input;
			taskers[i].input2=input2;
			threads[i]= new Thread(taskers[i]);
		}
	}
	private void runThreads()
	{
		for(int i=0;i<threads.length;i++)
		{
			threads[i].run();
		}
	}
	private void waitForThreads()
	{
		for(int i=0;i<threads.length;i++)
		{
			try
			{
				threads[i].join();
			} 
			catch (InterruptedException e)
			{
				System.err.println("Thread interrupted, result likely invalid.");
				e.printStackTrace();
			}
		}
		for(Thread thread:threads)
		{
			if(thread.isAlive())
			{
				System.out.println("Dun goofed");
			}
		}
	}
	private double[][] getResult()
	{
		double[][][] results=new double[threads.length][][];
		for(int i=0;i<threads.length;i++)
		{
			results[i]=taskers[i].result;
		}
		return Conv.combineResults(results);
	}
	public ArrayList<Particle> getEdges()
	{
		return edges;
	}
	public double[][] getMag()
	{
		return mag;
	}
}
