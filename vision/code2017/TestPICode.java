package code2017;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

public class TestPICode 
{
	public static void main(String[] args)
	{
		Vision17 vision = new Vision17();
		Webcam webcam=Webcam.getDefault();
		webcam.open();
		BufferedImage image1=webcam.getImage();
		BufferedImage image2=webcam.getImage();
		vision.setImage(image1, image2);
		Target target = vision.exec();
		System.out.printf("\nCoordinates: (%f, %f) \nAngle: [%f] \nDistance: [%f]\n", target.x, target.y, target.angle, target.distance);
	}
}
