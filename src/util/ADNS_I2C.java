package util;
/**
 * @author NeilHazra
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ADNS_I2C {
	I2C i2c;
	
	//byte[] nullArray = new byte[1];
	byte[]  dataReceived = new byte[12];
	
	public volatile int xd;
	public volatile int yd;
	
	public float x_total;
	public float y_total;
	public float theta;
	
	public ADNS_I2C() {
		i2c = new I2C(I2C.Port.kOnboard,8 ); 
	}	
	
	public float getX()	{
		saveData();
		return x_total;
	}
	public float getY()	{
		saveData();
		return y_total;
		
	}
	public float getTheta()	{
		saveData();
		return theta;
	}
	//stuff here for I2C comm 
	private void saveData()	{			
		//i2c.transaction(nullArray, 1, dataReceived, 3);
		i2c.readOnly(dataReceived, 12);
		
		byte[] x_Total = Arrays.copyOfRange(dataReceived, 0, 4);
		byte[] y_Total = Arrays.copyOfRange(dataReceived, 4, 8);
		byte[] thetaByte = Arrays.copyOfRange(dataReceived, 8, 12);
		

		x_total = ByteBuffer.wrap(x_Total).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		y_total = ByteBuffer.wrap(y_Total).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		theta = ByteBuffer.wrap(thetaByte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		
		
		SmartDashboard.putNumber("x", x_total);
		SmartDashboard.putNumber("y", y_total);
		SmartDashboard.putNumber("theta", theta);
	}	
}
		