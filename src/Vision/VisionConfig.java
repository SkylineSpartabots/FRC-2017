package Vision;

public class VisionConfig {
	
	public static final int ImageResolutionX = 640;
	public static final int ImageResolutionY = 480;
	
	
	public int hueLowerBound = 40;
	//public int hueUpperBound = 120;
	public int hueUpperBound = 90;
	
	//public int saturationLowerBound = 50;
	public int saturationLowerBound = 20;
	public int saturationUpperBound = 255;
	
	//public int valueLowerBound = 50;
	public int valueLowerBound = 90;
	public int valueUpperBound = 255;
	
	public double targetScoreUpperBound = 1;
	
	public boolean logTopTarget = true;
	public boolean logMatchTarget = true;
	public boolean saveBitmap = false;
	
	String m_string = "";
	String m_shortString = "";
	
	@Override
	public String toString()
    {
		if (m_string.isEmpty())
		{
			GenerateString();
		}
		return m_string;
    }
	
	public String GetShortString()
    {
		if (m_shortString.isEmpty())
		{
			StringBuilder builder = new StringBuilder();
			builder.append("hue"+hueLowerBound);
			builder.append("_"+hueUpperBound);
			builder.append("sat"+saturationLowerBound);
			builder.append("_"+saturationUpperBound);
			builder.append("val"+valueLowerBound);
			builder.append("_"+valueUpperBound);
			builder.append("score_"+targetScoreUpperBound);
			m_shortString = builder.toString();
		}
		return m_shortString;
    }
	
	void GenerateString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("hueLowerBound="+hueLowerBound);
		builder.append(", hueUpperBound="+hueUpperBound);
		builder.append(", saturationLowerBound="+saturationLowerBound);
		builder.append(", saturationUpperBound="+saturationUpperBound);
		builder.append(", valueLowerBound="+valueLowerBound);
		builder.append(", valueUpperBound="+valueUpperBound);
		builder.append(", targetScoreUpperBound="+targetScoreUpperBound);
		builder.append(", logTopTarget="+logTopTarget);
		builder.append(", logMatchTarget="+logMatchTarget);
		builder.append(", saveBitmap="+saveBitmap);
		m_string = builder.toString();
	}
	
}
