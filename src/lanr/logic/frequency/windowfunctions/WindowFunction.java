package lanr.logic.frequency.windowfunctions;

public abstract class WindowFunction {

	protected static final double PI = Math.PI;
	
	public abstract void apply(double[] samples);
}
