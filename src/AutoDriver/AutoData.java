package AutoDriver;

import Vision.ProcessResult;

enum ActionState {
    StartAction,
    WaitForAction,
}

enum ActionType {
	BaseAction,
    TakePicture,
    MoveForward,
    FinalMoveForward
}

public class AutoData {
	public static final long FinalTimeoutThresholdMs = 12000;
	public static final long MinimumAngleToTurnDegree = 2;	
	
	public static final double  TurnLeftPowerLeft = .6;
	public static final double  TurnLeftPowerRight = .42;
	
	public static final double  TurnRightPowerLeft = .42;
	public static final double  TurnRightPowerRight = .6;
	
	public static final double  MoveStraightPowerLeft = .6;
	public static final double  MoveStraightPowerRight = .6;
	
	public static final long MoveBaseTime = 150;
	public static final int proportionalTime = 20;
	// key data
	ActionState m_state = ActionState.StartAction;
	ActionType m_actionType = ActionType.TakePicture;
	public BaseAction m_action = null;
	
	// MoveForward
	public long m_autoStartTime = 0;
	public boolean m_finalTimeoutReached = false;
	public boolean m_finalFinished = false;
	
	// MoveForward
	public ProcessResult m_lastGoodResult;
}
