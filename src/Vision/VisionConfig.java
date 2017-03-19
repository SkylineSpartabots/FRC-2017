package Vision;

public class VisionConfig {
	
	public static final int ImageResolutionX = 640;
	public static final int ImageResolutionY = 480;
	
	
	public int hueLowerBound = 40;
	public int hueUpperBound = 120;
	
	public int saturationLowerBound = 200;
	public int saturationUpperBound = 255;
	
	public int valueLowerBound = 100;
	public int valueUpperBound = 255;
	
	public boolean logTopTarget = true;
	public boolean logMatchTarget = true;
	public boolean saveBitmap = true;
}
