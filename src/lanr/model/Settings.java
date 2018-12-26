package lanr.model;

import lanr.logic.frequency.FrequencyConversion;

/**
 * @author Nicolas Bruch
 * 
 *         Contains variable settings that are saved and used to change the
 *         received output or the performance.
 *
 */
public class Settings {

	private static Settings instance;
	/**
	 * Size of samples that are analyzed of a given signal.
	 */
	private int windowSize = 1024;
	/**
	 * Reduction factor of the visualization of the signal.<br>
	 * 0.01 = 10% of the signals sample content will be displayed.
	 */
	private double visualisationFactor = 0.01;
	/**
	 * Indicates whether a spectrogram will be drawn while analyzing or not.
	 */
	private boolean createSpectrogram = true;
	/**
	 * Indicates whether the vizualisation will be created when analyzing.
	 */
	private boolean showVisualisation = false;
	/**
	 * Contrast for the spectrogram.
	 */
	private int spectrogramContrast = 300;
	
	/**
	 * Number of threads used for analyzing and reading files.
	 */
	private int threadCount = 10;	
	/**
	 * Defines if a window function is used for the frequency data.
	 */
	private boolean usingWindowFunction = true;
	/**
	 * The conversion method to convert the samples into frequency domain.
	 */
	private FrequencyConversion conversionMethod = FrequencyConversion.FFT;

	private Settings() {
	};

	public Settings createSettings(SettingData data) {
		if (instance == null) {
			instance = new Settings();
		}
		if (data.getCreateSpectrogram().isPresent()) {
			createSpectrogram = data.getCreateSpectrogram().get();
		}
		if (data.getFrameSize().isPresent()) {
			windowSize = data.getFrameSize().get();
		}
		if (data.getVisualisationFactor().isPresent()) {
			visualisationFactor = data.getVisualisationFactor().get();
		}
		if (data.getShowVisualisation().isPresent()) {
			showVisualisation = data.getShowVisualisation().get();
		}
		if (data.getSpectrogramContrast().isPresent()) {
			spectrogramContrast = data.getSpectrogramContrast().get();
		}
		if(data.getThreadCount().isPresent()) {
			threadCount = data.getThreadCount().get();
		}
		if(data.getUsingWindowFunction().isPresent()) {
			this.usingWindowFunction = data.getUsingWindowFunction().get();
		}
		if(data.getFrequencyConverter().isPresent()) {
			this.conversionMethod = data.getFrequencyConverter().get();
		}
		return instance;
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int frameSize) {
		this.windowSize = frameSize;
	}

	public double getVisualisationFactor() {
		return visualisationFactor;
	}

	public void setVisualisationFactor(double visualisationFactor) {
		this.visualisationFactor = visualisationFactor;
	}

	public boolean createSpectrogram() {
		return createSpectrogram;
	}

	public void setCreateSpectrogram(boolean createSpectrogram) {
		this.createSpectrogram = createSpectrogram;
	}

	public boolean showVisualisation() {
		return showVisualisation;
	}

	public void setShowVisualisation(boolean showVisualisation) {
		this.showVisualisation = showVisualisation;
	}

	public int getSpectrogramContrast() {
		return spectrogramContrast;
	}

	public void setSpectrogramContrast(int spectrogramContrast) {
		this.spectrogramContrast = spectrogramContrast;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public FrequencyConversion getConversionMethod() {
		return conversionMethod;
	}

	public void setConversionMethod(FrequencyConversion conversionMethod) {
		this.conversionMethod = conversionMethod;
	}

	public boolean isUsingWindowFunction() {
		return usingWindowFunction;
	}

	public void setUsingWindowFunction(boolean usingWindowFunction) {
		this.usingWindowFunction = usingWindowFunction;
	}
}
