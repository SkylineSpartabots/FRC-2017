package util;

import edu.wpi.first.wpilibj.I2C;
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
		i2c = new I2C(I2C.Port.kOnboard,8 ); 
	}	
	
	//stuff here for I2C comm 
	public void saveData()	{
		
		
		
		
		
		//i2c.transaction(nullArray, 1, dataReceived, 3);
		
		i2c.readOnly(dataReceived, 1);
		
		//i2c.read(44, count, dataRecieved);
		
		SmartDashboard.putNumber("I2C value", dataReceived[1]);

	}	
}
