package lanr.logic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import lanr.logic.model.AudioData;
import lanr.logic.model.AudioStream;
import lanr.logic.model.LANRException;
import lanr.logic.model.Noise;

/**
 * Writer used to create the log files for the analysis data.
 * 
 * @author Nicolas Bruch
 *
 */
public class LogWriter {

	private static String logFolderPath = "logs/";

	/**
	 * Creates a log file containing all found noise and metadata of the given file.
	 * The log file will only be created if logFolderPath was set to a viable path.
	 * 
	 * @param data - File for which the log is to be created.
	 * @throws LANRException If the file could not be created.
	 */
	public static void writeLogFile(AudioData data) throws LANRException {
		String filename = getFileNameWithoutExtension(data.getName());
		Path path = Paths.get(logFolderPath + "/" + filename + ".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			String space = "\t\t";
			LocalDateTime now = LocalDateTime.now();
			writer.write("CREATED AT: " + now.toString() + System.lineSeparator());
			writer.write("FILE PATH: " + data.getPath() + System.lineSeparator());
			writer.write("META-DATA:" + System.lineSeparator());
			writer.write("PENALTY VALUE:" + space + data.getSeverity());
			for (AudioStream stream : data.getStreams()) {
				writer.newLine();
				writer.write("AUDIO_STREAM" + stream.getId() + ":");
				writer.write(
						"DURATION:" + space + Utils.getDurationString(stream.getLength()) + System.lineSeparator());
				writer.write("SAMPLE RATE:" + space + stream.getSampleRate() + System.lineSeparator());
				writer.write("BIT RATE:" + stream.getBitDepth() + space + System.lineSeparator());
				writer.write("VOLUME NORMALISATION:" + stream.getReplayGain() + space + System.lineSeparator());
				writer.newLine();
				writer.write("FOUND NOISE:" + System.lineSeparator());
				writer.newLine();
				for (Noise noise : stream.getFoundNoise()) {
					writer.write("Type=" + noise.getType() + "; Location="
							+ Utils.getDurationString(noise.getLocation()) + "; Duration="
							+ Utils.getDurationString(noise.getLength()) + "; Severity=" + noise.getSeverity() + ";");
					writer.newLine();
				}
			}
		} catch (IOException e) {
			throw new LANRException("Could not create log file.", e);
		}
	}

	private static String getFileNameWithoutExtension(String name) {
		String[] substrings = name.split("\\.");
		int extensionLength = substrings[substrings.length - 1].length();
		return name.substring(0, (name.length() - 1) - extensionLength);
	}
	
	public static String getLogFolderPath() {
		return logFolderPath;
	}

	public static void setLogFolderPath(String logFolderPath) {
		LogWriter.logFolderPath = logFolderPath;
	}
}
