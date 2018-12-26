package lanr.logic.noise;

public class WindowFunction {

	private final double[] scalars;

	private static final double PI = Math.PI;

	public WindowFunction(int size, FrequencyConversion conversion) {
	        scalars = new double[size];
	        if(conversion == FrequencyConversion.DCT) {
	        	for (int i = 0; i < size; i++) {
		            
		            double xx = Math.sin((PI/(2.0*size)) * (2.0 * i));
		            scalars[i] = Math.sin((PI/2.0) * (xx * xx));
		        }
	        }else if(conversion == FrequencyConversion.FFT) {
	        	for (int i = 0; i < size; i++) {
	        		double xx = Math.sin((PI/(2.0*size)) * (i + 0.5));
		            scalars[i] = Math.sin((PI/2.0) * (xx * xx));
		        }
	        }
	        
	    }

	public void apply(double[] data) {
		if (data.length != scalars.length) {
			throw new IllegalArgumentException("Invalid length. Was " + data.length + " but should be " + scalars.length);
		}
		for (int i = 0; i < data.length; i++) {
			data[i] *= scalars[i];
		}
	}
}
