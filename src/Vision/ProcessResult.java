package Vision;

public class ProcessResult {

	public Target m_targetLeft = null;
	public Target m_targetRight = null;

	public int m_centerX = 0;
	public int m_centerY = 0;

	public double m_distance = 0;
	public double m_sideDistance = 0;
	public int m_targetCount = 0;
	
	public long m_createTimestamp = 0;
	public long m_pictureTimestamp = 0;
	String m_string="";

	public ProcessResult(Target targetLeft, Target targetRight){
		m_targetLeft = targetLeft;
		m_targetRight = targetRight;

		m_createTimestamp = System.currentTimeMillis();
		process();
	}
	
	@Override
	public String toString()
    {
		return m_string;
    }
    
	public long age(){
		if (m_pictureTimestamp > 0)
		{
			return System.currentTimeMillis()-m_pictureTimestamp;
		}
		return System.currentTimeMillis()-m_createTimestamp;
	}

	
	public void process(){
		int screenCenterX = VisionConfig.ImageResolutionX/2;
		if (m_targetLeft!= null && m_targetRight != null)
		{
			m_centerX = (m_targetLeft.m_centerX + m_targetRight.m_centerX)/2;
			m_centerY = (m_targetLeft.m_centerY + m_targetRight.m_centerY)/2;
			m_distance = (m_targetLeft.m_distanceFromHeight + m_targetRight.m_distanceFromHeight)/2;
			m_sideDistance = Target.CalculateDistance(m_centerX - screenCenterX, 
					m_targetRight.m_centerX -  m_targetLeft.m_centerX, 
					Target.TargetSideDistance);
		}
		else if (m_targetLeft!= null)
		{
			m_centerX = m_targetLeft.m_centerX + m_targetLeft.m_width*2;
			m_centerY = m_targetLeft.m_centerY;
			
			m_distance = m_targetLeft.m_distanceFromHeight;
			m_sideDistance = Target.CalculateDistance(m_centerX - screenCenterX,
					m_targetLeft.m_width,
					Target.TargetWidth);
		}
		else if (m_targetRight!= null)
		{
			m_centerX = m_targetRight.m_centerX - m_targetRight.m_width*2;
			m_centerY = m_targetRight.m_centerY;
			
			m_distance = m_targetRight.m_distanceFromHeight;
			m_sideDistance = Target.CalculateDistance(screenCenterX - m_centerX,
					m_targetRight.m_width, 
					Target.TargetWidth);
		}
		
		if (m_targetLeft != null){
			m_targetCount++;
		} 
		
		if (m_targetRight != null) {
			m_targetCount++;
		} 
		
		StringBuilder builder = new StringBuilder();
		builder.append("TargetCount="+m_targetCount);
		builder.append(", distance="+TraceLog.Round2(m_distance));
		builder.append(", side="+TraceLog.Round2(m_sideDistance));
		builder.append(", centerX="+m_centerX);
		builder.append(", centerY="+m_centerY);	
		if (m_targetLeft != null){
			builder.append(", left="+m_targetLeft.m_index);
		}
		if (m_targetRight != null){
			builder.append(", right="+m_targetRight.m_index);
		}
		m_string = builder.toString();
	}

}