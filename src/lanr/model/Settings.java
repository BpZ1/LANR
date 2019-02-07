package lanr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lanr.logic.AudioLogic;
import lanr.logic.LogWriter;
import lanr.logic.Spectrogram;
import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.WindowFunction;
import lanr.logic.model.AudioStream;
import lanr.logic.model.LANRException;
import lanr.view.AudioDataContainer;
import lanr.view.StreamVisualisation;

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
	private Map<String, Object> settingsProperties = new HashMap<String, Object>();
	/**
	 * Size of samples that are analyzed of a given signal.
	 */
	public static final String WINDOW_SIZE_PROPERTY_NAME = "windowsize";
	/**
	 * Reduction factor of the visualization of the signal.<br>
	 * 0.01 = 10% of the signals sample content will be displayed.
	 */
	public static final String VISUALIZATION_FACTOR_PROPERTY_NAME = "visualisationfactor";
	/**
	 * Indicates whether a spectrogram will be drawn while analyzing or not.
	 */
	public static final String CREATE_SPECTRO_PROPERTY_NAME = "createspectrogram";
	/**
	 * The path to the log files.
	 */
	public static final String SPECTROGRAM_PATH_PROPERTY_NAME = "spectrorgamPath";
	/**
	 * Indicates whether the vizualisation will be created when analyzing.
	 */
	public static final String SHOW_VISUAL_PROPERTY_NAME = "showvisualisation";
	/**
	 * Contrast for the spectrogram.
	 */
	public static final String SPECTRO_CONTRAST_PROPERTY_NAME = "spectrogramcontrast";
	/**
	 * Number of threads used for analyzing and reading files.
	 */
	public static final String THREAD_COUNT_PROPERTY_NAME = "threadcount";
	/**
	 * The conversion method to convert the samples into frequency domain.
	 */
	public static final String CONVERSION_METHOD_PROPERTY_NAME = "conversionmethod";
	/**
	 * The type of window function used.
	 */
	public static final String WINDOWFUNCTION_PROPERTY_NAME = "windowfunction";
	/**
	 * Indicates whether a log file will be created.
	 */
	public static final String CREATE_LOG_PROPERTY_NAME = "createLog";	
	/**
	 * The path to the log files.
	 */
	public static final String LOG_PATH_PROPERTY_NAME = "logPath";
	
	private Settings() {
		settingsProperties.put(WINDOW_SIZE_PROPERTY_NAME, 1024);
		settingsProperties.put(VISUALIZATION_FACTOR_PROPERTY_NAME, 0.01);
		settingsProperties.put(CREATE_SPECTRO_PROPERTY_NAME, false);
		settingsProperties.put(SPECTROGRAM_PATH_PROPERTY_NAME, "spectrograms/");
		settingsProperties.put(SHOW_VISUAL_PROPERTY_NAME, true);
		settingsProperties.put(SPECTRO_CONTRAST_PROPERTY_NAME, 300);
		settingsProperties.put(THREAD_COUNT_PROPERTY_NAME, 10);
		settingsProperties.put(CONVERSION_METHOD_PROPERTY_NAME, FrequencyConversion.FFT);
		settingsProperties.put(WINDOWFUNCTION_PROPERTY_NAME, WindowFunction.Hanning);
		settingsProperties.put(CREATE_LOG_PROPERTY_NAME, false);
		settingsProperties.put(LOG_PATH_PROPERTY_NAME, "logs/");
	};

	public static Settings createSettings(SettingData data) {
		if (instance == null) {
			instance = new Settings();
		}
		if (data.getCreateSpectrogram().isPresent()) {
			instance.setCreateSpectrogram(data.getCreateSpectrogram().get());
		}
		if(data.getSpectrogramPath().isPresent()) {
			instance.setSpectrogramPath(data.getSpectrogramPath().get());
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
		if (data.getFrequencyConverter().isPresent()) {
			instance.setConversionMethod(data.getFrequencyConverter().get());
		}
		if(data.getWindowFunction().isPresent()) {
			instance.setWindowFunction(data.getWindowFunction().get());
		}
		if(data.getCreateLog().isPresent()) {
			instance.setLogCreation(data.getCreateLog().get());
		}
		if(data.getLogPath().isPresent()) {
			instance.setLogPath(data.getLogPath().get());
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
		for(Map.Entry<String, Object> entry : settingsProperties.entrySet()) {
			p.put(entry.getKey(), String.valueOf(entry.getValue()));
		}		
		p.store(new FileOutputStream(SETTINGS_FILE_NAME), "LANR - Settings");
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
						+ WINDOW_SIZE_PROPERTY_NAME + "'. Could not read ini file.");
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
								+ VISUALIZATION_FACTOR_PROPERTY_NAME + "'. Could not read ini file.");
			}
		}
		String spectro = p.getProperty(CREATE_SPECTRO_PROPERTY_NAME);
		if (spectro != null) {
			try {
				data.setCreateSpectrogram(getBooleanValue(spectro));
			} catch (LANRException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CREATE_SPECTRO_PROPERTY_NAME + "'. Could not read ini file.");
			}
		}
		String spectroPath = p.getProperty(SPECTROGRAM_PATH_PROPERTY_NAME);
		if (spectroPath != null) {
			try {	
				Paths.get(spectroPath);				
				data.setSpectrogramPath(spectroPath);			
			}catch(InvalidPathException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CREATE_SPECTRO_PROPERTY_NAME + "'. Could not read ini file.");
			}		
		}
		String visual = p.getProperty(SHOW_VISUAL_PROPERTY_NAME);
		if (visual != null) {			
			try {
				data.setShowVisualisation(getBooleanValue(visual));
			} catch (LANRException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CREATE_LOG_PROPERTY_NAME + "'. Could not read ini file.");
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
								+ SPECTRO_CONTRAST_PROPERTY_NAME + "'. Could not read ini file.");
			}
		}
		String threads = p.getProperty(THREAD_COUNT_PROPERTY_NAME);
		if (threads != null) {
			try {
				int threadN = Integer.parseInt(threads);
				data.setThreadCount(threadN);
			} catch (NumberFormatException e) {
				throw new IOException("Invalid value for property '" 
						+ THREAD_COUNT_PROPERTY_NAME + "'. Could not read ini file.");
			}
		}
		String convMethod = p.getProperty(CONVERSION_METHOD_PROPERTY_NAME);
		if (convMethod != null) {
			try {
				FrequencyConversion fc = FrequencyConversion.valueOf(convMethod);
				data.setFrequencyConverter(fc);
			}catch(IllegalArgumentException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CONVERSION_METHOD_PROPERTY_NAME + "'. Could not read ini file.");
			}	
		}
		String windowFunc = p.getProperty(WINDOWFUNCTION_PROPERTY_NAME);
		if (windowFunc != null) {
			try {
				WindowFunction wf = WindowFunction.valueOf(windowFunc);
				data.setWindowFunction(wf);
			}catch(IllegalArgumentException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ WINDOWFUNCTION_PROPERTY_NAME + "'. Could not read ini file.");
			}	
		}
		String logCreation = p.getProperty(CREATE_LOG_PROPERTY_NAME);
		if (logCreation != null) {
			try {
				data.setCreateLog(getBooleanValue(logCreation));
			} catch (LANRException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ CREATE_LOG_PROPERTY_NAME + "'. Could not read ini file.");
			}
		}
		String logPath = p.getProperty(LOG_PATH_PROPERTY_NAME);
		if (logPath != null) {
			try {	
				Paths.get(logPath);				
				data.setLogPath(logPath);		
			}catch(InvalidPathException e) {
				throw new IOException(
						"Invalid value for property '" 
								+ LOG_PATH_PROPERTY_NAME + "'. Could not read ini file.");
			}		
		}
		return data;
	}
	
	private static boolean getBooleanValue(String value) throws LANRException {
		String nValue = value.toLowerCase();
		if(nValue.equals("true")) {
			return true;
		}else if(nValue.equals("false")) {
			return false;
		}else {
			throw new LANRException("Invalid boolean value.");
		}
	}

	/**
	 * @param property - Name of the property.
	 * @return Value of a given property name.
	 */
	public Object getPropertyValue(String property) {
		return settingsProperties.get(property);
	}

	public void setWindowSize(int windowSize) {
		AudioLogic.setWindowSize(windowSize);
		this.settingsProperties.put(WINDOW_SIZE_PROPERTY_NAME, windowSize);
	}

	public void setVisualisationFactor(double visualisationFactor) {
		StreamVisualisation.setVisualisationReductionFactor(visualisationFactor);
		this.settingsProperties.put(VISUALIZATION_FACTOR_PROPERTY_NAME, visualisationFactor);
	}

	public void setCreateSpectrogram(boolean createSpectrogram) {
		AudioStream.setCreateSpectrogram(createSpectrogram);
		this.settingsProperties.put(CREATE_SPECTRO_PROPERTY_NAME, createSpectrogram);
	}
	
	public void setSpectrogramPath(String path) {
		AudioStream.setSpectrogramOutputFolder(path);
		this.settingsProperties.put(SPECTROGRAM_PATH_PROPERTY_NAME, path);
	}

	public void setShowVisualisation(boolean showVisualisation) {
		AudioDataContainer.setShowVisualization(showVisualisation);
		this.settingsProperties.put(SHOW_VISUAL_PROPERTY_NAME, showVisualisation);
	}

	public void setSpectrogramContrast(int spectrogramContrast) {
		Spectrogram.setContrast(spectrogramContrast);
		this.settingsProperties.put(SPECTRO_CONTRAST_PROPERTY_NAME, spectrogramContrast);
	}

	public void setThreadCount(int threadCount) {
		this.settingsProperties.put(THREAD_COUNT_PROPERTY_NAME, threadCount);
	}

	public void setConversionMethod(FrequencyConversion conversionMethod) {
		AudioStream.setConverter(conversionMethod);
		this.settingsProperties.put(CONVERSION_METHOD_PROPERTY_NAME, conversionMethod);
	}
	
	public void setWindowFunction(WindowFunction windowFunction) {
		AudioStream.setWindowFunction(windowFunction);
		this.settingsProperties.put(WINDOWFUNCTION_PROPERTY_NAME, windowFunction);
	}
	
	public void setLogPath(String path) {
		LogWriter.setLogFolderPath(path);
		this.settingsProperties.put(LOG_PATH_PROPERTY_NAME, path);
	}
	
	public void setLogCreation(boolean value) {
		AudioStream.setCreateLogFile(value);
		this.settingsProperties.put(CREATE_LOG_PROPERTY_NAME, value);
	}
}
