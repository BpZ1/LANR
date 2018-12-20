package lanr.view;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;

public class AudioVisualisation extends VBox {
	
	private static final String CSS_ID = "visualisationBackGround";
	
	/**
	 * Contains the channel index and its canvas + graphics context
	 */
	private List<ChannelVisualisation> channelVisuals = new LinkedList<ChannelVisualisation>();


	public AudioVisualisation(double width, double height, AudioData data) {	
		this.setId(CSS_ID);	
		//The canvas are created in advance and drawn to when analyzing
		for (AudioChannel channel : data.getAllChannel()) {
			channelVisuals.add(new ChannelVisualisation(
					width,
					height / data.getAllChannel().size(),
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
