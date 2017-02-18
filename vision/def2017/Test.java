package def2017;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algorithm.ParticleFinder;
import visionCore.Vision;
import newVision.*;

public class Test 
{
	final static String path="C:\\Users\\"+System.getProperty("user.name")+"\\Downloads\\RealFullField\\";
	final static String fileNumber="21";
	public static void main(String[] args)
	{
		/*Webcam webcam=Webcam.getDefault();
		webcam.setViewSize(new Dimension(960,540));
		webcam.open();
		VisionProcessing vision=new VisionProcessing();
		double[] target=vision.process(vision.createMap(webcam.getImage()));
		*/ 
		BufferedImage image=null;
		try
		{
			image=ImageIO.read(new File(path+fileNumber+".jpg"));
		} catch(IOException e)
		{
			
		}
		Vision2 v2=new Vision2();
		long start=System.currentTimeMillis();
		Vision v=new Vision();
		double[] target=v.process(image);
		System.out.println("Total: "+(System.currentTimeMillis()-start));
		System.out.println("("+target[0]+", "+target[1]+") Distance: "+target[2]+" Angle: "+target[3]);
		System.out.println(Math.toDegrees(target[3]));
		System.out.println("("+(int)(image.getWidth()/2.0+(target[0]*image.getWidth()/2.0))+", "+(int)(image.getHeight()/2.0+(target[1]*image.getHeight()/-2.0))+")");
		
	}
}
