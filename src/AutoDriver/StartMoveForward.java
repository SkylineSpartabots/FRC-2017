package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;

public class StartMoveForward extends BaseAction {

	double m_leftMotorPower = 0;
	double m_rightMotorPower = 0;
	double m_toMoveTimeInMsec = 0;
	
	public StartMoveForward()
	{
		m_actionType = ActionType.StartMoveForward;
	}

	@Override
	public void Start(AutoData p_data)
	{
		super.Start(p_data);	
		TraceLog.Log("StartMoveForward", "startPosition="+m_data.m_startPosition);

		if (m_data.m_startPosition == StartPosition.LeftSide) 
		{
			m_toMoveTimeInMsec = AutoData.StartMoveTime;
			m_leftMotorPower = AutoData.StartLeftPowerLeft;
			m_rightMotorPower = AutoData.StartLeftPowerRight;
			Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
		} 
		else if (m_data.m_startPosition == StartPosition.RightSide) 
		{
			m_toMoveTimeInMsec = AutoData.StartMoveTime;
			m_leftMotorPower = AutoData.StartRightPowerLeft;
			m_rightMotorPower = AutoData.StartRightPowerRight;
			Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
		} 	
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
		builder.append(", startPosition="+m_data.m_startPosition);
		builder.append(", leftMotorPower="+m_leftMotorPower);
		builder.append(", rightMotorPower="+m_rightMotorPower);
		builder.append(", toMoveTimeInMsec="+m_toMoveTimeInMsec);
		return builder.toString();
	}

}
