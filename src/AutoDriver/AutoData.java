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
    FinalMoveForward,
    StartMoveForward,
}


public class AutoData {
	public static final long FinalTimeoutThresholdMs = 9000;
	public static final long MinimumAngleToTurnDegree = 2;	
	
	public static final long FinalStartDistanceInches = 26;
	public static final long FinalStartAngleDegree = 1;
	
	
	public static final long StartMoveTime = 400;
	
	public static final double  StartLeftPowerLeft = .5;
	public static final double  StartLeftPowerRight = .6;
	
	public static final double  StartRightPowerLeft = .6;
	public static final double  StartRightPowerRight = .5;
	
	
	public static final long MoveBaseTime = 250;
	
	public static final double LeftPowerBase = .6;
	public static final double RightPowerBase = .6;
	public static final double Kp = .04;

	
	ActionState m_state = ActionState.StartAction;
	ActionType m_actionType = ActionType.TakePicture;
	public BaseAction m_action = null;
	
	public long m_autoStartTime = 0;
	public long m_autoRunTime = 0;
	public boolean m_finalTimeoutReached = false;
	public boolean m_finalFinished = false;
	
	public StartPosition m_startPosition = StartPosition.Center;
	public ProcessResult m_lastGoodResult;
}
