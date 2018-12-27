package lanr.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Utils {

	
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
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
