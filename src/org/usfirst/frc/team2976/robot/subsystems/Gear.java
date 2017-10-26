
package org.usfirst.frc.team2976.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.LowerGear;
import org.usfirst.frc.team2976.robot.commands.TestLimitSwitch;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Servo;
/**
 *@author NeilHazra
 */
public class Gear extends Subsystem {
	private CANTalon gearIntake;
	
	private CANTalon gearPivot;
    private DigitalInput limitSwitch;
	public Gear()	{
    	gearIntake = new CANTalon(RobotMap.gearIntakeMotor);
    	gearPivot = new CANTalon(RobotMap.gearPivotMotor);
    	limitSwitch = new DigitalInput(RobotMap.limitSwitch);
	}
	
	public boolean getLimitSwitch() {
		return limitSwitch.get();
	}
    
    public void intakeGear(double power)	{
    	gearIntake.set(power);
    }
    
    public void setGearPivot(double power) {
    	gearPivot.set(power);
    }
    
    public boolean isRaised()	{
    	return false;//beamBreak.get(); //might need to negate this value depending on wiring of sensor
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
       setDefaultCommand(new TestLimitSwitch());
    }
}




