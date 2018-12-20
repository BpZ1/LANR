package lanr.logic;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


import lanr.logic.model.AudioData;

public class AudioPlayer {

	private static SourceDataLine mLine;
	
	public static void playAudio(AudioData data) {
		openJavaSound(data);

	}
	
	private static void openJavaSound(AudioData data) {
		AudioFormat audioFormat = new AudioFormat(
				data.getSampleRate(), data.getBitDepth(), data.getAllChannel().size(),
				true,
				false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			mLine = (SourceDataLine) AudioSystem.getLine(info);
			mLine.open(audioFormat);
			mLine.start();
		} catch (LineUnavailableException e) {
			throw new RuntimeException("could not open audio line");
		}
	}
	

	private static void closeJavaSound() {
		if (mLine != null) {
			/*
			 * Wait for the line to finish playing
			 */
			mLine.drain();
			/*
			 * Close the line.
			 */
			mLine.close();
			mLine = null;
		}
	}
}
