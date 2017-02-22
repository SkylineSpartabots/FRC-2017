package Vision;


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
	
	public Target(MatOfPoint contour){
		m_rect = Imgproc.boundingRect(contour);
		m_contour = contour;
		m_width = m_rect.width;
		m_height = m_rect.height;
		m_area = m_rect.area();
		m_contourArea = Imgproc.contourArea(m_contour);
	}
	
	public double fillRatio (){
		if (m_area<0.01){
			return 100;
		} 
		
		return m_contourArea/m_area;
		
	}
	
	public double ratio(){
		if (m_width<0.01){
		return 1000;
		}
		return((double)m_height)/m_width;
	}
	
	
	
	public double ratioScore(){
		return Math.abs(2.50 - ratio());
	}
	
}



