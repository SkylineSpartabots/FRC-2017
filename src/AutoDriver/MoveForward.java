package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;

enum MoveForwardDirection {
	Left,
    Center,
    Right
}

public class MoveForward extends BaseAction {
	
	MoveForwardDirection m_direction = MoveForwardDirection.Center;
	double m_angle = 0;
	double m_distance = 50;
	double m_leftMotorPower = 0;
	double m_rightMotorPower = 0;
	double m_toMoveTimeInMsec = 0;
	
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
		
		m_angle = m_data.m_lastGoodResult.m_angle;
		m_distance = m_data.m_lastGoodResult.m_distance;
		
		m_toMoveTimeInMsec = AutoData.MoveBaseTime;
		
		m_leftMotorPower = AutoData.MovePowerBase - m_angle* AutoData.Kp;
		m_rightMotorPower = AutoData.MovePowerBase + m_angle* AutoData.Kp;
		
		Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
	}
	
	@Override
	public void Execute() 
	{
		super.Execute();
		Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
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
	
	public String GetStartLog()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ActionType="+m_actionType);
		builder.append(", direction=" + m_direction);
		builder.append(", angle="+TraceLog.Round2(m_angle));
		builder.append(", distance="+TraceLog.Round2(m_distance));
		builder.append(", leftMotorPower="+TraceLog.Round3(m_leftMotorPower));
		builder.append(", rightMotorPower="+TraceLog.Round3(m_rightMotorPower));
		builder.append(", toMoveTimeInMsec="+m_toMoveTimeInMsec);
		return builder.toString();
	}
}
