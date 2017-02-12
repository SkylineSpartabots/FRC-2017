package util;
import org.usfirst.frc.team2976.robot.RobotMap;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.InterruptHandlerFunction;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ADNS_I2C {
	I2C i2c;
	byte[] nullArray = new byte[1];
	byte[]  dataReceived = new byte[3];
	public volatile int xd;
	public volatile int yd;
	public int x_total;
	public int y_total;
	public double theta;
	
	public ADNS_I2C() {
		i2c = new I2C(I2C.Port.kOnboard,44); 
	}	
	public void saveData()	{
		//i2c.transaction(nullArray, 1, dataReceived, 3);
		//i2c.read(44, count, dataRecieved);
		SmartDashboard.putNumber("I2C value", dataReceived[1]);
	}	
	
}
