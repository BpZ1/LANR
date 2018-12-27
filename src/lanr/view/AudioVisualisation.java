package lanr.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lanr.logic.model.AudioChannel;
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
		double canvasHeight = height / data.getChannel().size();
		VBox content = new VBox();		
		//The canvas are created in advance and drawn to when analyzing
		for (AudioChannel channel : data.getChannel()) {
			content.getChildren().add(new ChannelVisualisation(
					minWidth,
					canvasHeight,
					channel));
		}
		this.setContent(content);
	}
}
