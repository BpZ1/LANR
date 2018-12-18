package lanr.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.model.Tuple;

public class AudioVisualisation extends VBox {

	private double width = 800;
	private double height = 200;

	public AudioVisualisation(AudioData data) {

		drawAudioData(data);
		this.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
	}

	private void drawAudioData(AudioData data) {
		for (AudioChannel channel : data.getAllChannel()) {
			Canvas canvas = new Canvas(width, height / data.getAllChannel().size());
			GraphicsContext gc = canvas.getGraphicsContext2D();
			if (drawAudioChannel(channel, gc)) {
				this.getChildren().add(canvas);
			}
		}
	}

	private boolean drawAudioChannel(AudioChannel channel, GraphicsContext gc) {
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(0.1);

		double sampleDistance = this.width / channel.getSamples().length;
		double maxSampleValue = Math.pow(2, channel.getBitDepth()) + 10; // added 10 for buffer
		double heightValue = this.height / maxSampleValue;
		double halfValue = maxSampleValue / 2;

		double[] samples = channel.getSamples();
		if (samples.length < 1) {
			return false;
		}

		double currentXPosition = 0.0;
		Tuple<Double, Double> lastPoint = new Tuple<Double, Double>(currentXPosition,
				heightValue * samples[0] + maxSampleValue);
		// Draw all lines for the samples
		double sampleYPosition = 0;
		for (double sample : channel.getSamples()) {
			currentXPosition += sampleDistance;
			sampleYPosition = (sample + halfValue) * heightValue;
			gc.strokeLine(lastPoint.x, lastPoint.y, currentXPosition, sampleYPosition);
			// update last point
			lastPoint.x = currentXPosition;
			lastPoint.y = sampleYPosition;
		}

		gc.stroke();
		return true;
	}

}
