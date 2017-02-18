package newVision;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import visionCore.Particle;
import visionCore.Vision;
import algorithm.FeatureDetector;
import algorithm.RectangleController;
import keyboard.DownloadImages;
import keyboard.KeyboardInput;

public class CornerTest extends JFrame implements Runnable
{
	//Just collapse this array
	final int[] images={ 0, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
			33, 55, 56, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
			72, 73, 74, 75, 76, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
			91, 92, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192,
			193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205,
			206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218,
			219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231,
			232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244,
			245, 246, 247, 248, 249, 251, 252, 253, 254, 255, 257, 258, 259,
			260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272,
			273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285,
			290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302,
			303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315,
			316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328,
			329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341,
			342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354,
			355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367,
			368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380,
			381, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395,
			396, 397, 398, 399, 420, 421, 422, 423, 424, 425, 426, 427, 428,
			429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441,
			442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453, 454,
			465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477,
			478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490,
			491, 492, 493, 494, 495, 496, 497, 498, 499, 500, 501, 502, 503,
			504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, 516,
			517, 518, 519, 520, 521, 522, 523, 524, 525, 526, 527, 528, 529,
			530, 531, 532, 533, 534, 535, 536, 537, 538, 539, 540, 541, 542 };
	BufferedImage image;
	private int imageNumber=0;
	private int previousImage=-1;
	private Thread t;
	private KeyboardInput keyboard=new KeyboardInput();
	private FeatureDetector fd=new FeatureDetector(4);
	private boolean[][] map=new boolean[1][1];
	Vision2 v2=new Vision2();
	public double[][] map2;
	ArrayList<Point> corners=null;
	int[][] cornerScore=null;
	double wMult=2.0;
	double hMult=2.0;
	public static void main(String[] args)
	{
		CornerTest ct=new CornerTest();
		ct.start();
	}
	public CornerTest()
	{
		image=getImage(imageNumber);
		pack();
		setSize((getInsets().left*2)+(int)(image.getWidth()*wMult),(getInsets().top+getInsets().bottom)+(int)(image.getHeight()*hMult));
		t=new Thread(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addKeyListener(keyboard);
		execute();
		
		setVisible(true);
		t.start();
		System.out.println("Keyboard Activated");
	}
	public void start()
	{
		while(true)
		{
			long start=System.currentTimeMillis();
			execute();
			final int sleepTime=50;
			long t=sleepTime-(System.currentTimeMillis()-start);
			if(t<0)
			{
				t=0;
			}
			assert t<= sleepTime;
			try
			{
				Thread.sleep(t);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void execute()
	{
		keyboard.updateKeys();
		if(keyboard.keyOnce(KeyEvent.VK_A))
		{
			imageNumber--;
		}
		if(keyboard.keyOnce(KeyEvent.VK_D))
		{
			System.out.println("Key Registered");
			imageNumber++;
		}
		imageNumber=putInRange(imageNumber);
		if(imageNumber!=previousImage)
		{
			processImage();
		}
		previousImage=imageNumber;
		repaint();
	}
	public void processImage()
	{
		long t3=System.nanoTime();
		image=getImage(imageNumber);
		System.out.printf("Time for loading image is [%d] ms\n",(System.nanoTime()-t3)/1000000);
		v2=new Vision2();
		long t2=System.nanoTime();
		map2=v2.createMap(image);
		System.out.printf("Time for map: [%f]\n",(double)((System.nanoTime()-t2)/1000000));
		long t1=System.nanoTime();
		System.out.printf("Time for Filter is [%d]\n",(System.nanoTime()-t1)/1000000);
		fd.setMap(map2);
		fd.setFindCorners(true);
		Thread t=new Thread(fd);
		t.start();
		try
		{
			t.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		corners=fd.corners;
		cornerScore=fd.cornerScore;
		System.out.printf("Time for Harris Corner is [%d]\n",(System.nanoTime()-t1)/1000000);
	}
	public void paint(Graphics frameG)
	{
		BufferedImage canvas=new BufferedImage((int)(image.getWidth()*wMult),(int)(image.getHeight()*hMult),BufferedImage.TYPE_INT_RGB);
		Graphics g=canvas.getGraphics();
		//g.drawImage(image, 0, 0, null);
		final int cornerCircleRad=3;
		for(int i=0;i<map2.length;i++)
		{
			for(int j=0;j<map2[0].length;j++)
			{
				double multiplier=map2[i][j];
				if(multiplier<0)
				{
					multiplier=0;
				}
				if(multiplier>1)
				{
					multiplier=1;
				}
				int s=(int) (multiplier*255.0);
				g.setColor(new Color(s,s,s));
				g.fillRect(j+image.getWidth(), i, 1, 1);
				g.fillRect(j, i+image.getHeight(), 1, 1);
			}
		}
		/*
		int[][][] rgb=v2.getArray(image);
		for(int i=0;i<rgb.length;i++)
		{
			for(int j=0;j<rgb[0].length;j++)
			{
				g.setColor(new Color(rgb[i][j][0],0,0));
				g.fillRect(j, i+image.getHeight(), 1, 1);
				g.setColor(new Color(0,rgb[i][j][1],0));
				g.fillRect(j+image.getWidth(), i+image.getHeight(), 1, 1);
				g.setColor(new Color(0,0,rgb[i][j][2]));
				g.fillRect(j+image.getWidth(), i, 1, 1);
			}
		}
		*/
		//Final Corner Drawer
		g.setColor(Color.RED);
		final int cross=3;
		for(Point p: corners)
		{
			//g.fillRect(p.x-cross, p.y,(cross*2)+1, 1);
			//g.fillRect(p.x, p.y-cross, 1, (cross*2)+1);
			//g.drawOval(p.x-cornerCp.yrcleRad, p.y-cornerCp.yrcleRad, cornerCp.yrcleRad*2, cornerCp.yrcleRad*2);
			//g.drawOval(p.x-cornerCp.yrcleRad+p.ymage.getWp.ydth(), p.y-cornerCp.yrcleRad, cornerCp.yrcleRad*2, cornerCp.yrcleRad*2);
			//g.fillRect(p.x-cross+image.getWidth(), p.y,(cross*2)+1, 1);
			//g.fillRect(p.x+image.getWidth(), p.y-cross, 1, (cross*2)+1);
			g.fillRect(p.x-cross, p.y+image.getHeight(),(cross*2)+1, 1);
			g.fillRect(p.x, p.y-cross+image.getHeight(), 1, (cross*2)+1);
		}
		int max=0;
		for(int i=0;i<cornerScore.length;i++)
		{
			for(int j=0;j<cornerScore[0].length;j++)
			{
				if(cornerScore[i][j]>max)
				{
					max=cornerScore[i][j];
				}
			}
		}
		max=max/5;
		for(int i=0;i<cornerScore.length;i++)
		{
			for(int j=0;j<cornerScore[0].length;j++)
			{
				g.setColor(getHeatColor(cornerScore[i][j], max));
				g.fillRect(j, i, 1, 1);
			}
		}
		frameG.drawImage(canvas, getInsets().left, getInsets().top, null);
	}
	public Color getHeatColor(int value, int max)
	{
		double ratio=value/(max*1.0);
		ratio=Math.max(ratio,0.0);
		ratio=Math.min(ratio,1.0);
		final Color[] heat=
			{
				Color.BLACK,
				Color.BLUE,
				Color.CYAN,
				Color.GREEN,
				Color.YELLOW,
				Color.RED
			};
		int c=(int) Math.floor(ratio*(heat.length-1));
		if(c==heat.length-1)
		{
			return heat[heat.length-1];
		}
		return gradBetween(heat[c],heat[c+1],ratio*(heat.length-1)-c);
	}
	private Color gradBetween(Color c1, Color c2, double rat)
	{
		Color color=new Color((int)(((c2.getRed()-c1.getRed())*rat)+c1.getRed()), (int)(((c2.getGreen()-c1.getGreen())*rat)+c1.getGreen()), (int)(((c2.getBlue()-c1.getBlue())*rat)+c1.getBlue()));
		return color;
	}
	@Override
	public void run()
	{
		Scanner s=new Scanner(System.in);
		while(true)
		{
			System.out.println("Choose an Image Number or use the AD keys to browse");
			String input=s.nextLine();
			int inputnum;
			try
			{
				inputnum=Integer.parseInt(input);
				imageNumber=putInRange(inputnum);
				System.out.printf("Processed: [%d] for %d.jpg\n",imageNumber,images[imageNumber]);
			}
			catch(NumberFormatException e)
			{
				System.out.println("Please enter a valid number");
			}
		}
		
	}
	private int putInRange(int input)
	{
		input=input%images.length;
		if(input<0)
		{
			input=input+images.length;
		}
		return input;
	}
	public BufferedImage getImage(int imageNumber)
	{
		String path=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"RealFullField"+File.separator;
		imageNumber=images[imageNumber];
		BufferedImage image=null;
		File file;
		file=new File(path+imageNumber+".jpg");
		//file=new File(path+"Test.jpg");
		if(!file.exists())
		{
			System.out.println("Images not found. Attempting to download...");
			try
			{
				DownloadImages.download(path);
			} catch (IOException e)
			{
				System.out.println("Download Failed. Exiting...");
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Images Successfully Downloaded at "+path);
		}
		try
		{
			image=ImageIO.read(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return image;
	}
}
