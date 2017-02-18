package edgedetector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import keyboard.KeyboardInput;
import code2017.Particle;
import code2017.Target;
import code2017.Vision17;

public class EdgeDisplayer extends JFrame implements Runnable
{
	private static final long serialVersionUID = 8125498152122081866L;
	public final static Color[] COLOR_PALETTE=
		{
			new Color(0xED0A3F),
			new Color(0xFF3F34),
			new Color(0xFF861F),
			new Color(0xFBE870),
			new Color(0xC5E17A),
			new Color(0x01A368),
			new Color(0x76D7EA),
			new Color(0x0066FF),
			new Color(0x8359A3),
			new Color(0xAF593E),
			new Color(0x03BB85),
			new Color(0xFFDF00),
			new Color(0x8B8680),
			new Color(0x0A6B0D),
			new Color(0x8FD8D8),
			new Color(0xF653A6),
			new Color(0xCA3435),
			new Color(0xFFCBA4),
			new Color(0xCD9A9E),
			new Color(0xFA9D5A),
			new Color(0xA36F40),
			new Color(0xFFAE42)
		};
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
	private final double wMult=2;
	private final double hMult=2;
	private ArrayList<Particle> edges=null;
	private double[][] map=null;
	private EdgeDetector ed=null;
	private double[][] mag=null;
	public EdgeDisplayer(BufferedImage image)
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
				
				ed=new EdgeDetector(4);
				map=Conv.generateDoubleMap(image);
				ed.init(map);
				ed.exec();
				edges=ed.getEdges();
				mag=ed.getMag();
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
		g.setColor(new Color(0xAAAAAA));
		g.fillRect(0, image.getHeight()+1, image.getWidth()*2, image.getHeight());
		if(map!=null)
		{
			for(int i=0;i<map.length;i++)
			{
				for(int j=0;j<map[0].length;j++)
				{
					int strength=(int)(map[i][j]*255.0);
					g.setColor(new Color(strength, strength, strength));
					g.fillRect(j, i, 1, 1);
				}
			}
		}
		if(edges!=null)
		{
			for(Particle edge: edges)
			{
				g.setColor(COLOR_PALETTE[edge.count%COLOR_PALETTE.length]);
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
		if(mag!=null)
		{
			final double MAG_STRENGTH=	0.03;
			final double CONST_SECOND= 50.0;
			for(int i=0;i<mag.length;i++)
			{
				for(int j=0;j<mag[0].length;j++)
				{
					int strength=(int)(Math.log(mag[i][j]*MAG_STRENGTH)*CONST_SECOND);
					strength=Math.min(strength, 255);
					strength=Math.max(strength, 0);
					g.setColor(new Color(strength, strength, strength));
					g.fillRect(j+image.getWidth(), i+image.getHeight(), 1, 1);
				}
			}
		}
		g.setColor(Color.YELLOW);
		
		g.drawImage(image, image.getWidth(), 0, null);
		
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
