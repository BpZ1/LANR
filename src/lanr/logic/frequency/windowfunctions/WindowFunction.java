package lanr.logic.frequency.windowfunctions;

/**
 * Contains the different types of window funcions.
 * 
 * @author Nicolas Bruch
 *
 */
public enum WindowFunction {

	None(""),
	Hanning("Hanning"),
	Hamming("Hamming"),
	Kaiser("Kaiser");
	
	private String selected;
	
	private WindowFunction(String selection) {
		this.selected = selection;
	}
	
	/**
	 * @param windowSize - Size of the window.
	 * @return Implementation for the selected window.
	 */
	public WindowFunctionImpl getImplementation(int windowSize) {
		switch(this.selected) {
			case "Hanning":
				return new VonHannWindow(windowSize);
				
			case "Hamming":
				return new HammingWindow(windowSize);
				
			case "Kaiser":
				return new KaiserWindow(windowSize);
				
			default:
				return null;
		}	
	}
}
