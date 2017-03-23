package AutoDriver;

import Vision.TraceLog;

public class BaseAction {
	AutoData m_data;
	ActionType m_actionType;

	protected boolean m_finished = false;
	protected long m_actionStartTime = 0;
	protected long m_actionRunPeriod = 0;
	public BaseAction()
	{
		m_actionType = ActionType.BaseAction;
	}
	
	public void Start(AutoData p_data)
	{
		m_data = p_data;
		m_data.m_state = ActionState.WaitForAction;
		m_actionStartTime = System.currentTimeMillis();
	}
	
	public void Execute()
	{
		m_actionRunPeriod = System.currentTimeMillis() - m_actionStartTime;
	}
	
	public boolean IsFinished()
	{
		return m_finished;
	}
	
	public void Stop()
	{
	}
	
	public ActionType GetNextActionType()
	{
		return ActionType.TakePicture;
	}
	
	protected String GetLogString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ActionType="+m_actionType);
		builder.append(", m_actionStartTime="+m_actionStartTime);
		builder.append(", m_actionRunPeriod="+m_actionRunPeriod);
		builder.append(", m_finished="+m_finished);
		return builder.toString();
	}
}
