package Vision;

public class Result {
	Target m_targetLeft=null;
	Target m_targetRight=null;
	
	public int m_centerX=0;
	public int m_centerY=0;
	
	int m_pixelsToCenter = 0;
	
	long start;
	
	public static final int targetHeight = 5;
	public static final int targetWidth = 2;
	
	
	public Result (Target targetLeft, Target targetRight){
		m_targetLeft = targetLeft;
		m_targetRight = targetRight;
		if (hasBothTarget()){
			center();
		} else if (m_targetLeft!=null){
			centerLeft();
		} else if (m_targetRight!=null){
			centerRight();
		}
		
		start = System.currentTimeMillis();
	}
	
	boolean hasLeftTarget()
	{
		return m_targetLeft!=null;
	}
	
	boolean hasRightTarget()
	{
		return m_targetRight!=null;
	}
	public long age(){
		return System.currentTimeMillis()-start;
	}
	
	
	public void center() {
		m_centerX = (m_targetLeft.m_rect.x+m_targetRight.m_rect.x+m_targetRight.m_width)/2;
		m_centerY = (m_targetLeft.m_rect.y+m_targetRight.m_rect.y+m_targetRight.m_height)/2;
	}
	
	public void centerLeft() {
		m_centerX = (int)(m_targetLeft.m_rect.x+(m_targetLeft.m_width*5.0)/2);
		m_centerY = (int)(m_targetLeft.m_rect.y+m_targetLeft.m_height/2);
	}
	
	public void centerRight() {
		m_centerX = (int)(m_targetRight.m_rect.x-(m_targetRight.m_width*3.0)/2);
		m_centerY = (int)(m_targetRight.m_rect.y+m_targetRight.m_height/2);
	}
	
	public double distance(){
		//distance = targetHeight * resY / (pixelHeight * tan(viewAngleY))
		double tanViewAngleY=0.68215375;
		if (hasBothTarget()){
			return (targetHeight * VisionMain.resolutionY) / (((m_targetLeft.m_height+m_targetRight.m_height)/2)*tanViewAngleY);
		} else if (hasLeftTarget()){
			return (targetHeight * VisionMain.resolutionY) / (m_targetLeft.m_height*tanViewAngleY);
		} else if (hasRightTarget()){
			return (targetHeight * VisionMain.resolutionY) / (m_targetRight.m_height*tanViewAngleY);
		}
		return 0;
	}
	
	public double sideDistance (){
		if (hasBothTarget() || hasLeftTarget()){
			return ((1.0*targetWidth * (VisionMain.resolutionX/2-m_centerX))/m_targetLeft.m_rect.width);
		} else if (hasRightTarget()){
			return ((1.0*targetWidth * (VisionMain.resolutionX/2-m_centerX))/m_targetRight.m_rect.width);
		}
		return 0;
	}
	
	public double rotateDistance (){
		double tanViewAngleY=0.68215375;
		//negative: rotate left, positive: rotate right
		if (hasBothTarget()){
			return (targetHeight * VisionMain.resolutionY) / (m_targetLeft.m_height*tanViewAngleY) -
					(targetHeight * VisionMain.resolutionY) / (m_targetRight.m_height*tanViewAngleY);
		}
		return 0;
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
}