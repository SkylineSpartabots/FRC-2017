package util;

public class Kalman {

	private final double q;
	private final double r;
	private double x = 0d;
	private double p = 50d;
	private double k = 0.04d;
	
	public Kalman(double initial_measurement) {
		this(1.4d, 64d, initial_measurement);
	}
	
	public Kalman(double q, double r, double initial_measurement) {
		this.q = q;
		this.r = r;
		this.x = initial_measurement;
	}
	
	public double getPredictedValue(double measurement) {
		p = p + q;
		k = p / (p + r);
		x = x + k * (measurement - x);
		p = (1 - k) * p;
		return x;
	}
}