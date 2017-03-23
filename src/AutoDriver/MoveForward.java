package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;

public class MoveForward extends BaseAction {
	
	double m_leftMotorPower = 0;
	double m_rightMotorPower = 0;
	double m_toMoveTimeInMsec = 0;
	String m_string = "";
	
	public MoveForward()
	{
		m_actionType = ActionType.MoveForward;
	}
	
	@Override
	public void Start(AutoData p_data)
	{
		super.Start(p_data);
				
		if (m_data.m_lastGoodResult.m_targetCount != 2)
		{
			TraceLog.Log("MoveForward", "Exception, picture is not good, targetCount="+m_data.m_lastGoodResult.m_targetCount);
			m_finished = true;
			return;
		}

		if (Math.abs(m_data.m_lastGoodResult.m_angle)>AutoData.MinimumAngleToTurnDegree)
		{
			m_toMoveTimeInMsec = AutoData.MoveBaseTime + AutoData.proportionalTime*Math.abs(m_data.m_lastGoodResult.m_angle);
			if (m_data.m_lastGoodResult.m_angle>0)
			{
				m_leftMotorPower = AutoData.TurnRightPowerLeft;
				m_rightMotorPower = AutoData.TurnRightPowerRight;
			}
			else
			{
				m_leftMotorPower = AutoData.TurnLeftPowerLeft;
				m_rightMotorPower = AutoData.TurnLeftPowerRight;
			}
		}
		else 
		{	
			m_toMoveTimeInMsec = AutoData.MoveBaseTime * 1.5;
			m_leftMotorPower = AutoData.MoveStraightPowerLeft;
			m_rightMotorPower = AutoData.MoveStraightPowerRight;
		}
		
		Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("MoveForward, angle="+m_data.m_lastGoodResult.m_angle);
		builder.append(", m_leftMotorPower="+m_leftMotorPower);
		builder.append(", m_rightMotorPower="+m_rightMotorPower);
		builder.append(", m_toMoveTimeInMsec="+m_toMoveTimeInMsec);
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
	}
	
	@Override
	public ActionType GetNextActionType()
	{
		return ActionType.TakePicture;
	}
	
	@Override
	protected String GetLogString()
	{
		return m_string;
	}
	
}
