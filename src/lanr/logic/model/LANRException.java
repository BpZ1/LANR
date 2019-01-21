package lanr.logic.model;

/**
 * @author Nicolas Bruch
 *
 */
public class LANRException extends Exception {

	private static final long serialVersionUID = -5064460493701095648L;

	public LANRException(String message) {
		super(message);
	}
	
	public LANRException(String message, Throwable t) {
		super(message, t);
	}
	
	public LANRException(Throwable t) {
		super(t);
	}
}
