package lanr.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.model.Tuple;

public class AudioVisualisation extends ScrollPane {

	private double width = 300;
	private double height = 100;

	public AudioVisualisation(AudioData data) {

		drawAudioData(data);
	}

	private void drawAudioData(AudioData data) {
		for (AudioChannel channel : data.getAllChannel()) {
			Canvas canvas = new Canvas();
			GraphicsContext gc = canvas.getGraphicsContext2D();
			if (drawAudioChannel(channel, gc)) {
				this.getChildren().add(canvas);
			}
		}
	}

	private boolean drawAudioChannel(AudioChannel channel, GraphicsContext gc) {
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(5);
		double sampleDistance = this.width / channel.getSamples().length;
		System.out.println("Bitrate: " + channel.getBitRate());
		double maxSampleValue = Math.pow(2, channel.getBitRate()) + 10; // added 10 for buffer
		double heightValue = this.height / maxSampleValue;
		double halfValue = maxSampleValue / 2;
		System.out.println("Height value: " + heightValue);
		System.out.println("Max sample value: " + maxSampleValue);

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
			sampleYPosition = heightValue * sample + halfValue;
			gc.strokeLine(lastPoint.x, lastPoint.y, currentXPosition, sampleYPosition);
			// update last point
			lastPoint.x = currentXPosition;
			lastPoint.y = sampleYPosition;
		}

		return true;
	}

}
