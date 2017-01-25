package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.DriveWithJoystick;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import util.PIDMain;
import util.PIDSource;
import util.RPS;	
/**
 *
 */
public class DriveTrain extends Subsystem  {
	private SpeedController rightFrontMotor, leftFrontMotor;
    private SpeedController rightBackMotor, leftBackMotor;
    public RobotDrive m_drive;
    public PIDMain rotationLock;
    public PIDSource gyroSource;
	public RPS rps;
	
	public DriveTrain()	{
		rps = new RPS();
		
		gyroSource = new PIDSource()	{
			public double getInput() {
				return getHeading();
			}
		};		
		rotationLock = new PIDMain(gyroSource, 0, 100, 0.001, 0, 0);	
		
		rightFrontMotor = new CANTalon(RobotMap.RightFrontDriveMotor);
    	leftFrontMotor = new CANTalon(RobotMap.LeftFrontDriveMotor);
    	rightBackMotor = new CANTalon(RobotMap.RightBackDriveMotor);
    	leftBackMotor = new CANTalon(RobotMap.LeftBackDriveMotor);	
    	
    	m_drive  = new RobotDrive(leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor);
    	m_drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
    	m_drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
	}
	public void initDefaultCommand() {
        setDefaultCommand(new DriveWithJoystick());
    }
    public void drive(double x, double y, double rotation) {
    	m_drive.mecanumDrive_Cartesian(x, y, rotation, 0);
    }
    public void rotationLockDrive(double x, double y)	{
    	m_drive.mecanumDrive_Cartesian(x, y,  rotationLock.getOutput(), 0);
    }
    public double getHeading(){
    	return rps.getAngle();		
    }
}