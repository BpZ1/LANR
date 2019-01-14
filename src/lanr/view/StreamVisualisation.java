package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioStream;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

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
	/**
	 * Distance between samples.
	 */
	private final double sampleDistance;
	/**
	 * Height distance multiplier.
	 */
	private final double heightValue;
	/**
	 * Maximum sample size.<br>
	 * Size goes from 100 to -100
	 * or 0 to 200 in the canvas.
	 */
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
		//Number of samples that will be displayed
		this.sampleCount = (long) ((channel.getLength() * channel.getSampleRate()) * visualisationReductionFactor);
		if (channel.getLength() / 3 < parentWidth) {
			this.width = parentWidth;
		} else {
			this.width = channel.getLength() / 3;
		}
		this.setWidth(width);
		this.sampleDistance = width / sampleCount;
		double maxSampleValue = 200;
		this.heightValue = height / maxSampleValue;
		this.halfValue = maxSampleValue / 2;

	}

	private void drawAudioData(double[] data) {
		// Take every Xth element
		int sampleSize = (int) (data.length * visualisationReductionFactor);
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
	}
	
	private void drawNoiseIntervals(List<Noise> noises) {
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setGlobalAlpha(0.2);
		double multiplier = visualisationReductionFactor * sampleDistance;
		for(Noise noise : noises) {
			gc.setFill(getNoiseColor(noise.getType()));
			gc.fillRect(
					noise.getLocation() * multiplier,
					0,
					noise.getLength() * multiplier,
					this.getHeight());
		}
	}
	
	public static Color getNoiseColor(NoiseType type) {
		switch(type) {
			case Background:
				return Color.YELLOW;
				
			case Clipping:
				return Color.RED;
				
			case Hum:
				return Color.GREEN;
				
			case Silence:
				return Color.WHITE;
				
			case Volume:
				return Color.PINK;
		}
		throw new IllegalArgumentException("Invalid noise type");
	}

	private PropertyChangeListener createChangeListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Platform.runLater(() -> {
					switch (evt.getPropertyName()) {
						case AudioStream.DATA_ADDED_PROPERTY:
							double[] dataPacket = (double[]) evt.getNewValue();
							drawAudioData(dataPacket);
							break;
						case AudioStream.ANALYZING_COMPLETE:
							@SuppressWarnings("unchecked") 
							List<Noise> noise = (List<Noise>) evt.getNewValue();
							drawNoiseIntervals(noise);
							break;
					}
				});
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
