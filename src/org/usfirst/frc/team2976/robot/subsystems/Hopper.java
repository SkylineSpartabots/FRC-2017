package org.usfirst.frc.team2976.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team2976.robot.RobotMap;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Servo;
/**
 *@author NeilHazra
 */
public class Hopper extends Subsystem {
	private CANTalon hopperMotor;
	
	private Servo hopperServoRight;
	private Servo hopperServoLeft;
	 
	public Hopper()	{
    	hopperMotor = new CANTalon(RobotMap.hopperMotor);
       	hopperServoRight = new Servo(RobotMap.hopperServoRight);
    	hopperServoLeft = new Servo(RobotMap.hopperServoLeft);
	}
    
    public void raiseHopper(double power)	{
    	hopperMotor.set(power);
    }
    
    public void lowerHopper(double power)	{
    	hopperMotor.set(power);
    }
    public void setHopperServos(double scaledPosition) {
    	hopperServoRight.set(scaledPosition);
        hopperServoLeft.set(1.0-scaledPosition);	
    	
    }
    public boolean isRaised()	{
    	return false;//beamBreak.get(); //might need to negate this value depending on wiring of sensor
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

