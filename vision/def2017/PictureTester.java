package def2017;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.ArrayList;

import javax.swing.*;

import code2017.Particle;
import code2017.Target;
import code2017.Vision17;
import edgedetector.EdgeDisplayer;
import keyboard.KeyboardInput;

public class PictureTester extends JFrame implements Runnable
{
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
	private int[][][] rgb=null;
	private Insets inset;
	private KeyboardInput keyboard=new KeyboardInput();
	private Thread t;
	private int imageQ=0;
	private boolean processImage=false;
	private Vision17 v;
	private boolean[][] map;
	private ArrayList<Particle> edges=null;
	private ArrayList<Particle> particles=null;
	private Point target=new Point(-1,-1);
	private Particle particle=null;
	private Particle pair=null;
	private final double wMult=3;
	private final double hMult=2;
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
				
				v=new Vision17();
				v.setImage(null, image);
				Target target=v.exec();
				//map=v.map;
				this.edges=v.edges;
				this.particles=v.particles;
				this.target=target.getPixelPoint(image.getWidth(), image.getHeight());
				this.particle=v.particle;
				this.pair=v.pair;
				this.rgb=v.rgb;
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
			g.setColor(Color.WHITE);
			for(int x=0;x<particle.getWidth();x++)
			{
				for(int y=0;y<particle.getHeight();y++)
				{
					if(particle.getLocalValue(x, y))
					{
						g.fillRect(x+particle.x, y+particle.y, 1, 1);
					}
				}
			}
		}
		if(pair!=null)
		{
			g.setColor(Color.GREEN);
			for(int x=0;x<pair.getWidth();x++)
			{
				for(int y=0;y<pair.getHeight();y++)
				{
					if(pair.getLocalValue(x, y))
					{
						g.fillRect(x+pair.x, y+pair.y, 1, 1);
					}
				}
			}
		}
		if(particle!=null && false)
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

		if(edges!=null)
		{
			for(Particle edge: edges)
			{
				g.setColor(EdgeDisplayer.COLOR_PALETTE[edge.count%EdgeDisplayer.COLOR_PALETTE.length]);
				for(int x=0;x<edge.getWidth();x++)
				{
					for(int y=0;y<edge.getHeight();y++)
					{
						if(edge.getLocalValue(x, y))
						{
							int x1=(int) (x+edge.getX());
							int y1=(int) (y+edge.getY());
							
							g.fillRect(x1, y1+image.getHeight(), 1, 1);
						}
					}
				}
			}
		}
		if(particles!=null)
		{
			for(Particle particle: particles)
			{
				g.setColor(EdgeDisplayer.COLOR_PALETTE[particle.count%EdgeDisplayer.COLOR_PALETTE.length]);
				for(int x=0;x<particle.getWidth();x++)
				{
					for(int y=0;y<particle.getHeight();y++)
					{
						if(particle.getLocalValue(x, y))
						{
							int x1=(int) (x+particle.getX());
							int y1=(int) (y+particle.getY());
							
							g.fillRect(x1+image.getWidth(), y1+image.getHeight(), 1, 1);
						}
					}
				}
			}
		}
		if(rgb!=null)
		{
			for(int i=0;i<rgb.length;i++)
			{
				for(int j=0;j<rgb[0].length;j++)
				{
					g.setColor(new Color(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]));
					g.fillRect(j+image.getWidth(), i, 1, 1);
				}
			}
		}
		g.setColor(Color.CYAN);
		g.fillRect(target.x-(radius/2), target.y-(radius/2), radius, radius);
		g.drawImage(image, image.getWidth()*2, 0, null);
		g.fillRect(target.x+image.getWidth()-(radius/2), target.y-(radius/2), radius, radius);
		
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
