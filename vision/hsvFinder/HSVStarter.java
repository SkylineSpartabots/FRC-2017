package hsvFinder;

import java.util.Scanner;


public class HSVStarter implements Runnable
{
	public static void main(String[] args)
	{
		HSVStarter ss=new HSVStarter();
		ss.doStuff();
	}
	HSVProcessor sp=new HSVProcessor();
	public void doStuff()
	{
		Thread t=new Thread(this);
		t.start();
		sp.start();
	}
	@Override
	public void run()
	{
		Scanner s=new Scanner(System.in);
		while(true)
		{
			sp.imageNumber=s.nextInt();
		}
		
	}
}
