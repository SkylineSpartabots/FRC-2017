package def;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import visionCore.Vision;

import com.github.sarxos.webcam.Webcam;

public class PiTest 
{
	public static void main(String[] args)
	{
		long start;
		Webcam webcam;
		start=System.currentTimeMillis();
		webcam=Webcam.getDefault();
		webcam.open();
		System.out.println("Webcam Initialize Time: "+(System.currentTimeMillis()-start));
		Vision vision=new Vision();
		BufferedImage image;
		image=webcam.getImage();
		start=System.currentTimeMillis();
		double[] target=vision.process(image);
		System.out.println("Process Time: "+(System.currentTimeMillis()-start));
		System.out.println("("+target[0]+", "+target[1]+") Distance: "+target[2]);
		try {
			ImageIO.write(image, "PNG", new File("capture.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webcam.close();
	}
}
