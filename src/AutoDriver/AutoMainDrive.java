package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.command.Command;


public class AutoMainDrive extends Command {
	AutoData m_data = new AutoData();
	
    public AutoMainDrive() {
    	requires(Robot.drivetrain);
    }
	
	protected void initialize()
	{
		m_data.m_autoStartTime = System.currentTimeMillis();
		
		m_data.m_lastGoodResult = new ProcessResult(null, null);
		m_data.m_lastGoodResult.m_distance = 100;
		
		StartAction(new TakePicture());
	}
	protected void execute()
	{
		if (!m_data.m_finalTimeoutReached){
			long currentTime = System.currentTimeMillis() - m_data.m_autoStartTime;
			TraceLog.Log("AutoMainDrive CheckFinal", "currentTime=" + currentTime);
			if (currentTime > AutoData.FinalTimeoutThresholdMs) {
				TraceLog.Log("AutoMainDrive ", "Final Reached! currentTime=" + currentTime);
				m_data.m_finalTimeoutReached = true;
				StartAction(new FinalMoveForward());
				return;
			}
		}
		
		if (ActionState.StartAction == m_data.m_state)
		{
			switch (m_data.m_actionType)
			{
			 case TakePicture:
				 StartAction(new TakePicture());
	             break;
			 case MoveForward:
				 StartAction(new MoveForward());
	             break;
			 default:
				TraceLog.Log("AutoMainDrive StartAction  Skip", "Can not start ActionType=" + m_data.m_actionType);
				break;
			}
			return;
		}
		
		if (ActionState.WaitForAction == m_data.m_state)
		{
			WaitForAction();
			return;
		}
		
		TraceLog.Log("AutoMainDrive Exception!!!", "Unknown ActionState="+m_data.m_state);
	}
	
	
	protected boolean isFinished()
	{
		return m_data.m_finalFinished;
	}

	
	void StartAction(BaseAction p_action)
	{
		m_data.m_action = p_action;
		p_action.Start(m_data);
		TraceLog.Log("AutoMainDrive StartAction", m_data.m_action.GetLogString());
	}
	
	void WaitForAction()
	{
		TraceLog.Log("AutoMainDrive WaitForAction", m_data.m_action.GetLogString());
		m_data.m_action.Execute();
		if (m_data.m_action.IsFinished())
		{
			m_data.m_action.Stop();
			m_data.m_actionType = m_data.m_action.GetNextActionType();
			m_data.m_state = ActionState.StartAction;
		}
	}
    // Called once after isFinished returns true
    protected void end() {
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
	
}
