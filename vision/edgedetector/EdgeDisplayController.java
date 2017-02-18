package edgedetector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import code2017.DownloadImages;



public class EdgeDisplayController implements Runnable
{
	static String[] TARGET_TYPES={
		"Blue Boiler",
		"LED Boiler",
		"LED Peg",
		"Red Boiler"
	};
	final static int TARGET_SETTING = 2;
	static String path=System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"FRC Field 2017"+File.separator+TARGET_TYPES[TARGET_SETTING]+File.separator;
	int imageNumber=0;
	boolean newImage=true;
	File[] imageFiles=null;
	public static void main(String[] args)
	{
		EdgeDisplayController controller=new EdgeDisplayController();
		controller.control();
	}
	public void findPath()
	{
		
	}
	EdgeDisplayer frame=null;
	public void control()
	{
		findPath();
		imageFiles=findPictureFiles(new File(path), "jpg");
		frame=new EdgeDisplayer(getImage(new File(path+"1ftH1ftD0Angle0Brightness.jpg")));
		Thread t=new Thread(this);
		t.start();
		try
		{
			Thread.sleep(250);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		while(true)
		{
			int pics=frame.getImageQueue();
			if(pics!=0)
			{
				imageNumber=imageNumber+pics;
				while(imageNumber>=imageFiles.length)
				{
					imageNumber=imageNumber-imageFiles.length;
				}
				while(imageNumber<0)
				{
					imageNumber=imageNumber+imageFiles.length;
				}
				System.out.println("Picture: "+imageNumber+" \t--   "+imageFiles[imageNumber].getName());
				frame.setImage(getImage(imageFiles[imageNumber]));
				frame.setTitle("Image: "+imageNumber+" - "+imageFiles[imageNumber].getName());
			}
			if(newImage)
			{
				newImage=false;
				frame.setImage(getImage(imageFiles[imageNumber]));
				frame.setTitle("Image: "+imageNumber+" - "+imageFiles[imageNumber].getName());
			}
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static BufferedImage getImage(File imageFile)
	{
		BufferedImage image=null;
		File file=imageFile;
		if(!file.exists())
		{
			System.out.println("Images not found. Attempting to download...");
			try
			{
				DownloadImages.download();
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
				if(newImage>=0&&newImage<imageFiles.length)
				{
					imageNumber=newImage;
					frame.setTitle("Image: "+imageNumber+" - "+imageFiles[imageNumber]+".jpg");
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
	public File[] findPictureFiles(File file, String fileType)
	{
		if(!file.isDirectory())
		{
			return null;
		}
		File[] files=file.listFiles();
		for(int i=files.length-1;i>=0;i--)
		{
			if(!files[i].getName().endsWith("."+fileType))
			{
				File[] newArray=new File[files.length-1];
				System.arraycopy(files, 0, newArray, 0, i);
				if(i+1<files.length)
				{
					System.arraycopy(files, i+1, newArray, i, files.length-i);
				}
				files=newArray;
			}
		}
		return files;
	}
}
