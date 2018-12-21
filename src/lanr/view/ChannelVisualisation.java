package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import lanr.logic.model.AudioChannel;

/**
 * @author Nicolas Bruch
 * 
 *         Contains methods for the visual representation of an audio channel
 *
 */
public class ChannelVisualisation extends Canvas {

	private final double width;
	private final double height;
	private final AudioChannel channel;
	private final long sampleCount;
	private final double sampleDistance;
	private final double maxSampleValue;
	private final double heightValue;
	private final double halfValue;
	private double currentXPosition = 0;
	private double lastYPosition = 0;

	public ChannelVisualisation(double parentWidth, double height, AudioChannel channel) {
		this.height = height;	
		this.setHeight(height);
		this.channel = channel;
		this.getGraphicsContext2D().setStroke(Color.BLUE);
		this.getGraphicsContext2D().setLineWidth(0.1);
		channel.addChangeListener(createChangeListener());
		this.sampleCount = (channel.getLength() * channel.getSampleRate())
				/ AudioChannel.VISUALISATION_REDUCTION_FACTOR;
		
		if(channel.getLength() / 3 < parentWidth) {
			this.width = parentWidth;
		}else {
			this.width = channel.getLength() / 3;
		}
		this.setWidth(width);
		this.sampleDistance = width / sampleCount;
		this.maxSampleValue = Math.pow(2, channel.getBitDepth()) + 10; // added 10 for buffer
		this.heightValue = height / maxSampleValue;
		this.halfValue = maxSampleValue / 2;

	}
	
	private void drawAudioData(short[] data) {
		double sampleYPosition;
		for (short sample : data) {
			double newXPosition = currentXPosition + sampleDistance;
			/*
			 * Half width is added to convert from negative to positive. The height value
			 * multiplier is needed to convert the sample value into the height space of the
			 * canvas.
			 */
			sampleYPosition = (sample + halfValue) * heightValue;
			this.getGraphicsContext2D().strokeLine(
					currentXPosition,
					lastYPosition,
					newXPosition,
					sampleYPosition);
			// update last point
			lastYPosition = sampleYPosition;
			currentXPosition = newXPosition;
		}
		this.getGraphicsContext2D().fill();
	}

	private PropertyChangeListener createChangeListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				short[] dataPacket = (short[]) evt.getNewValue();
				drawAudioData(dataPacket);
			}
		};
		return listener;
	}

	public AudioChannel getAudioChannel() {
		return channel;
	}

}
