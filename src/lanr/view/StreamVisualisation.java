package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioStream;

/**
 * Contains methods for the visual representation of an audio channel
 * 
 * @author Nicolas Bruch
 * 
 */
public class StreamVisualisation extends Canvas {	
	private final double width;
	private final AudioStream channel;
	private final long sampleCount;
	private final double sampleDistance;
	private final double maxSampleValue;
	private final double heightValue;
	private final double halfValue;
	/**
	 * Percentage of the frame that will be visualized.
	 */
	public static double visualisationReductionFactor = 0.1;
	private double currentXPosition = 0;
	private double lastYPosition = 0;

	public StreamVisualisation(double parentWidth, double height, AudioStream channel) {
		this.setHeight(height);
		this.channel = channel;
		this.getGraphicsContext2D().setStroke(Color.BLUE);
		this.getGraphicsContext2D().setLineWidth(0.1);
		channel.addChangeListener(createChangeListener());
		this.sampleCount = (long) ((channel.getLength() * channel.getSampleRate()) 
				* visualisationReductionFactor);

		if (channel.getLength() / 3 < parentWidth) {
			this.width = parentWidth;
		} else {
			this.width = channel.getLength() / 3;
		}
		this.setWidth(width);
		this.sampleDistance = width / sampleCount;
		this.maxSampleValue = 200;
		this.heightValue = height / maxSampleValue;
		this.halfValue = maxSampleValue / 2;

	}

	private void drawAudioData(double[] data) {
		Platform.runLater(()->{
			// Take every Xth element
			int sampleSize = (int) (data.length 
					* visualisationReductionFactor);
			int skipDistance = (int) (visualisationReductionFactor * 100);
			double[] samples = new double[sampleSize];
			int counter = 0;
			for (int i = 0; i < samples.length; i++) {	
				samples[i] = data[counter];		
				counter += skipDistance;
			}

			double sampleYPosition;
			for (double sample : samples) {
				double newXPosition = currentXPosition + sampleDistance;
				/*
				 * Half width is added to convert from negative to positive. The height value
				 * multiplier is needed to convert the sample value into the height space of the
				 * canvas.
				 */
				sampleYPosition = (sample * 100 + halfValue) * heightValue;
				this.getGraphicsContext2D().strokeLine(currentXPosition, lastYPosition, newXPosition, sampleYPosition);
				// update last point
				lastYPosition = sampleYPosition;
				currentXPosition = newXPosition;
			}
			this.getGraphicsContext2D().fill();
		});		
	}

	private PropertyChangeListener createChangeListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				double[] dataPacket = (double[]) evt.getNewValue();
				drawAudioData(dataPacket);
			}
		};
		return listener;
	}
	
	public static void setVisualisationReductionFactor(double value) {
		visualisationReductionFactor = value;
	}
	
	public static double getVisualisationReductionFactor() {
		return visualisationReductionFactor;
	}

	public AudioStream getAudioChannel() {
		return channel;
	}

}