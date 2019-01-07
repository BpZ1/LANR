package lanr.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lanr.logic.model.AudioStream;
import lanr.logic.model.AudioData;

/**
 * @author Nicolas Bruch
 *
 */
public class AudioVisualisation extends ScrollPane {
	
	private static final String CSS_ID = "visualisationBackGround";

	public AudioVisualisation(double height, double minWidth, AudioData data) {	
		this.setVbarPolicy(ScrollBarPolicy.NEVER);
		this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setHeight(height);
		this.setId(CSS_ID);
		double canvasHeight = height / data.getStreams().size();
		VBox content = new VBox();		
		//The canvas are created in advance and drawn to when analyzing
		for (AudioStream stream : data.getStreams()) {
			content.getChildren().add(new StreamVisualisation(
					minWidth,
					canvasHeight,
					stream));
		}
		this.setContent(content);
	}
}
