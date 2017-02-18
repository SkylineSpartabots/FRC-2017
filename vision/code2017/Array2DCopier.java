package code2017;

import java.util.Arrays;

public class Array2DCopier 
{
	public static boolean[][] copyOf(boolean[][] original)
	{
		boolean[][] copy = new boolean[original.length][];
	    for (int i = 0; i < original.length; i++) 
	    {
	        copy[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return copy;
	}
	public static double[][] copyOf(double[][] original)
	{
		double[][] copy = new double[original.length][];
	    for (int i = 0; i < original.length; i++) 
	    {
	        copy[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return copy;
	}
}
