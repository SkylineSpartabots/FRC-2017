package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.ClimbRope;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Climber extends Subsystem {
	private CANTalon climberMotor;
	
	public Climber(){
		climberMotor = new CANTalon(RobotMap.climberMotor);
	}

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public void setClimber(double power){
		climberMotor.set(power); 
	}
    public void initDefaultCommand() {
    	setDefaultCommand (new ClimbRope());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

