package org.usfirst.frc.team2976.robot.subsystems;

import org.usfirst.frc.team2976.robot.RobotMap;
import org.usfirst.frc.team2976.robot.commands.ClimbRope;
import org.usfirst.frc.team2976.robot.commands.SpinIntake;

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

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public void setRoller(boolean in, boolean out){
		if (in  && out != true) {
			intakeMotor.set(1); 
		} else if (out && in != true){
			intakeMotor.set(-0.2);
		} else {
			intakeMotor.set(0);
		}
	}
    public void initDefaultCommand() {
    	setDefaultCommand (new SpinIntake());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

