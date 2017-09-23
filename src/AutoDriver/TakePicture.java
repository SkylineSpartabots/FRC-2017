package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.Target;
import Vision.TraceLog;

public class TakePicture extends BaseAction{
	
	long m_lastGoodPictureTimestamp = 0;
	ActionType m_nextAction = ActionType.MoveForward;
	public TakePicture()
	{
		m_actionType = ActionType.TakePicture;
	}
	
	@Override
	public void Execute() 
	{
		super.Execute();
		
		ProcessResult lastGoodResult = Robot.vision.LastGoodResult;
		if (null == lastGoodResult)
		{
			return;
		}
		
		m_lastGoodPictureTimestamp = lastGoodResult.m_pictureTimestamp;
		if (m_lastGoodPictureTimestamp > m_actionStartTime)
		{
			m_data.m_lastGoodResult = lastGoodResult;	
			m_finished = true;

			if (!m_data.m_finalTimeoutReached){
				double angle = m_data.m_lastGoodResult.m_angle;
				double distance = m_data.m_lastGoodResult.m_distance;
				
				if (distance < AutoData.FinalStartDistanceInches && angle<AutoData.FinalStartAngleDegree) {
					TraceLog.Log("TakePicture", 
							"Start FinalMoveForward by target. distance=" + TraceLog.Round2(distance) 
							+ ", angle="+ TraceLog.Round2(angle)); 
					m_data.m_finalTimeoutReached = true;
					m_nextAction = ActionType.FinalMoveForward;
				}
			}
		}
	}
	
	
	@Override
	public ActionType GetNextActionType()
	{
		return m_nextAction;
	}
	
	public String GetStartLog()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ActionType="+m_actionType);
		builder.append(", StartTime="+m_actionStartTime);
		return builder.toString();
	}
	
	public String GetWaitLog()
	{
		return super.GetWaitLog()+", lastGoodPictureTime="+m_lastGoodPictureTimestamp;
	}
}
