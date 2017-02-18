package scoreFinder;

import java.util.Scanner;

public class ScoreStarter implements Runnable
{

	public static void main(String[] args)
	{
		ScoreStarter ss=new ScoreStarter();
		ss.doStuff();
	}
	ScoreProcessor sp=new ScoreProcessor();
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
