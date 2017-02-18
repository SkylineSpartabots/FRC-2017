package exampleModules;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import code2017.Target;
import code2017.Vision17;
import clientController.Client;

public class ClientSample
{
	Client client;
	final static String PATH = "D:\\Images\\";
	public void init()
	{
		client=new Client();
		client.init();
	}
	public void exec()
	{
		while(true)
		{
			if(client.waitToProcess(1000))//This number doesn't matter
			{
				Vision17 v=new Vision17();
				BufferedImage image1=client.getImage("image1");
				BufferedImage image2=client.getImage("image2");
				v.setImage(image1, image2);
				Target target=v.exec();
				client.transmitTarget(target);
				long t=System.currentTimeMillis();
				saveImage(image1, "image1."+t);
				saveImage(image2, "image2."+t);
			}
			//Don't need to wait since the waitToProcess already does
		}
	}
	public void saveImage(BufferedImage image, String name)
	{
		try
		{
			ImageIO.write(image, "jpg", new File(PATH+name+".jpg"));
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
