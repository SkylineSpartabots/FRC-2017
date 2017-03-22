package Vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class Target {

	static final double TargetHWRatio = 2.2;
	static final double HwRationRange = .3;
	
	static public final double TargetFillFactor = 0.80;
	static final double FillFactorRange = .20;
	
	public static final double TanViewAngleY =0.68215375;
	public static final double TargetHeight = 5; 	//	inch 
	public static final double TanViewAngleX =0.68215375;
	public static final double TargetWidth = 2; 	// 	inch
	public static final double TargetSideDistance = 8.25; //inch, horizontal distance between 2 targets 
	
	public int m_index = -1;
	public int m_x;
	public int m_y;
	public int m_width;
	public int m_height;
	
	public double m_area;
	
	double m_contourArea;
	double m_fillFactor;
	public double m_fillFactorScore;
	double m_hwRatio;
	public double m_hwRatioScore;
	public double m_score;
	
	int m_centerX;
	int m_centerY;
	double m_distanceFromHeight;
	
	String m_string;

	MatOfPoint m_contour;
	public VisionConfig m_config;

	public Target(MatOfPoint contour, VisionConfig config){
		m_contour = contour;
		m_config = config;
		
		Rect rect = Imgproc.boundingRect(m_contour);
		m_x = rect.x;
		m_y = rect.y;
		m_width = rect.width;
		m_height = rect.height;
		
		m_area = rect.area();
	}

	@Override
	public String toString()
    {
		return m_string;
    }
	
	public static double CalculateDistanceHeight(int pixelHeight)
	{
		return 1.0 * TargetHeight * VisionConfig.ImageResolutionY/ (pixelHeight * TanViewAngleY);
	}
	
	
	public static double CalculateDistanceWidth(int pixelWidth)
	{
		return 1.0 * TargetWidth * VisionConfig.ImageResolutionX/ (pixelWidth * TanViewAngleX);
	}
	
	
	public static double CalculateDistance(int pixelDiff, int targetPixelDiff, double targetSize)
	{
		return targetSize * pixelDiff / targetPixelDiff;
	}
	
	
	public void QuickProcess()
	{
		m_contourArea = Imgproc.contourArea(m_contour);

		m_hwRatio =  1.0 * m_height / m_width;
		m_fillFactor = 1.0 * m_contourArea / m_area;
		
		//normalize score to good approximate value instead of ideal value
		m_hwRatioScore = (m_hwRatio-TargetHWRatio)/HwRationRange;
		m_fillFactorScore = (m_fillFactor-TargetFillFactor)/FillFactorRange;
		
		// use square to a)handle positive/negative score b) curve for close match
		m_score = m_hwRatioScore*m_hwRatioScore*m_fillFactorScore*m_fillFactorScore; 
	}

	public void CompleteProcess()
	{
		m_centerX = m_x + m_width/2;
		m_centerY = m_y + m_height/2;
		m_distanceFromHeight = CalculateDistanceHeight(m_height);

		StringBuilder builder = new StringBuilder();
		builder.append("index="+m_index);
		builder.append(", score="+TraceLog.Round3(m_score));
		builder.append(", hwRatioScore="+TraceLog.Round3(m_hwRatioScore));
		builder.append(", fillFactorScore="+TraceLog.Round3(m_fillFactorScore));
		
		
		builder.append(", m_hwRatio="+TraceLog.Round2(m_hwRatio));
		builder.append(", fillFactor="+TraceLog.Round2(m_fillFactor));
		
		builder.append(", width="+m_width);
		builder.append(", height="+m_height);
		builder.append(", dist="+TraceLog.Round2(m_distanceFromHeight));
		
		builder.append(", x="+m_x);
		builder.append(", y="+m_y);
		builder.append(", area="+m_area);
		builder.append(", contourArea="+m_contourArea);
		
		builder.append(", centerX="+m_centerX);
		builder.append(", centerY="+m_centerY);		

		m_string = builder.toString();
	}

}
