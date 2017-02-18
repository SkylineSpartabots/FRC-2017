package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger 
{
	protected boolean initialized=false;
	public Logger() 
	{
		initialized=false;
		
	}
	public void init()
	{
		initialized=true;
	}
	public void println(String s)
	{
		checkInit();
		System.out.println(getTimeString() + s);
	}
	public void print(String s)
	{
		checkInit();
		System.out.print(getTimeString() + s);
	}
	public void printf(String s, Object ...args)
	{
		checkInit();
		System.out.printf(getTimeString()+s, args);
	}
	public void close()
	{
		
	}
	protected String getTimeString()
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate+"\t";
	}
	protected void checkInit()
	{
		if(initialized)
		{
			return;
		}
		else
		{
			init();
		}
	}
}
