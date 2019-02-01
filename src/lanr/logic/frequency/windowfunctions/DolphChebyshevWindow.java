package lanr.logic.frequency.windowfunctions;

public class DolphChebyshevWindow extends WindowFunctionImpl {

	private final int length;
	private final double beta;
	/**
	 * -20 * alpha defines the value of the sidelobes in dBFS.
	 */
	private final double alpha = 5.0;
	
	private final double[] scalars;
	
	public DolphChebyshevWindow(double[] samples) {
		this.scalars = new double[samples.length];
		this.length = samples.length;
		this.beta = Math.cosh((1.0 / length) * (1.0 / Math.cosh(Math.pow(10, alpha))));
		for(int i = 0; i < samples.length; i++) {
			scalars[i] =  calculateScalar(i, samples);
		}
	}
	
	private double calculateScalar(int n, double[] samples) {
		double sumResult = 0.0;
		for(int i = 0; i < samples.length - 1; i++) {
			//sumResults += zeroPhase(i) * Math.pow(Math.E, (I * 2 * Math.PI * k * n) / length);
		}
		return (1 / length) * sumResult;
		
	}
	
	private double zeroPhase(double k) {
		double numerator = Math.cos(length * (1.0 / Math.cos(beta * Math.cos((Math.PI / k) / length))));
		double denominator = Math.cosh(length * (1 / Math.cosh(beta)));
		return numerator / denominator; 
	}
	
	@Override
	public void apply(double[] samples) {
		if (samples.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + samples.length + " but should be " + scalars.length);
		}
		
	}

}
