package def;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import keyboard.DownloadImages;


public class PictureController implements Runnable
{
	static String path=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"RealFullField"+File.separator;
	int imageNumber=0;
	public static int[] images={ 0, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15,
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
	boolean newImage=true;
	public static void main(String[] args)
	{
		PictureController controller=new PictureController();
		controller.control();
	}
	public void findPath()
	{
		path=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"RealFullField"+File.separator;
	}
	PictureTester frame=new PictureTester(getImage(images[imageNumber]));
	public void control()
	{
		findPath();
		Thread t=new Thread(this);
		t.start();
		while(true)
		{
			int pics=frame.getImageQueue();
			if(pics!=0)
			{
				imageNumber=imageNumber+pics;
				while(imageNumber>=images.length)
				{
					imageNumber=imageNumber-images.length;
				}
				while(imageNumber<0)
				{
					imageNumber=imageNumber+images.length;
				}
				System.out.println("Picture: "+imageNumber+" \t--   "+images[imageNumber]+".jpg");
				frame.setImage(getImage(images[imageNumber]));
				frame.setTitle("Image: "+imageNumber+" - "+images[imageNumber]+".jpg");
			}
			if(newImage)
			{
				newImage=false;
				frame.setImage(getImage(images[imageNumber]));
				frame.setTitle("Image: "+imageNumber+" - "+images[imageNumber]+".jpg");
			}
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static BufferedImage getImage(int imageNumber)
	{
		BufferedImage image=null;
		File file=new File(path+imageNumber+".jpg");
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
			
		}
		return image;
	}
	@Override
	public void run()
	{
		Scanner s=new Scanner(System.in);
		String input;
		while(true)
		{
			System.out.println("Input a Number");
			input=s.nextLine();
			try
			{
				int newImage;
				newImage=Integer.parseInt(input);
				if(newImage>=0&&newImage<images.length)
				{
					imageNumber=newImage;
					frame.setTitle("Image: "+imageNumber+" - "+images[imageNumber]+".jpg");
					this.newImage=true;
					break;
				}
			}
			catch(NumberFormatException e)
			{
				System.out.println("Invalid Input");
			}
		}
		s.close();
	}
}
