package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;

public class FinalMoveForward extends BaseAction {
	
	double m_leftMotorPower = 0;
	double m_rightMotorPower = 0;
	double m_toMoveTimeInMsec = 0;
	String m_string = "";
	
	public FinalMoveForward()
	{
		m_actionType = ActionType.FinalMoveForward;
	}
	
	@Override
	public void Start(AutoData p_data)
	{
		super.Start(p_data);	

		m_leftMotorPower = AutoData.MoveStraightPowerLeft;
		m_rightMotorPower = AutoData.MoveStraightPowerRight;
		
		if (m_data.m_lastGoodResult.m_distance > 60)
		{
			m_toMoveTimeInMsec= 1500;
		}
		else if (m_data.m_lastGoodResult.m_distance > 40) 
		{
			m_toMoveTimeInMsec = 1000;
		}
		else if (m_data.m_lastGoodResult.m_distance > 20) 
		{
			m_toMoveTimeInMsec = 700;
		}
		else 
		{
			m_toMoveTimeInMsec = 400;
		}
		Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
		
		StringBuilder builder = new StringBuilder();
		builder.append("FinalMoveForward Start Distance="+m_data.m_lastGoodResult.m_distance);
		builder.append(", m_leftMotorPower="+m_leftMotorPower);
		builder.append(", m_rightMotorPower="+m_rightMotorPower);
		builder.append(", m_toMoveTimeInMsec="+m_toMoveTimeInMsec);
		builder.append(", m_rightMotorPower="+m_rightMotorPower);
		builder.append(", m_data.m_lastGoodResult.m_distancec="+m_data.m_lastGoodResult.m_distance);
		m_string = super.GetLogString() + builder.toString();
	}
	
	@Override
	public void Execute()
	{
		super.Execute();
		if (m_actionRunPeriod > m_toMoveTimeInMsec){
			m_finished = true;
		}
	}
		
	@Override
	public void Stop()
	{
		Robot.drivetrain.tankDrive(0, 0);
		m_data.m_finalFinished = true;
	}
	
	@Override
	public ActionType GetNextActionType()
	{
		return ActionType.BaseAction;
	}
	
	@Override
	protected String GetLogString()
	{
		return m_string;
	}
}
