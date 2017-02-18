package clientController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageByteConverter
{
	public static String[] IMAGE_FORMATS=
		{
			"png",
			"jpg"
		};
	public static byte[] getImageToBytes(BufferedImage image)
	{
		return getImageToBytes(image, "jpg");
	}
	public static byte[] getImageToBytes(BufferedImage image, String fileType)
	{
		boolean foundFormat=false;
		for(String format: IMAGE_FORMATS)
		{
			if(format.equals(fileType))
			{
				foundFormat=true;
				break;
			}
		}
		if(!foundFormat)
		{
			System.out.printf("WARNING: Invalid file format. Location: getImageToBytes\n");
			return null;
		}
		if(image==null)
		{
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(image, fileType, out);
			byte[] bytes=out.toByteArray();
			out.flush();
			return bytes;
		}
		catch(IOException e)
		{
			System.out.printf("WARNING: IOException in ImageByteConverter. Location: getImageToBytes\n");
		}
		return null;
	}
	public static BufferedImage getBytesToImage(byte[] bytes)
	{
		if(bytes==null)
		{
			return null;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try
		{
			BufferedImage image = ImageIO.read(in);
			if(image==null)
			{
				System.out.printf("WARNING: null image generated. Location: getBytesToImage\n");
			}
			return image;
		} 
		catch (IOException e)
		{
			System.out.printf("WARNING: IOException in ImageByteConverter. Location: getBytesToImage\n");
		}
		return null;
	}
}
