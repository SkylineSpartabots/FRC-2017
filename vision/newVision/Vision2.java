package newVision;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;

import algorithm.Operation;
import algorithm.RectangleController;
import visionCore.RGB;
/*
 * NOTES:
 * -Scrap Blob Project
 * -Use Ixx and Iyy for sides (threshold 2000), find connections between corners and find the target
 * -Method:
 * Connections between corners and type (ix / iy)
 * Link corners using connections to find target
 * Use target w/ least angle and moderate size
 */
public class Vision2
{
	private boolean[][] lowerMask=new boolean[120][80];
	public double[] process(double[][] in)
	{
		double[][] map=Vision2.copyOf(in);
		double[] target=new double[3];
		RectangleController rc=new RectangleController(4);
		rc.map=map;
		rc.setCommand(Operation.PROCESS);
		Thread t=new Thread(rc);
		t.start();
		
		generateMasks();
		ArrayList<Blob> blobs=findBlobs(Vision2.copyOf(map));
		
		
		return target;
	}
	private void generateMasks()
	{
		Point center=new Point(lowerMask[0].length/2,0);
		for(int i=0;i<lowerMask.length;i++)
		{
			for(int j=0;j<lowerMask[0].length;j++)
			{
				if(distance(center,new Point(j,i))<=lowerMask.length)
				{
					lowerMask[i][j]=true;
				}
			}
		}
	}
	public ArrayList<Blob> findBlobs(double[][] map)
	{
		ArrayList<Blob> blobs=new ArrayList<Blob>();
		for(int i=0;i<map.length;i++)
		{
			for(int j=0;j<map[0].length;j++)
			{
				
			}
		}
		return blobs;
	}
	public int[][][] getArray(BufferedImage image)
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
	public double[][] createMap(BufferedImage input)
	{
		int[][][] image=getArray(input);
		double[][] map=new double[image.length][image[0].length];
		final double factor=1.0/(3*255.0);
		for (int i = 0; i < image.length; i++)
		{
			for (int j = 0; j < image[0].length; j++)
			{
				double value=1.0;
				int red = image[i][j][0];
				int green = image[i][j][1];
				int blue = image[i][j][2];
				/*
				int[] hsv = getHSV(red, green, blue);
				final double[] weights={0.4,0.3,0.3};
				final int[] means={80, 152,101};
				final int[] dev={35,90,75};
				for(int k=0;k<hsv.length;k++)
				{
					//value=value-(weights[k]*(Math.abs(hsv[k]-means[k])/(1.0*dev[k])));
				}
				*/
				/*
				value=(100-Math.max(0.0,155-green))/100.0;
				value=value-Math.max(0.0,red-40.0)/100.0;
				
				value=Math.min(1.0,value);
				value=Math.max(0.0,value);
				*/
				value=(red+green+blue)*(factor);
				map[i][j] = value;
			}
		}
		return map;
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
					hsv[0]=(int) (30.0*(((g-b)/delta)%6));
					break;
				case GREEN:
					hsv[0]=(int) (30.0*(((b-r)/delta)+2));
					break;
				case BLUE:
					hsv[0]=(int) (30.0*(((r-g)/delta)+4));
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
			hsv[1]=(int) (255.0*delta/max);
		}
		hsv[2]=(int) (255.0*max);
		return hsv;
	}
	public static double[][] copyOf(double[][] original)
	{
		double[][] copy = new double[original.length][];
		for (int i = 0; i < original.length; i++)
		{
			copy[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return copy;
	}
	private double distance(Point p, Point p2)
	{
		return Math.sqrt(Math.pow(p.x - p2.x, 2) + Math.pow(p.y - p2.y, 2));
	}
}
