package org.usfirst.frc.team2976.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team2976.robot.RobotMap;

import com.ctre.CANTalon;
/**
 *@author NeilHazra
 */
public class Hopper extends Subsystem {
	private CANTalon hopperMotor;
	private DigitalInput beamBreak;
    
	public Hopper()	{
    	hopperMotor = new CANTalon(RobotMap.hopperMotor);
    	beamBreak = new DigitalInput(RobotMap.limitSwitchHopper);
	}
    
    public void raiseHopper(double power)	{
    	hopperMotor.set(power);
    }
    
    public void lowerHopper(double power)	{
    	hopperMotor.set(power);
    }
    
    public boolean isRaised()	{
    	return beamBreak.get(); //might need to negate this value depending on wiring of sensor
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

