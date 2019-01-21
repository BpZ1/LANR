package lanr.logic.model;

/**
 * @author Nicolas Bruch
 *
 */
public class LANRFileException extends Exception {

	private static final long serialVersionUID = -5064460493701095648L;

	public LANRFileException(String message) {
		super(message);
	}
	
	public LANRFileException(String message, Throwable t) {
		super(message, t);
	}
	
	public LANRFileException(Throwable t) {
		super(t);
	}
}
