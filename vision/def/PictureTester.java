package def;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import algorithm.ParticleFinder;
import visionCore.Particle;
import visionCore.Vision;
import keyboard.KeyboardInput;

public class PictureTester extends JFrame implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6265417222272758163L;
	//Size of Target Marker
	final int radius=4;
	//Controls
	final int right=KeyEvent.VK_RIGHT;
	final int left=KeyEvent.VK_LEFT;
	final int a=KeyEvent.VK_A;
	final int d=KeyEvent.VK_D;
	//End Controls
	private BufferedImage image;
	private Insets inset;
	private KeyboardInput keyboard=new KeyboardInput();
	private Thread t;
	private int imageQ=0;
	private boolean processImage=true;
	private Vision v;
	private boolean[][] map;
	private Point target=new Point(-1,-1);
	private Particle particle=null;
	private final double wMult=2;
	private final double hMult=2;
	private ArrayList<Point> corners=new ArrayList<Point>();
	public PictureTester(BufferedImage image)
	{
		super();
		t=new Thread(this);
		this.image=image;
		pack();
		setSize((int)(image.getWidth()*wMult)+getInsets().left+getInsets().right,(int)(image.getHeight()*hMult)+getInsets().bottom+getInsets().top);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(keyboard);
		setVisible(true);
		t.start();
		this.inset=getInsets();
	}
	private void mainCycle()
	{
		long time;
		while(true)
		{
			time=System.currentTimeMillis();
			if(processImage)
			{
				processImage=false;
				
				v=new Vision();
				map=v.createMap(image);
				double[] target=v.process(image);
				target[0]=(target[0]+1)*(image.getWidth()/2.0);
				target[1]=-1.0*(target[1]-1)*(image.getHeight()/2.0);
				this.target=new Point((int)target[0],(int)target[1]);
				if(v.bestParticle!=null)
				{
					particle=v.bestParticle;
					System.out.printf("Angle: [%f]\n",Math.toDegrees(particle.getAngle()));
				}
				corners=v.corners_display;
				System.out.println("Processed");
			}
			keyboard.updateKeys();
			if(keyboard.keyOnce(d)||keyboard.keyOnce(d))
			{
				imageQ++;
			}
			if(keyboard.keyOnce(a)||keyboard.keyOnce(a))
			{
				imageQ--;
			}
			repaint();
			time=50-(System.currentTimeMillis()-time);
			if(time<0)
			{
				time=0;
			}
			try
			{
				Thread.sleep(time);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void paint(Graphics frameG)
	{
		BufferedImage picture=new BufferedImage((int)(image.getWidth()*wMult),(int)(image.getHeight()*hMult), BufferedImage.TYPE_INT_RGB);
		Graphics g=picture.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, image.getHeight()+1, image.getWidth()*2, image.getHeight());
		g.setColor(Color.YELLOW);
		if(map!=null)
		{
			for(int i=0;i<image.getHeight();i++)
			{
				for(int j=0;j<image.getWidth();j++)
				{
					if(map[i][j])
					{
						g.fillRect(j, i, 1, 1);
					}
				}
			}
		}
		if(particle!=null)
		{
			g.setColor(Color.RED);
			for(Point corner: particle.corners)
			{
				//g.fillRect(corner.x+particle.x, corner.y+particle.y, 2, 2);
				g.drawLine(target.x, target.y,corner.x, corner.y);
			}
			//Draws line of angle in above/below quadrant
			g.setColor(Color.GREEN);
			Point center=new Point(image.getWidth()/4,image.getHeight()/4);
			Point p1=new Point(0,center.y+(int)(center.x*Math.tan(particle.getAngle())));
			Point p2=new Point(image.getWidth()/2,center.y-(int)(center.x*Math.tan(particle.getAngle())));
			if(particle.x>image.getWidth()/2)
			{
				//Right side of image
				if(particle.y>image.getHeight()/2)
				{
					//Bottom
					g.drawLine(p1.x+image.getWidth()/2, p1.y, p2.x+image.getWidth()/2, p2.y);
				}
				else
				{
					//Top
					g.drawLine(p1.x+image.getWidth()/2, p1.y+image.getHeight()/2, p2.x+image.getWidth()/2, p2.y+image.getHeight()/2);
				}
			}
			else
			{
				//Left side
				if(particle.y>image.getHeight()/2)
				{
					//Bottom
					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
				else
				{
					//Top
					g.drawLine(p1.x, p1.y+image.getHeight()/2, p2.x, p2.y+image.getHeight()/2);
				}
			}
		}
		g.setColor(Color.CYAN);
		g.fillRect(target.x-(radius/2), target.y-(radius/2), radius, radius);
		g.drawImage(image, image.getWidth(), 0, null);
		g.fillRect(target.x+image.getWidth()-(radius/2), target.y-(radius/2), radius, radius);
		g.setColor(Color.RED);
		final int cross=3;
		for(Point p: corners)
		{
			g.fillRect(p.x-cross+image.getWidth(), p.y,(cross*2)+1, 1);
			g.fillRect(p.x+image.getWidth(), p.y-cross, 1, (cross*2)+1);
		}
		
		frameG.drawImage(picture, inset.left, inset.top, null);
	}
	public void setImage(BufferedImage image)
	{
		this.image=image;
		processImage=true;
	}
	public int getImageQueue()
	{
		int toReturn=imageQ;
		imageQ=0;
		return toReturn;
	}
	@Override
	public void run()
	{
		mainCycle();
		
	}
}
