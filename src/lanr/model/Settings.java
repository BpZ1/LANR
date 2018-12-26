package lanr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import lanr.logic.frequency.FrequencyConversion;

/**
 * Contains variable settings that are saved and used to change the received
 * output or the performance.
 * 
 * @author Nicolas Bruch
 * 
 */
public class Settings {

	private static final String SETTINGS_FILE_NAME = "settings.ini";

	private static Settings instance;
	/**
	 * Size of samples that are analyzed of a given signal.
	 */
	private Tuple<String, Integer> windowSize = 
			new Tuple<String, Integer>(WINDOW_SIZE_PROPERTY_NAME, 1024);
	private static final String WINDOW_SIZE_PROPERTY_NAME = "windowsize";
	/**
	 * Reduction factor of the visualization of the signal.<br>
	 * 0.01 = 10% of the signals sample content will be displayed.
	 */
	private Tuple<String, Double> visualisationFactor =
			new Tuple<String, Double>(VISUALIZATION_FACTOR_PROPERTY_NAME, 0.01);
	private static final String VISUALIZATION_FACTOR_PROPERTY_NAME = "visualisationfactor";
	/**
	 * Indicates whether a spectrogram will be drawn while analyzing or not.
	 */
	private Tuple<String, Boolean> createSpectrogram =
			new Tuple<String, Boolean>(CREATE_SPECTRO_PROPERTY_NAME, true);
	private static final String CREATE_SPECTRO_PROPERTY_NAME = "createspectrogram";
	/**
	 * Indicates whether the vizualisation will be created when analyzing.
	 */
	private Tuple<String, Boolean> showVisualisation =
			new Tuple<String, Boolean>(SHOW_VISUAL_PROPERTY_NAME, true);
	private static final String SHOW_VISUAL_PROPERTY_NAME = "showvisualisation";
	/**
	 * Contrast for the spectrogram.
	 */
	private Tuple<String, Integer> spectrogramContrast = 
			new Tuple<String, Integer>(SPECTRO_CONTRAST_PROPERTY_NAME, 300);
	private static final String SPECTRO_CONTRAST_PROPERTY_NAME = "spectrogramcontrast";
	/**
	 * Number of threads used for analyzing and reading files.
	 */
	private Tuple<String, Integer> threadCount = 
			new Tuple<String, Integer>(THREAD_COUNT_PROPERTY_NAME, 10);
	private static final String THREAD_COUNT_PROPERTY_NAME = "threadcount";
	/**
	 * Defines if a window function is used for the frequency data.
	 */
	private Tuple<String, Boolean> usingWindowFunction =
			new Tuple<String, Boolean>(USING_WINDOWFUNCTION_PROPERTY_NAME, true);
	private static final String USING_WINDOWFUNCTION_PROPERTY_NAME = "usingwindowfunction";
	/**
	 * The conversion method to convert the samples into frequency domain.
	 */
	private Tuple<String, FrequencyConversion> conversionMethod =
			new Tuple<String, FrequencyConversion>(
					CONVERSION_METHOD_PROPERTY_NAME, FrequencyConversion.FFT);
	private static final String CONVERSION_METHOD_PROPERTY_NAME = "conversionmethod";

	private Settings() {};

	public static Settings createSettings(SettingData data) {
		if (instance == null) {
			instance = new Settings();
		}
		if (data.getCreateSpectrogram().isPresent()) {
			instance.setCreateSpectrogram(data.getCreateSpectrogram().get());
		}
		if (data.getWindowSize().isPresent()) {
			instance.setWindowSize(data.getWindowSize().get());
		}
		if (data.getVisualisationFactor().isPresent()) {
			instance.setVisualisationFactor(data.getVisualisationFactor().get());
		}
		if (data.getShowVisualisation().isPresent()) {
			instance.setShowVisualisation(data.getShowVisualisation().get());
		}
		if (data.getSpectrogramContrast().isPresent()) {
			instance.setSpectrogramContrast(data.getSpectrogramContrast().get());
		}
		if (data.getThreadCount().isPresent()) {
			instance.setThreadCount(data.getThreadCount().get());
		}
		if (data.getUsingWindowFunction().isPresent()) {
			instance.setUsingWindowFunction(data.getUsingWindowFunction().get());
		}
		if (data.getFrequencyConverter().isPresent()) {
			instance.setConversionMethod(data.getFrequencyConverter().get());
		}
		return instance;
	}

	/**
	 * Returns the instance of this class, or null
	 * if it was not created via the create() method.
	 * @return Settings instance or null
	 */
	public static Settings getInstance() {
		return instance;
	}

	/**
	 * Saves the current settings properties to an ini file.
	 * @throws IOException
	 */
	public void save() throws IOException {
		Properties p = new Properties();
		p.put(createSpectrogram.x, String.valueOf(createSpectrogram.y));
		p.put(windowSize.x, String.valueOf(windowSize.y));
		p.put(visualisationFactor.x, String.valueOf(visualisationFactor.y));
		p.put(showVisualisation.x, String.valueOf(showVisualisation.y));
		p.put(spectrogramContrast.x, String.valueOf(spectrogramContrast.y));
		p.put(threadCount.x, String.valueOf(threadCount.y));
		p.put(usingWindowFunction.x, String.valueOf(usingWindowFunction.y));
		p.put(conversionMethod.x, String.valueOf(conversionMethod.y));
		
		Date date = new Date();
		p.store(new FileOutputStream(SETTINGS_FILE_NAME), "Created at: " + date.toString());
	}

