package lanr.view;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Nicolas Bruch
 *
 */
public class Utils {

	
	/**
	 * Displays an info dialog to the user.
	 * @param header
	 * @param message
	 */
	public static void showInfoDialog(String header, String message) {
		if (header == null || message == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}
	
	/**
	 * Displays an info dialog to the user.
	 * @param header
	 * @param message
	 */
	public static void showInfoDialog(String header, String message, Node content) {
		if (header == null || message == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.getDialogPane().setContent(content);
		alert.setContentText(message);

		alert.showAndWait();
	}
	
	/**
	 * Displays an info dialog to the user.
	 * @param header
	 * @param message
	 */
	public static void showInfoDialog(String titel, String header, String content) {
		if (header == null || header == null || content == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titel);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}

	/**
	 * Displays an error dialog to the user.
	 * @param header
	 * @param message
	 */
	public static void showErrorDialog(String header, String message) {
		if (header == null || message == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}
	
	/**
	 * Displays a confirmation dialog to the user.
	 * @param title
	 * @param header
	 * @param body
	 * @return
	 */
	public static boolean confirmationDialog(String title, String header, String body) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(body);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
		    return false;
		}
	}
	
	/**
	 * Lets the user chose a directory.
	 * @param title
	 * @return Directory chosen by the user, or an empty string.
	 */
	public static String showDirectoryDirectorySelect(String title, Window owner) {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle(title);
		File result = dc.showDialog(owner);
		if(result == null) {
			return null;
		}
		return result.getAbsolutePath();
	}
	
	/**
	 * Rounds a value to the given number of decimal places.
	 * @param value
	 * @param places
	 * @return
	 */
	public static double round(double value, int places) {
	    if (places < 0) {
	    	throw new IllegalArgumentException("Invalid value for number of places");
	    }

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
