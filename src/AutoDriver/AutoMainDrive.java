package AutoDriver;

import org.usfirst.frc.team2976.robot.Robot;

import Vision.ProcessResult;
import Vision.TraceLog;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class AutoMainDrive extends Command {
	AutoData m_data = new AutoData();
	long m_runTime = 0;
	
    public AutoMainDrive(StartPosition startPosition) {
    	requires(Robot.drivetrain);
    	m_data.m_startPosition = startPosition;
    }

	protected void initialize()
	{
		m_data.m_autoStartTime = System.currentTimeMillis();
		
		m_data.m_lastGoodResult = new ProcessResult(null, null);
		m_data.m_lastGoodResult.m_distance = 100;
		
		StartAction(new StartMoveForward());
	}
	
	protected void execute()
	{
		TraceLog.Log("Range", "Ultrasonic="+Robot.drivetrain.getDistanceInches());
		
		m_data.m_autoRunTime = System.currentTimeMillis() - m_data.m_autoStartTime;
		if (CheckToStartFinalMoveForward())
		{
			return;
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
			 case FinalMoveForward:
				 StartAction(new FinalMoveForward());
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

	boolean CheckToStartFinalMoveForward()
	{
		if (!m_data.m_finalTimeoutReached){
			
			if (m_data.m_autoRunTime > AutoData.FinalTimeoutThresholdMs) {
				TraceLog.Log("CheckToStartFinalMoveForward", "Start FinalMoveForward() by time"); 
				m_data.m_finalTimeoutReached = true;
				StartAction(new FinalMoveForward());
				return true;
			}
		}
		
		return false;
	}
	
	void StartAction(BaseAction p_action)
	{
		m_data.m_action = p_action;
		p_action.Start(m_data);
		TraceLog.Log("StartAction at "+ m_data.m_autoRunTime, m_data.m_action.GetStartLog());
	}
	
	void WaitForAction()
	{
		m_data.m_action.Execute();
		TraceLog.Log("WaitForAction at " + m_data.m_autoRunTime, m_data.m_action.GetWaitLog());
		if (m_data.m_action.IsFinished())
		{
			m_data.m_action.Stop();
			m_data.m_actionType = m_data.m_action.GetNextActionType();
			m_data.m_state = ActionState.StartAction;
			TraceLog.Log("WaitForAction Finished", 
					"next actionType="+m_data.m_actionType+", ActionState="+m_data.m_state);
		}
	}
    // Called once after isFinished returns true
    protected void end() {
    	TraceLog.Log("AutoMainDrive", "End");
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
	
}
