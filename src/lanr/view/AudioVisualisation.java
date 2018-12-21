package lanr.view;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;

/**
 * @author Nicolas Bruch
 *
 */
public class AudioVisualisation extends VBox {
	
	private static final String CSS_ID = "visualisationBackGround";

	public AudioVisualisation(double height, AudioData data) {	
		this.setHeight(height);
		this.setId(CSS_ID);	
		double canvasHeight = height / data.getAllChannel().size();
		//The canvas are created in advance and drawn to when analyzing
		for (AudioChannel channel : data.getAllChannel()) {
			this.getChildren().add(new ChannelVisualisation(
					this.getWidth(),
					canvasHeight,
					channel));
		}
	}
	
	private Color getSeverityColor(double severity) {
		if(severity < 0.3 && severity > 0.001) {
			return Color.GREEN;
		}
		if(severity > 0.3 && severity < 0.6) {
			return Color.YELLOW;
		}
		if(severity > 0.6) {
			return Color.RED;
		}
		return Color.BLUE;
	}

}
