package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class ParameterSettingsView extends GridPane {
	
	public ParameterSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);
	}
	
	
}
