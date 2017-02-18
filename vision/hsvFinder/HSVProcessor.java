package hsvFinder;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import keyboard.KeyboardInput;
import scoreFinder.ScoreFrame;
import visionCore.RGB;
import def.PictureController;

public class HSVProcessor implements Runnable, MouseListener
{
	int imageNumber=0;
	int imQ=0;
	final int frameDelay=20;
	BufferedImage image=null;
	HSVFrame hf;
	KeyboardInput keyboard=new KeyboardInput();
	int[][][] rgb;
	public void start()
	{
		image=PictureController.getImage(PictureController.images[imageNumber]);
		hf=new HSVFrame(new Dimension(image.getWidth(),image.getHeight()), keyboard, this);
		hf.setImage(image);
		rgb=getArray(image);
		Thread t=new Thread(this);
		t.start();
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
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
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
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	private int[][][] getArray(BufferedImage image)
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
	public int[] getHSV(int red, int green, int blue)
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
}
