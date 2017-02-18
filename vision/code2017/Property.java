package code2017;

public class Property 
{
	double heightWidthRatio;
	double moment;
	double coverage;
	double[] xprofile;
	double[] yprofile;
	short defaultPropertyID=0;
	/*
	 * 0 - Non-ideal dynamic property
	 * 1 - Ideal Boiler Top
	 * 2 - Ideal Boiler Bottom
	 * 3 - Ideal Gear
	 */
	
	public Property(double hwr, double m, double c, double[] xp, double[] yp)
	{
		this.heightWidthRatio=hwr;
		this.moment=m;
		this.coverage=c;
		this.xprofile=xp;
		this.yprofile=yp;
	}
	public Property(double hwr, double m, double c, double[] xp, double[] yp, short id)
	{
		this.heightWidthRatio=hwr;
		this.moment=m;
		this.coverage=c;
		this.xprofile=xp;
		this.yprofile=yp;
		this.defaultPropertyID=id;
	}
	public static Property getIdealBoilerTop()
	{
		return new Property(4.0/15.0, 0.0, 1.0, null, null, (short) 1);
	}
	public static Property getIdealBoilerDown()
	{
		return new Property(2.0/15.0, 0.0, 1.0, null, null, (short)2);
	}
	public static Property getIdealGear()
	{
		return new Property(5.0/2.0, 0.0, 1.0, null, null, (short)3);
	}
}
