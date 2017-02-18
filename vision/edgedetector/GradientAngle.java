package edgedetector;

public enum GradientAngle
{
	GA0,
	GA90,
	GA135,
	GA45;
	public static GradientAngle getAngle(double x, double y)
	{
		final double EIGHTH_PI=180.0/8.0;//I know this is in degrees. That is intentional.
		double angle=Math.atan2(y, x);
		angle=Math.toDegrees(angle);
		if(angle<0)
		{
			angle=angle+180.0;
		}
		int sector=(int)(Math.floor(angle/(EIGHTH_PI)));
		switch(sector)
		{
			case 0:
			case 7:
			case 8://Weird scenario where angle is exactly PI radians or 180 degrees
				return GA0;
			case 1:
			case 2:
				return GA45;
			case 3:
			case 4:
				return GA90;
			case 5:
			case 6:
				return GA135;
			default:
				System.out.println("WARNING: Integer improperly bounded. Code improper. Expect incorrect results. Location: Rounding angle switch statement.");
				break;
		}
		return null;
	}
	
}
