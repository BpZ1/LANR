package lanr.logic;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import io.humble.ferry.Buffer;
import io.humble.video.AudioFormat;
import io.humble.video.MediaAudio;
import lanr.logic.utils.DoubleConverter;

/**
 * Contains methods for retrieving and converting channel and sample data.
 * 
 * @author Nicolas Bruch
 *
 */
class FileReaderUtils {
	
	
	/**
	 * Calculates the Root Mean Square of the given samples.
	 * This is done by calculating the average of the squared channel
	 * samples, then calculating the average of the channel averages.
	 * And then squaring the result.
	 * @param channelSamples
	 * @return
	 */
	static double calculateRMS(double[][] channelSamples) {
		double[] means = new double[channelSamples.length];
		//Calculating the sum of the squared sample values
		for(int j = 0; j < means.length; j++) {
			for(int i = 0; i < channelSamples[j].length; i++) {
				means[j] += Math.pow(channelSamples[j][i], 2);
			}
		}
		//Calculating the average mean
		for(int i = 0; i < means.length; i++) {
			means[i] /= channelSamples[i].length;
		}
		double sum = 0.0;
		for(int i = 0; i < means.length; i++) {
			sum += means[i];
		}
		sum /= channelSamples.length;
		return Math.sqrt(sum);
	}
	
	/**
	 * Converts the samples of the different channels into doubles 
	 * in the range between -1 and 1.
	 * @param samples - Buffer in which the samples for each channel will be stored.
	 * @param audio - Audio data containing the samples.
	 * @param doubleConverter - Matching sample converter.
	 */
	static void getChannelSamples(DoubleBuffer[] samples, MediaAudio audio,
			DoubleConverter doubleConverter) {
		//Converts the data if it is in planar form
		if(audio.isPlanar()) {
			int planeCount = audio.getNumDataPlanes();
			double[][] planeSamples = new double[planeCount][];
			//Get the data from every plane
			for(int i = 0; i < planeCount; i++) {
				final Buffer buffer = audio.getData(i);		
			    int bufferSize = audio.getDataPlaneSize(i);
			    byte[] bytes = new byte[audio.getDataPlaneSize(i)];
			    buffer.get(0, bytes, 0, bufferSize);
			    buffer.delete(); 
				planeSamples[i] = doubleConverter.convert(bytes);
			}
			//Put samples in the buffer
			for(int i = 0; i < planeSamples.length; i++) {
				for(int j = 0; j < planeSamples[i].length; j++) {
					samples[i].put(planeSamples[i][j]);
				}
			}
		}else {
			int size = AudioFormat.getBufferSizeNeeded(audio.getNumSamples(), audio.getChannels(), audio.getFormat());
			ByteBuffer data = ByteBuffer.allocate(size);
			final Buffer buffer = audio.getData(0);		
		    int bufferSize = audio.getDataPlaneSize(0);
		    byte[] bytes = data.array();
		    buffer.get(0, bytes, 0, bufferSize);
		    buffer.delete();
		    double[] allSamples = doubleConverter.convert(bytes);		    
	    	for (int i = 0; i < allSamples.length; i += audio.getChannels()) {
	    		int channelCounter = 0;
	    		for(int s = i; s < i + audio.getChannels(); s++) {
	    			samples[channelCounter].put(allSamples[s]);
	    			channelCounter++;
	    		}
			}
		}
	}
	
	static double[] toMono(double[][] channels) {
		double[] monoSignal = new double[channels[0].length];
		for(int i = 0; i < channels[0].length; i++) {
			double sample = 0;
			for(int j = 0; j < channels.length; j++) {
				sample += channels[j][i];
			}
			monoSignal[i] = sample / channels.length;
		}
		return monoSignal;
	}

	/**
	 * Converts the raw audio data from the {@link MediaAudio} object to
	 * a mono signal and into samples of type double and stores them in the given buffer.
	 * @param streamData - Buffer in which the samples will be stored.
	 * @param audio - Audio data containing the raw data.
	 * @param doubleConverter - Converter for converting the sample from byte to double.
	 */
	static void getSamples(DoubleBuffer streamData, MediaAudio audio,
			DoubleConverter doubleConverter) {
		
		double multiplicationFactor = 1.0 / audio.getChannels();
		//Converts the data if it is in planar form
		if(audio.isPlanar()) {
			int planeCount = audio.getNumDataPlanes();
			double[][] planeSamples = new double[planeCount][];
			//Get the data from every plane
			for(int i = 0; i < planeCount; i++) {
				final Buffer buffer = audio.getData(i);		
			    int bufferSize = audio.getDataPlaneSize(i);
			    byte[] bytes = new byte[audio.getDataPlaneSize(i)];
			    buffer.get(0, bytes, 0, bufferSize);
			    buffer.delete(); 
				planeSamples[i] = doubleConverter.convert(bytes);
			}	
			//Combine the samples from the planes into a single mono sample
			streamData.put(toMono(planeSamples));
		}else {
			//Non planar audio means the samples alternate in channel
			int size = AudioFormat.getBufferSizeNeeded(audio.getNumSamples(), audio.getChannels(), audio.getFormat());
			ByteBuffer data = ByteBuffer.allocate(size);
			final Buffer buffer = audio.getData(0);		
		    int bufferSize = audio.getDataPlaneSize(0);
		    byte[] bytes = data.array();
		    buffer.get(0, bytes, 0, bufferSize);
		    buffer.delete();
		    double[] samples = doubleConverter.convert(bytes);		    
	    	for (int i = 0; i < samples.length; i += audio.getChannels()) {
	    		double sample = 0.0;
	    		for(int s = i; s < i + audio.getChannels(); s++) {
	    			sample += samples[s] * multiplicationFactor;
	    		}
	    		streamData.put(sample);
			}
		}
	}
}
