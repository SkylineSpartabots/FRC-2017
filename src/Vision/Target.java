package Vision;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


public class Target {
	public Rect m_rect;
	public MatOfPoint m_contour;
	int m_width;
	int m_height;
	double m_ratio;
	double m_ratioScore;
	double m_area;
	double m_contourArea;
	
	public Target(Rect rect, MatOfPoint contour){
		m_rect = rect;
		m_contour = contour;
		m_width = rect.width;
		m_height = rect.height;
		m_area = rect.area();
		m_contourArea = Imgproc.contourArea(m_contour);
	}
	
	
	public double ratio(){
		return((double)m_height)/m_width;
	}
	
	
	
	public double ratioScore(){
		return Math.abs(2.5 - ratio());
	}
	
}
