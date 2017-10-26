package org.usfirst.frc.team2976.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.usfirst.frc.team2976.robot.commands.GearIn;
import org.usfirst.frc.team2976.robot.commands.GearOut;
import org.usfirst.frc.team2976.robot.commands.LiftGear;
import org.usfirst.frc.team2976.robot.commands.LowerGear;
import org.usfirst.frc.team2976.robot.commands.IntakeGear;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

public class OI {
	public Joystick driveStick;
	public Joystick secondStick;

	public enum Button {
		RBumper(6), LBumper(5), A(1), B(2), X(3), Y(4), RightJoystickBtn(10), LeftJoystickBtn(9);

		private final int number;

		Button(int number) {
			this.number = number;
		}

		public int getBtnNumber() {
			return number;
		}
	}

	public enum Axis {
		LX(0), LY(1), LTrigger(2), RTrigger(3), RX(4), RY(5), X(0), Y(1), Z(2);
		private final int number;

		Axis(int number) {
			this.number = number;
		}

		public int getAxisNumber() {
			return number;
		}
	}

	public OI() {
		driveStick = new Joystick(0);
		secondStick = new Joystick(1);
		
		//new JoystickButton(driveStick, OI.Button.A.getBtnNumber()).whenPressed(new TimedDrive(50,0.5,true));
		//new JoystickButton(driveStick, OI.Button.B.getBtnNumber()).whenPressed(new TimedDrive(50,-0.5,true));
		new JoystickButton(driveStick, OI.Button.A.getBtnNumber()).whenPressed(new LowerGear()); //lowering the gear
		new JoystickButton(driveStick, OI.Button.Y.getBtnNumber()).whenPressed(new LiftGear()); //lifting the gear
		new JoystickButton(driveStick, OI.Button.B.getBtnNumber()).whileHeld(new IntakeGear(-0.6)); //in
		new JoystickButton(driveStick, OI.Button.X.getBtnNumber()).whileHeld(new IntakeGear(0.6)); //out
		new JoystickButton(driveStick, OI.Button.LBumper.getBtnNumber()).whileHeld(new GearOut()); //out
		new JoystickButton(driveStick, OI.Button.RBumper.getBtnNumber()).whileHeld(new GearIn()); //out
		
	}
}