	/**
	 * Loads the .ini file if it is found and returns the
	 * {@link SettingData} with the set property values.
	 * If the .ini was not found the values will be the default.
	 * All missing properties in the ini will be set to default.
	 * 
	 * @return The {@link SettingData} with all found parameters set.
	 * @throws IOException If the ini has invalid parameters. 
	 */
	public static SettingData load() throws IOException {
		SettingData data = new SettingData();
		Properties p = new Properties();
		// Check if the ini file exists.
		// If not return the default data
		File f = new File(SETTINGS_FILE_NAME);
		if (!f.exists() || f.isDirectory()) {
			return data;
		}
		p.load(new FileInputStream(SETTINGS_FILE_NAME));

		String wSize = p.getProperty(WINDOW_SIZE_PROPERTY_NAME);
		if (wSize != null) {
			try {
				int size = Integer.parseInt(wSize);
				data.setWindowSize(size);
			} catch (NumberFormatException e) {
				throw new IOException("Invalid value for property '" 
						+ WINDOW_SIZE_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String vfactor = p.getProperty(VISUALIZATION_FACTOR_PROPERTY_NAME);
		if (vfactor != null) {
			try {
				double factor = Double.parseDouble(vfactor);
				data.setVisualisationFactor(factor);
			} catch (NumberFormatException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ VISUALIZATION_FACTOR_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String spectro = p.getProperty(CREATE_SPECTRO_PROPERTY_NAME).toLowerCase();
		if (spectro != null) {
			if (spectro.equals("true")) {
				data.setCreateSpectrogram(true);
			} else if (spectro.equals("false")) {
				data.setCreateSpectrogram(false);
			} else {
				throw new IOException(
						"Invalid value for property '" 
								+ CREATE_SPECTRO_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String visual = p.getProperty(SHOW_VISUAL_PROPERTY_NAME).toLowerCase();
		if (visual != null) {
			if (visual.equals("true")) {
				data.setShowVisualisation(true);
			} else if (visual.equals("false")) {
				data.setShowVisualisation(false);
			} else {
				throw new IOException(
						"Invalid value for property '" 
								+ SHOW_VISUAL_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String sContrast = p.getProperty(SPECTRO_CONTRAST_PROPERTY_NAME);
		if (sContrast != null) {
			try {
				int contrast = Integer.parseInt(sContrast);
				data.setSpectrogramContrast(contrast);
			} catch (NumberFormatException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ SPECTRO_CONTRAST_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String threads = p.getProperty(THREAD_COUNT_PROPERTY_NAME);
		if (threads != null) {
			try {
				int threadN = Integer.parseInt(threads);
				data.setSpectrogramContrast(threadN);
			} catch (NumberFormatException e) {
				throw new IOException("Invalid value for property '" 
						+ THREAD_COUNT_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String usingWind = p.getProperty(USING_WINDOWFUNCTION_PROPERTY_NAME).toLowerCase();
		if (usingWind != null) {
			if (usingWind.equals("true")) {
				data.setUsingWindowFunction(true);
			} else if (usingWind.equals("false")) {
				data.setUsingWindowFunction(false);
			} else {
				throw new IOException(
						"Invalid value for property '" 
								+ USING_WINDOWFUNCTION_PROPERTY_NAME + "'. Could not read ini file");
			}
		}
		String convMethod = p.getProperty(CONVERSION_METHOD_PROPERTY_NAME).toUpperCase();
		if (convMethod != null) {
			try {
				FrequencyConversion fc = FrequencyConversion.valueOf(convMethod);
				data.setFrequencyConverter(fc);
			}catch(IllegalArgumentException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CONVERSION_METHOD_PROPERTY_NAME + "'. Could not read ini file");
			}	
		}
		return data;
	}

	public int getWindowSize() {
		return windowSize.y;
	}

	public void setWindowSize(int frameSize) {
		this.windowSize.y = frameSize;
	}

	public double getVisualisationFactor() {
		return visualisationFactor.y;
	}

	public void setVisualisationFactor(double visualisationFactor) {
		this.visualisationFactor.y = visualisationFactor;
	}

	public boolean createSpectrogram() {
		return createSpectrogram.y;
	}

	public void setCreateSpectrogram(boolean createSpectrogram) {
		this.createSpectrogram.y = createSpectrogram;
	}

	public boolean showVisualisation() {
		return showVisualisation.y;
	}

	public void setShowVisualisation(boolean showVisualisation) {
		this.showVisualisation.y = showVisualisation;
	}

	public int getSpectrogramContrast() {
		return spectrogramContrast.y;
	}

	public void setSpectrogramContrast(int spectrogramContrast) {
		this.spectrogramContrast.y = spectrogramContrast;
	}

	public int getThreadCount() {
		return threadCount.y;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount.y = threadCount;
	}

	public FrequencyConversion getConversionMethod() {
		return conversionMethod.y;
	}

	public void setConversionMethod(FrequencyConversion conversionMethod) {
		this.conversionMethod.y = conversionMethod;
	}

	public boolean isUsingWindowFunction() {
		return usingWindowFunction.y;
	}

	public void setUsingWindowFunction(boolean usingWindowFunction) {
		this.usingWindowFunction.y = usingWindowFunction;
	}
}
