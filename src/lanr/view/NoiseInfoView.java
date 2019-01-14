package lanr.view;

import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lanr.logic.model.NoiseType;

public class NoiseInfoView extends Stage {

	public NoiseInfoView() {
		Accordion root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 300, 250);
		this.setScene(scene);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
	}
	
	private Accordion createRootPane() {
		Accordion ac = new Accordion();
		for(NoiseType type : NoiseType.values()) {
			ac.getPanes().add(createNoiseTypePane(type));			
		}
		return ac;
	}
	
	private TitledPane createNoiseTypePane(NoiseType type) {
		TitledPane tp = new TitledPane();
		tp.setText(type.toString());
		Rectangle rect = new Rectangle(12,12);
		rect.setFill(StreamVisualisation.getNoiseColor(type));
		tp.setGraphic(rect);
		Label description = new Label();
		description.setText(type.getDefinition());
		description.setWrapText(true);
		tp.setContent(description);
		return tp;
	}
}
