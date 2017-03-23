package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.Target;
import Vision.TraceLog;

public class TakePicture extends BaseAction{
	
	String m_string = "";
	public TakePicture()
	{
		m_actionType = ActionType.TakePicture;
	}
	
	@Override
	public void Execute() 
	{
		ProcessResult lastGoodResult = Robot.vision.LastGoodResult;
		
		if (lastGoodResult.m_pictureTimestamp>m_actionStartTime)
		{
			m_data.m_lastGoodResult = lastGoodResult;	
			m_finished = true;
		}

		m_string = super.GetLogString() 
				+ ", lastGoodResult.m_pictureTimestamp="
				+ lastGoodResult.m_pictureTimestamp;
	}
	
	
	@Override
	public ActionType GetNextActionType()
	{
		return ActionType.MoveForward;
	}
	
	@Override
	protected String GetLogString()
	{
		return m_string;
	}
}
