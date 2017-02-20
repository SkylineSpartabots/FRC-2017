package Vision;

public class Result {
	Target m_targetLeft=null;
	Target m_targetRight=null;
	
	public int m_centerX=0;
	public int m_centerY=0;
	
	int m_pixelsToCenter = 0;
	
	
	public static final int resolutionX = 320;
	public static final int resolutionY = 240;
	public static final int targetHeight = 5;
	public static final int targetWidth = 2;
	
	
	public Result (Target targetLeft, Target targetRight){
		m_targetLeft = targetLeft;
		m_targetRight = targetRight;
		if (hasBothTarget()){
			center();
		}
	}
	
	public void center() {
		m_centerX = (m_targetLeft.m_rect.x+m_targetRight.m_rect.x+m_targetRight.m_width)/2;
		m_centerY = (m_targetLeft.m_rect.y+m_targetRight.m_rect.y+m_targetRight.m_height)/2;
	}
	
	public double distance(){
		//distance = targetHeight * resY / (pixelHeight * tan(viewAngleY))
		if (hasBothTarget()){
		//return (targetHeight * resolutionY) / (((m_targetLeft.m_height+m_targetRight.m_height)/2)*Math.tan(34.3));
			return (targetHeight * resolutionY) / (((m_targetLeft.m_height+m_targetRight.m_height)/2)*0.68215375);
		} else {
			return 0;
		}
	}
	
	public int pixelsToCenter(){
		if (hasBothTarget()){
			m_pixelsToCenter = 160 - m_centerX;
			return m_pixelsToCenter;
		} else {
			return 0;
		}
	}
	
	public double sideDistance (){
		if (hasBothTarget()){
		
			return ((targetWidth * (160.00-m_centerX))/m_targetLeft.m_rect.width);
		} else {
			return 10;
		}
	}
	
	public boolean hasBothTarget(){
		return m_targetLeft!=null && m_targetRight!=null;
	}
	
	public boolean hasOneTarget(){
		return !hasBothTarget() && (m_targetLeft!=null || m_targetRight!=null);
	}
	
	public boolean hasNoTarget(){
		return m_targetLeft==null && m_targetRight==null;
	}
	
	public int targetNumber() {
		if (m_targetLeft!=null && m_targetRight!=null){
			return 2;
		} else if (m_targetLeft!=null || m_targetRight!=null) {
			return 1;
		} else if (m_targetLeft==null && m_targetRight==null){
			return 0;
		} else {
			return -1;
		}
	}
	
	public int targetLeftX() {
		if (hasBothTarget()){
			return m_targetLeft.m_rect.x;
		} else {
			return 0;
		}
	}
	
	public int targetLeftHeight(){
		if (hasBothTarget()){
			return m_targetLeft.m_rect.height;
		} else {
			return 0;
		}
	}
	
	public int targetRightHeight(){
		if (hasBothTarget()){
			return m_targetRight.m_rect.height;
		} else {
			return 0;
		}
	}
	
	public double slope(){
		if (hasBothTarget()){
			return (m_targetRight.m_rect.y-m_targetLeft.m_rect.y)/(m_targetRight.m_rect.x-m_targetLeft.m_rect.x);
		} else {
			return 0;
		}
	}
}