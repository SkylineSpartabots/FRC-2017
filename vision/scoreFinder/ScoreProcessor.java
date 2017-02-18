package scoreFinder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import newVision.Vision2;
import algorithm.FeatureDetector;
import visionCore.*;
import keyboard.KeyboardInput;
import def.PictureController;

public class ScoreProcessor implements MouseListener, Runnable
{
	//PATH used to save multiple files of scores into
	static final String PATH="C:\\Users\\Twangybeast\\Downloads\\RealFullField\\Data";
	static final int frameDelay=20;
	boolean dragging=false;
	Point s1=null;
	Point s2=null;
	Rectangle selector=null;
	ScoreFrame sf;
	int imageNumber=0;
	BufferedImage image=null;
	KeyboardInput keyboard=new KeyboardInput();
	int imQ=0;
	boolean processed=false;
	Particle map=null;
	boolean[][] fullMap=null;
	Vision v=new Vision();
	Score[] score=null;
	public void start()
	{
		image=PictureController.getImage(PictureController.images[imageNumber]);
		fullMap=v.createMap(image);
		sf=new ScoreFrame(new Dimension(image.getWidth(),image.getHeight()), keyboard, this);
		sf.setImage(image);
		Thread t=new Thread(this);
		t.start();
		loadScores();
		while(true)
		{
			long t1=System.currentTimeMillis();
			
			execute();
			
			
			
			
			
			int waitTime=(int) (frameDelay-(System.currentTimeMillis()-t1));
			if(waitTime>0)
			{
				try
				{
					Thread.sleep(waitTime);
				} catch (InterruptedException e)
				{
					System.err.println("Sleep interrupted. Wat.");
				}
			}
		}
	}
	public void execute()
	{
		if(imQ!=0)
		{
			int toAdd=imQ;
			imQ=imQ-toAdd;
			imageNumber=imageNumber+toAdd;
			limitIN();
			image=PictureController.getImage(PictureController.images[imageNumber]);
			fullMap=v.createMap(image);
			sf.setImage(image);
			selector=null;
			score=null;
			loadScores();
		}
		if(dragging)
		{
			calculateSelector(MouseInfo.getPointerInfo().getLocation());
		}
		if(selector!=null)
		{
			sf.setSelector(selector);
		}
		if(selected()&&!processed)
		{
			processed=true;
			process();
		}
		if(sf.isConfirmed())
		{
			saveScores();
		}
		sf.repaint();
	}
	private void loadScores()
	{
		readScores();
		if(score!=null)
		{
			System.out.printf("Particle Score Values:\n\tEquivalent Rectangle: [%f]\n\tCoverage Area: [%f]\n\tMoment: [%f]\n", score[0].ratio, score[1].ratio, score[2].ratio);
			sf.setProfiles(score[3].xprofile, score[3].yprofile);
		}
	}
	public void saveScores()
	{
		/*
		 * Information to save
		 * 	Image Number
		 * 	Selector info
		 * 	Scores
		 */
		int imagenum=this.imageNumber;//Used in file name
		short selectorx=(short)(selector.getX());//			0002
		short selectory=(short)(selector.getY());//			0002
		short selectorw=(short)(selector.getWidth());//		0002
		short selectorh=(short)(selector.getHeight());//	0002
											//Sub Total 1:	0008
	
		double[] scores=new double[3];//					0024
		double[] xprof=new double[100];//					0800
		double[] yprof=new double[100];//					0800
											//Sub Total 2:	1624
										
											//Total:		1632
		byte[] bytes=new byte[1632];
		//Store the scores
		for(int i=0;i<scores.length;i++)
		{
			scores[i]=score[i].ratio;
		}
		//Store the profiles
		xprof=score[scores.length].xprofile;
		yprof=score[scores.length].yprofile;
		
		//Stores the info into the byte array
		ByteBuffer bb=ByteBuffer.wrap(bytes);
		bb.putShort(selectorx);
		bb.putShort(selectory);
		bb.putShort(selectorw);
		bb.putShort(selectorh);
		for(int i=0;i<scores.length;i++)
		{
			bb.putDouble(scores[i]);
		}
		for(int i=0;i<xprof.length;i++)
		{
			bb.putDouble(xprof[i]);
		}
		for(int i=0;i<yprof.length;i++)
		{
			bb.putDouble(yprof[i]);
		}
		File file=new File(PATH);
		if(!file.exists())
		{
 			System.out.println("Error in PATH. Please adjust code. Not Saving...");
			return;
		}
		file=new File(PATH+"\\"+imagenum+".score");
		if(file.exists())
		{
			System.out.println("File Already Exists. Overriding...");
		}
		else
		{
			System.out.println("File does NOT exit. Creating new file...");
		}
		try
		{
			file.createNewFile();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		FileOutputStream out=null;
		try
		{
			out=new FileOutputStream(file);
			//Writes bytes to file
			out.write(bytes);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{ 
			try
			{
				out.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Save Complete");
	}
	public void readScores()
	{
		File file=new File(PATH+"\\"+imageNumber+".score");
		if(!file.exists())
		{
			System.out.println("File Does Not Exist. Not attempting to read file...");
			return;
		}
		FileInputStream in=null;
		try
		{
			in=new FileInputStream(file);
			byte[] bytes=new byte[1632];
			if(in.read(bytes)!=bytes.length)
			{
				System.out.println("File possibly corrupt. Not enough bytes provided. Not attempting to read bytes...");
				return;
			}
			ByteBuffer bb=ByteBuffer.wrap(bytes);
			short selectorx=bb.getShort();
			short selectory=bb.getShort();
			short selectorw=bb.getShort();
			short selectorh=bb.getShort();
			double[] scores=new double[3];
			for(int i=0;i<scores.length;i++)
			{
				scores[i]=bb.getDouble();
			}
			double[] xprof=new double[100];
			for(int i=0;i<xprof.length;i++)
			{
				xprof[i]=bb.getDouble();
			}
			double[] yprof=new double[100];
			for(int i=0;i<yprof.length;i++)
			{
				yprof[i]=bb.getDouble();
			}
			selector=new Rectangle(selectorx,selectory,selectorw,selectorh);
			score=new Score[4];
			score[0]=new Score(scores[0], ScoreType.EQUIV_RECT);
			score[1]=new Score(scores[1], ScoreType.COVERAGE);
			score[2]=new Score(scores[2], ScoreType.MOMENT);
			score[3]=new Score(xprof,yprof);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public static byte[] doubleToByte(double d)
	{
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(d);
		return bytes;
	}
	public static double byteToDouble(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).getDouble();
	}
	public static byte[] shortToByte(short s)
	{
		byte[] bytes=new byte[2];
		ByteBuffer.wrap(bytes).putShort(s);
		return bytes;
	}
	public static short byteToShort(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).getShort();
	}
	public void process()
	{
		System.out.printf("Width: [%d]\t Height: [%d]\n",selector.width,selector.height);
		map=new Particle(selector.x,selector.y,new boolean[(int) selector.getHeight()][(int) selector.getWidth()]);
		for(int i=0;i<map.map.length;i++)
		{
			System.arraycopy(fullMap[i+selector.y], selector.x, map.map[i], 0, (int) selector.getWidth());
		}
		sf.setMap(map);
		ArrayList<Particle> particles=v.findParticles(Vision.copyOf(map.map));
		Particle particle=null;
		if(particles.size()!=0)
		{
			int best=0;
			for(Particle p: particles)
			{
				if(p.count>best)
				{
					particle=p;
				}
			}
			FeatureDetector fd=new FeatureDetector(4);
			Vision2 v2=new Vision2();
			Thread t=new Thread(fd);
			fd.setFindCorners(true);
			double[][] grayScaleFull=v2.createMap(image);
			double[][] grayScale=new double[(int) selector.getHeight()][(int) selector.getWidth()];
			for(int i=0;i<selector.getHeight();i++)
			{
				System.arraycopy(grayScaleFull[i+selector.y], selector.x, grayScale[i], 0, (int) selector.getWidth());
			}
			fd.setMap(grayScale);
			t.start();
			try
			{
				t.join();
			} 
			catch (InterruptedException e)
			{
				System.err.println("Thread interrupted. Results likely invalid.");
				e.printStackTrace();
			}
			score=new Score[4];
			score[0]=v.equivRect(particle, fd.corners);
			score[1]=v.coverage(particle);
			score[2]=v.moment(particle);
			score[3]=v.profile(particle);
			System.out.printf("Particle Score Values:\n\tEquivalent Rectangle: [%f]\n\tCoverage Area: [%f]\n\tMoment: [%f]\n", score[0].ratio, score[1].ratio, score[2].ratio);
			System.out.printf("\tCount: [%d]\n", particle.count);
			sf.setProfiles(score[3].xprofile, score[3].yprofile);
		}
	}
	public void limitIN()
	{
		while(imageNumber<0)
		{
			imageNumber=imageNumber+(PictureController.images.length);
		}
		while(imageNumber>=PictureController.images.length)
		{
			imageNumber=imageNumber-(PictureController.images.length);
		}
	}
	public void calculateSelector(Point s2)
	{
		Point d1=new Point(s1.x,s1.y);
		d1.translate(-1*sf.getInsets().left, -1*sf.getInsets().top);
		s2.translate(-1*sf.getInsets().left, -1*sf.getInsets().top);
		int width=1+Math.abs(d1.x-s2.x);
		int height=1+Math.abs(d1.y-s2.y);
		selector=new Rectangle(Math.min(d1.x, s2.x),Math.min(d1.y, s2.y),width,height);
		if(selector.intersects(new Rectangle(0,0,image.getWidth(),image.getHeight())))
		{
			selector=selector.intersection(new Rectangle(0,0,image.getWidth(),image.getHeight()));
		}
		else
		{
			selector=null;
		}
	}
	public boolean selected()
	{
		if(selector==null)
		{
			return false;
		}
		if(dragging)
		{
			return false;
		}
		return true;
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(new Rectangle(sf.getInsets().left,sf.getInsets().top,image.getWidth(),image.getHeight()).contains(e.getPoint()))
		{
			if(!dragging)
			{
				dragging=true;
				s1=e.getPoint();
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(dragging)
		{
			dragging=false;
			processed=false;
			s2=e.getPoint();
			if(sf.isVisible())
			{
				calculateSelector(s2);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run()
	{
		while(true)
		{
			keyboard.updateKeys();
			if(keyboard.keyOnce(KeyEvent.VK_A)||keyboard.keyOnce(KeyEvent.VK_LEFT))
			{
				imQ--;
			}
			if(keyboard.keyOnce(KeyEvent.VK_D)||keyboard.keyOnce(KeyEvent.VK_RIGHT))
			{
				imQ++;
			}
			try
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
