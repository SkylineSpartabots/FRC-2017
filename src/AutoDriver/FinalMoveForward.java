package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.TraceLog;

public class FinalMoveForward extends BaseAction {
	double m_angle = 0;
	double m_distance = 50;
	
	double m_leftMotorPower = 0;
	double m_rightMotorPower = 0;
	double m_toMoveTimeInMsec = 0;
	
	public FinalMoveForward()
	{
		m_actionType = ActionType.FinalMoveForward;
	}
	
	@Override
	public void Start(AutoData p_data)
	{
		super.Start(p_data);	

		m_angle = m_data.m_lastGoodResult.m_angle;
		m_distance = m_data.m_lastGoodResult.m_distance;
		
		m_leftMotorPower = AutoData.LeftPowerBase;
		m_rightMotorPower = AutoData.RightPowerBase;
		
		
			m_toMoveTimeInMsec = 3000;
		
		
		Robot.drivetrain.tankDrive(m_leftMotorPower, m_rightMotorPower);
	}
	
	@Override
	public void Execute()
	{
		super.Execute();
		TraceLog.Log("FinalMoveForward", "Execute,actionRunPeriod= "+m_actionRunPeriod+"toMoveTimeInMsec="+m_toMoveTimeInMsec);
		if (m_actionRunPeriod > m_toMoveTimeInMsec){
			m_finished = true;
			TraceLog.Log("FinalMoveForward", "finished!!!");
		}
	}
		
	@Override
	public void Stop()
	{
		Robot.drivetrain.tankDrive(0, 0);
		m_data.m_finalFinished = true;
		TraceLog.Log("FinalMoveForward", "Stop");
	}
	
	@Override
	public ActionType GetNextActionType()
	{
		return ActionType.BaseAction;
	}
	
	public String GetStartLog()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ActionType="+m_actionType);
		builder.append(", angle="+TraceLog.Round2(m_angle));
		builder.append(", distance="+TraceLog.Round2(m_distance));
		builder.append(", leftMotorPower="+m_leftMotorPower);
		builder.append(", rightMotorPower="+m_rightMotorPower);
		builder.append(", toMoveTimeInMsec="+m_toMoveTimeInMsec);
		return builder.toString();
	}	
}
