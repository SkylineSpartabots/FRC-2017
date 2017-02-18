package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger extends Logger 
{
	private String path;
	private PrintWriter writer=null;
	public FileLogger(String filePath) 
	{
		super();
		path=filePath;
	}
	public void init()
	{
		initialized=true;
		File file = new File(path);
		try
		{
			writer = new PrintWriter(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void println(String s)
	{
		checkInit();
		String ts=getTimeString();
		System.out.println(ts+s);
		writer.println(ts + s);
	}
	public void print(String s)
	{
		checkInit();
		String ts=getTimeString();
		System.out.print(ts+s);
		writer.print(ts + s);
	}
	public void printf(String s, Object ...args)
	{
		checkInit();
		String ts=getTimeString();
		System.out.printf(ts+s, args);
		writer.printf(ts+s, args);
	}
	public void close()
	{
		writer.close();
	}
}
