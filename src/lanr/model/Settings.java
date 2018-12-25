package lanr.model;

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
	private static int frameSize = 1024;
	/**
	 * Reduction factor of the visualization of the signal.<br>
	 * 0.01 = 10% of the signals sample content will be displayed.
	 */
	private static double visualisationFactor = 0.01;
	/**
	 * Indicates whether a spectrogram will be drawn while analyzing or not.
	 */
	private static boolean createSpectrogram = false;
	/**
	 * Indicates whether the vizualisation will be created when analyzing.
	 */
	private static boolean showVisualisation = false;
	/**
	 * Contrast for the spectrogram.
	 */
	private static int spectrogramContrast = 300;

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
			frameSize = data.getFrameSize().get();
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
		return instance;
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		Settings.frameSize = frameSize;
	}

	public double getVisualisationFactor() {
		return visualisationFactor;
	}

	public void setVisualisationFactor(double visualisationFactor) {
		Settings.visualisationFactor = visualisationFactor;
	}

	public boolean createSpectrogram() {
		return createSpectrogram;
	}

	public void setCreateSpectrogram(boolean createSpectrogram) {
		Settings.createSpectrogram = createSpectrogram;
	}

	public boolean showVisualisation() {
		return showVisualisation;
	}

	public static void setShowVisualisation(boolean showVisualisation) {
		Settings.showVisualisation = showVisualisation;
	}

	public static int getSpectrogramContrast() {
		return spectrogramContrast;
	}

	public static void setSpectrogramContrast(int spectrogramContrast) {
		Settings.spectrogramContrast = spectrogramContrast;
	}

	public static void setInstance(Settings instance) {
		Settings.instance = instance;
	}
}
