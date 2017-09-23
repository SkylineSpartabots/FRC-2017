package org.usfirst.frc.team2976.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team2976.robot.RobotMap;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Servo;
/**
 *@author NeilHazra
 */
public class Gear extends Subsystem {
	private Jaguar gearMotor;
	
	private CANTalon gearPivot;
	 
	public Gear()	{
    	gearMotor = new Jaguar(RobotMap.gearMotor);
       	gearPivot= new CANTalon(RobotMap.gearPivotMotor);

	}
    
    public void intakeGear(double power)	{
    	gearMotor.set(power);
    }
    
    public void setGearPivot(double power) {
    	gearPivot.set(power);
    	//gearServoLeft.set(scaledPosition);	
    	
    }
    public boolean isRaised()	{
    	return false;//beamBreak.get(); //might need to negate this value depending on wiring of sensor
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

