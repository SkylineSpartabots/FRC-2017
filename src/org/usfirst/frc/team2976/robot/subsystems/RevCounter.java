package org.usfirst.frc.team2976.robot.subsystems;
import java.util.Timer;
import java.util.TimerTask;
import org.usfirst.frc.team2976.robot.RobotMap;
import com.ctre.*;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.command.Subsystem;
import util.PIDMain;
import util.PIDSource;
/**
 *
 */
public class RevCounter extends Subsystem {
	private Timer velocityTimer;
    int samplingTime = 100;
	private CANTalon wheel;
	private Counter revCounter;
	public PIDSource wheelSource;
	public PIDMain wheelShooterPID;
	int prevValue = 0;
	int deltaPos;
	public RevCounter()	{
		double right_kp = 0.0003;
		double right_ki = 0;
		double right_kd = 0;
		wheel = new CANTalon(6); //?????????
		revCounter = new Counter(RobotMap.wheelRevCounter);
		velocityTimer = new Timer();// create the timer
		velocityTimer.scheduleAtFixedRate(new VelocityCompute(), 0, samplingTime);
		wheelSource = new PIDSource() {
			public double getInput() {
				return getVelocity();
			}
		};
	wheelShooterPID = new PIDMain(wheelSource, 0, samplingTime, right_kp, right_ki, right_kd);
	}
	public void setWheelPID(double setpoint) {
		wheelShooterPID.setSetpoint(setpoint);
		wheel.set(wheelShooterPID.getOutput());
	}
	public void setMotor(double x)	{
		wheel.set(Math.abs(x));
	}
	
	public int getCount()	{
		return revCounter.get();
	}
	
	public int getVelocity()	{
		int velocity;
		velocity = deltaPos;
		if(Math.signum(wheel.getOutputVoltage()) == -1)	{ //if the motor is going backwards, account for it
			velocity *= -1;
		}
		return velocity;
	}
	public class VelocityCompute extends TimerTask {
		public void run() {
			deltaPos = (getCount()-prevValue)*10000/samplingTime; 
			// divide by 6 and multiply by 60 and by then by 1000/samplingtime
			
			if (getCount()>10000)	{
				revCounter.reset();
			}
			prevValue = getCount();
		}
	}
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

