package lanr.logic;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Class for creating a spectrogram. After a window size has been
 * defined, windows can be added without limits. The
 * {@link BufferedImage} will be generated when the getImage() method is
 * called.
 * 
 * @author Nicolas Bruch 
 *
 */
public class Spectrogram {

	/**
	 * Defines how many columns will be added to the picture if the size goes over
	 * the current maximum.
	 */
	private static final int ARRAY_MULTIPLICATOR = 1000;
	private static final double COLOR_MULTIPLIER = 0.1;
	private final int windowSize;
	/**
	 * Array containing the pixel informations.
	 */
	private int[] pixel;
	/**
	 * Contrast for the spectrogram. Default = 300
	 */
	private static int contrast = 300;
	private static double scale = 1;
	private int maxFrames = ARRAY_MULTIPLICATOR;
	private int currentFrame = 0;
	private BufferedImage image;

	public Spectrogram(int windowSize) {
		this.windowSize = windowSize;
		pixel = new int[windowSize * ARRAY_MULTIPLICATOR];
	}

	/**
	 * Doubles the size of the pixel array.
	 */
	private void resize() {
		int[] resultArray = new int[pixel.length * 2];
		for (int column = 0; column < maxFrames; column++) {
			for (int row = 0; row < windowSize; row++) {
				resultArray[column + row * maxFrames * 2] = pixel[column + row * maxFrames];
			}
		}
		pixel = resultArray;
		maxFrames *= 2;
	}

	/**
	 * Adds one vertical line to the spectrogram.
	 * 
	 * @param window - FFT data.
	 */
	public void addWindow(double[] window) {
		if (currentFrame > maxFrames) {
			resize();
		}
		for (int i = 0; i < window.length; i++) {
			int color = colorFor(window[i]);
			int index = currentFrame + i * maxFrames;
			if (pixel.length > index) {
				pixel[index] = color;
			}
		}
		currentFrame++;
	}

	/**
	 * Returns the color for a given value.
	 * 
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
	 * 
	 * @param image - Image to be transformed.
	 * @param at - Transform
	 * @return Transformed image
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
	 * 
	 * @param img
	 * @param newW
	 * @param newH
	 * @return
	 */
	private BufferedImage resize(BufferedImage img, double scale) {
		int newWidth = (int) (img.getWidth() * scale);
		int newHeight = (int) (img.getHeight() * scale);
		Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public BufferedImage getImage() {
		if (this.image == null) {
			this.image = new BufferedImage(currentFrame - 1, windowSize, BufferedImage.TYPE_INT_RGB);
			int[] pixelData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			for (int column = 0; column < image.getWidth(); column++) {
				for (int row = 0; row < image.getHeight(); row++) {
					pixelData[column + row * image.getWidth()] = pixel[column + row * maxFrames];
				}
			}
			AffineTransform at = new AffineTransform();
			at.concatenate(AffineTransform.getScaleInstance(1, -1));
			at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
			if(scale != 1) {
				image = resize(image, scale);
			}
			return createTransformed(image, at);
		} else {
			return image;
		}
	}

	public static void setContrast(int value) {
		contrast = value;
	}

	public static int getContrast() {
		return contrast;
	}
	
	public static void setScale(double value) {
		scale = value;
	}
	
	public static double getScale() {
		return scale;
	}
}
