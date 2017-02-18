package scoreFinder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.*;

import visionCore.Particle;
import keyboard.*;

public class ScoreFrame extends JFrame implements ActionListener
{
	KeyboardInput keyboard;
	static final float wMult=2;
	static final float hMult=1f;
	private Dimension image;
	private BufferedImage im=null;
	private Rectangle selector=null;
	private Particle map=null;
	private JButton confirm;
	private boolean confirmed=false;
	private double[] xprofile=null;
	private double[] yprofile=null;
	final float multiplier=2.0f;
	private Point prev;
	public ScoreFrame(Dimension image, KeyboardInput keyboard, MouseListener ml)
	{
		super();
		Dimension confirmD=new Dimension((int) (image.getWidth()*2),50);
		setLayout(null);
		pack();
		this.image=image;
		this.keyboard=keyboard;
		setSize((int)(image.getWidth()*wMult)+getInsets().left+getInsets().right,(int) (confirmD.height+(multiplier*100)+(image.getHeight()*hMult)+getInsets().bottom+getInsets().top));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(keyboard);
		addMouseListener(ml);
		confirm=new JButton("Save");
		confirm.setActionCommand("confirm");
		confirm.setVerticalTextPosition(AbstractButton.CENTER);
		confirm.setHorizontalTextPosition(AbstractButton.LEADING);
		confirm.setLocation(0, (int) image.getHeight());
		confirm.setPreferredSize(new Dimension(20, 42));
		confirm.addActionListener(this);
		confirm.setEnabled(false);
		confirm.setBounds(0, (int)(image.getHeight()+(multiplier*100)), confirmD.width, confirmD.height);
		confirm.setFocusable(false);
		add(confirm);
		setVisible(true);
	}
	public void paint(Graphics graphics)
	{
		BufferedImage canvas=new BufferedImage((int)(image.getWidth()*wMult),(int)((multiplier*100)+(image.getHeight()*hMult)), BufferedImage.TYPE_INT_RGB);
		Graphics g=canvas.getGraphics();
		if(im!=null)
		{
			g.drawImage(im, 0, 0, null);
		}
		if(map!=null)
		{
			g.setColor(Color.YELLOW);
			for(int x=0;x<map.getWidth();x++)
			{
				for(int y=0;y<map.getHeight();y++)
				{
					if(map.getLocalValue(x, y))
					{
						g.fillRect((int)(x+map.getX()+image.getWidth()), (int)(y+map.getY()), 1, 1);
					}
				}
			}
		}
		if(selector!=null)
		{
			g.setColor(Color.RED);
			g.drawRect(selector.x, selector.y, (int)selector.getWidth(), (int)selector.getHeight());
		}
		if(xprofile!=null && yprofile != null)
		{
			BufferedImage profiles=new BufferedImage(200,100, BufferedImage.TYPE_INT_RGB);
			Graphics gp=profiles.getGraphics();
			int stripeWidth=5;
			for(int i=0;i<100/stripeWidth;i++)
			{
				if(i%2==0)
				{
					gp.setColor(Color.WHITE);
				}
				else
				{
					gp.setColor(new Color(200, 200, 200));
				}
				gp.fillRect(i*stripeWidth,0, stripeWidth, profiles.getHeight());
				gp.fillRect(100,i*stripeWidth, profiles.getWidth(), stripeWidth);
			}
			gp.setColor(Color.BLUE);
			for(int i=0;i<100;i++)
			{
				gp.fillRect(i, (int) (100-(xprofile[i]*100)), 1, 1);
			}
			gp.setColor(Color.GREEN);
			for(int i=0;i<100;i++)
			{
				gp.fillRect((int)(Math.max(0,(yprofile[i]*100)-1)+100), i, 1, 1);
			}
			BufferedImage scaledp=new BufferedImage((int)(profiles.getWidth()*multiplier),(int)(profiles.getHeight()*multiplier),profiles.getType());
			AffineTransform at=new AffineTransform();
			at.scale(multiplier, multiplier);
			AffineTransformOp op=new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			op.filter(profiles, scaledp);
			g.drawImage(scaledp, 0, (int) image.getHeight(), null);
		}
		Point mouse=getMousePosition();
		if(mouse==null)
		{
			mouse=prev;
		}
		else
		{
			mouse.translate(-1*getInsets().left, -1*getInsets().top);
		}
		//mouse.translate(getInsets().left, getInsets().right);
		if(mouse!=null)
		{
			mouse.x=(int) Math.max(0,Math.min(image.getWidth(), mouse.x));
			mouse.y=(int) Math.max(0,Math.min(image.getHeight(), mouse.y));
			g.setColor(Color.ORANGE);
			g.fillRect(0, mouse.y, image.width, 1);
			g.fillRect(mouse.x, 0, 1, image.height);
			graphics.drawImage(canvas, getInsets().left, getInsets().top, null);
			prev=new Point((int)(mouse.getX()),(int)(mouse.getY()));
		}
		confirm.repaint();
	}
	public void setImage(BufferedImage im)
	{
		this.im=im;
		confirm.setEnabled(false);
		xprofile=null;
		yprofile=null;
		selector=null;
		map=null;
	}
	public void setSelector(Rectangle selector)
	{
		this.selector=selector;
	}
	public void setMap(Particle map)
	{
		this.map=map;
	}
	public void setProfiles(double[] xprofile, double[] yprofile)
	{
		this.xprofile=xprofile;
		this.yprofile=yprofile;
		confirm.setEnabled(true);
	}
	public boolean isConfirmed()
	{
		if(confirmed)
		{
			confirmed=false;
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("confirm"))
		{
			confirmed=true;
		}
		
	}
}
