package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.Climb;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Climber extends Subsystem {
	private Talon climberMotor;
	
	public Climber(){
		climberMotor = new Talon(RobotMap.climberMotor);
	}
	public void setClimber(double power){
		climberMotor.set(power); 
		SmartDashboard.putNumber("ClimberMotor set", power);
	}
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new Climb());
    }
}