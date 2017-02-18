package hsvFinder;

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

public class HSVFrame extends JFrame implements ActionListener
{
	KeyboardInput keyboard;
	static final float wMult=1;
	static final float hMult=1f;
	private Dimension image;
	private BufferedImage im=null;
	private JButton confirm;
	private boolean confirmed=false;
	final float multiplier=2.0f;
	private Point prev;
	public HSVFrame(Dimension image, KeyboardInput keyboard, MouseListener ml)
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
			BufferedImage scaledp=new BufferedImage((int)(im.getWidth()*multiplier),(int)(im.getHeight()*multiplier),im.getType());
			AffineTransform at=new AffineTransform();
			at.scale(multiplier, multiplier);
			AffineTransformOp op=new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			op.filter(im, scaledp);
			g.drawImage(scaledp, 0, 0, null);
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
