package lanr.view;

import javafx.scene.layout.VBox;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;

/**
 * @author Nicolas Bruch
 *
 */
public class AudioVisualisation extends VBox {
	
	private static final String CSS_ID = "visualisationBackGround";

	public AudioVisualisation(double height, double minWidth, AudioData data) {	
		this.setHeight(height);
		this.setId(CSS_ID);	
		double canvasHeight = height / data.getChannel().size();
		//The canvas are created in advance and drawn to when analyzing
		for (AudioChannel channel : data.getChannel()) {
			this.getChildren().add(new ChannelVisualisation(
					minWidth,
					canvasHeight,
					channel));
		}
	}
}
