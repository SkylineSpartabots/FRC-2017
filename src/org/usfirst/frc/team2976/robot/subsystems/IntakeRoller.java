package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeRoller extends Subsystem {
	private CANTalon intakeMotor;
	
	public IntakeRoller(){
		intakeMotor = new CANTalon(RobotMap.intakeMotor);
	}
	
	public void setRoller(double power){
			intakeMotor.set(power); 	
	}
    public void initDefaultCommand() {
    }
}
