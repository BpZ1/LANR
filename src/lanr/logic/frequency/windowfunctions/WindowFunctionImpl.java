package lanr.logic.frequency.windowfunctions;

/**
 * The window function is used to reduce
 * the spectral leakage in the frequency data
 * that is created after using this function.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class WindowFunctionImpl {

	protected static final double PI = Math.PI;
	
	/**
	 * Applies the function on the given samples.
	 * @param samples - Samples of a signal.
	 */
	public abstract void apply(double[] samples);
}
