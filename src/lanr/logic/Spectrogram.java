package lanr.logic;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Spectrogram {

	/**
	 * Defines how many columns will be added to the picture 
	 * if the size goes over the current maximum.
	 */
	private static final int ARRAY_MULTIPLICATOR = 1000;
	private static final double COLOR_MULTIPLIER = 0.0001;
	private final int frameSize;
	/**
	 * Array containing the pixel informations.
	 */
	private int[] pixel;
	/**
	 * Contrast for the spectrogram.
	 * Default = 300
	 */
	private static int contrast = 300;
	private int maxFrames = ARRAY_MULTIPLICATOR;
	private int currentFrame = 0;
	private BufferedImage image;

	public Spectrogram(int frameSize) {
		this.frameSize = frameSize;
		pixel = new int[frameSize * ARRAY_MULTIPLICATOR];
	}
	
	/**
	 * Doubles the size of the pixel array.
	 */
	private void resize() {
		int[] resultArray = new int[pixel.length * 2];
		for(int column = 0; column < maxFrames; column ++) {
			for(int row = 0; row < frameSize; row++) {
				resultArray[column + row * maxFrames * 2] = pixel[column + row * maxFrames];
			}
		}	
		pixel = resultArray;
		maxFrames *= 2;
	}

	/**
	 * Adds one vertical line to the spectrogram.
	 * @param frame - FFT data.
	 */
	public void addFrame(double[] frame) {
		if(currentFrame > maxFrames) {
			resize();			
		}
		for (int i = 0; i < frame.length; i++) {
			int color = colorFor(frame[i]);
			int index = currentFrame + i * maxFrames;
			if (pixel.length > index) {
				pixel[index] = color;
			}
		}
		currentFrame++;
	}

	/**
	 * Returns the color for a given value.
	 * @param val
	 * @return
	 */
	private int colorFor(double val) {
		int greyVal = (int) (contrast * Math.log1p(Math.abs(COLOR_MULTIPLIER * val)));
		greyVal = Math.min(255, Math.max(0, greyVal));
		return (greyVal << 16) | (greyVal << 8) | (greyVal);
	}

	/**
	 * Transforms the image.
	 * @param image
	 * @param at
	 * @return
	 */
	private BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	/**
	 * Changes the size of the image.
	 * @param img
	 * @param newW
	 * @param newH
	 * @return
	 */
	private BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public BufferedImage getImage() {
		if(this.image == null) {
			this.image = new BufferedImage(currentFrame-1, frameSize, BufferedImage.TYPE_INT_RGB);
			int[] pixelData = ( (DataBufferInt) image.getRaster().getDataBuffer() ).getData();
			for(int column = 0; column < image.getWidth(); column ++) {
				for(int row = 0; row < image.getHeight(); row++) {
					pixelData[column + row * image.getWidth()] = pixel[column + row * maxFrames];
				}
			}				
			AffineTransform at = new AffineTransform();
			at.concatenate(AffineTransform.getScaleInstance(1, -1));
			at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
			return createTransformed(image, at);
		}else {
			return image;
		}		
	}
	
	public static void setContrast(int value) {
		contrast = value;
	}
	
	public static int getContrast() {
		return contrast;
	}
}
