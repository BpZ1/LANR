package lanr.logic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.humble.ferry.Buffer;
import io.humble.video.AudioFormat;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.Rational;
import lanr.logic.model.AudioData;
import lanr.logic.model.AudioStream;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;
import lanr.logic.utils.Converter;
import lanr.logic.utils.DoubleConverter;

/**
 * This class contains methods for reading the audio of 
 * media files.
 * 
 * @author Nicolas Bruch
 *
 */
class FileReader {

	/**
	 * Reference Replay gain value calculated from a 83dB pink noise signal.
	 * This value is just used to normalize the volume of the audio.
	 * The size of this value does not matter as long as 
	 * the replay gain value is not saved in the file, or the file
	 * is not played with volume adjusted according to this value.
	 */
	private static final double refDBValue = -21.796831800472418;
	/**
	 * Creates a {@link AudioData} object containing data about the audio file.
	 * 
	 * @param path - Path to the file to be read.
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 */
	public static AudioData createFileContainer(String path) throws InterruptedException, IOException, LANRFileException {
		// Creating the audio object
		List<AudioStream> audioStreams = new ArrayList<AudioStream>();
		AudioData data = new AudioData(path, audioStreams);

		Demuxer demuxer = Demuxer.make();
		demuxer.open(path, null, false, true, null, null);

		// Iterate through all audio streams
		int numStreams = demuxer.getNumStreams();
		for (int i = 0; i < numStreams; i++) {
			final DemuxerStream stream = demuxer.getStream(i);
			final Decoder decoder = stream.getDecoder();
			// Check if the found stream is an audio stream
			if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_AUDIO) {
				decoder.open(null, null);
				final MediaAudio samples = MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(),
						decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat());
				
				int bitDepth = samples.getBytesPerSample() * 8;
				int sampleRate = samples.getSampleRate();
				// Calculate the time of the stream in seconds
				Rational r = stream.getTimeBase();
				long length = stream.getDuration() / r.getDenominator();
				AudioStream audioStream = new AudioStream(data, bitDepth, sampleRate, i, length);
				audioStreams.add(audioStream);
			}
		}
		if (audioStreams.isEmpty()) {
			throw new LANRFileException("No audio stream could be found in " + path);
		}
		demuxer.close();
		return data;
	}

	/**
	 * Starts the analyzing process for a given audio file.
	 * @param data - File to be analyzed.
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 * @throws LANRException
	 */
	public static void analyseFile(AudioData data, int windowSize, AtomicBoolean interrupted)
			throws InterruptedException, IOException, LANRFileException, LANRException {
		if (data == null) {
			throw new IllegalArgumentException("Audio data musn't be null");
		}
		data.startAnalysis();
		Demuxer demuxer;
		for (AudioStream audioStream : data.getStreams()) {
			//Scan file to calculate the ReplayGain
			demuxer = Demuxer.make();			
			demuxer.open(data.getPath(), null, false, true, null, null);
			calculateReplayGain(audioStream, demuxer, windowSize, interrupted);
			demuxer.close();
			//Scan file for analysis
			demuxer = Demuxer.make();			
			demuxer.open(data.getPath(), null, false, true, null, null);
			analyzeAudioStreamData(audioStream, demuxer, windowSize, interrupted);
			demuxer.close();
		}
		data.setAnalyzed(true);
	}
	
	/**
	 * Calculates the Replay Gain value of a given audio stream.
	 * @param streamData
	 * @param demuxer
	 * @param windowSize
	 * @param interrupted
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 * @throws LANRException
	 */
	private static void calculateReplayGain(AudioStream streamData, Demuxer demuxer, int windowSize, AtomicBoolean interrupted) 
			throws InterruptedException, IOException, LANRFileException, LANRException {
		final DemuxerStream stream = demuxer.getStream(streamData.getId());
		final Decoder decoder = stream.getDecoder();
		
		decoder.open(null, null);
	
		final MediaAudio samples = MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(),
				decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat());
				
		final MediaPacket packet = MediaPacket.make();

		//Number of samples in 50 ms of the signal
		final int sampleCount50Ms = (int) (0.05 * samples.getSampleRate());
		// List that collects the data that will be sent to the channels
		DoubleBuffer[] channelData = new DoubleBuffer[samples.getChannels()];
		for(int i = 0; i  < channelData.length; i++) {
			channelData[i] = DoubleBuffer.allocate((2048 + sampleCount50Ms));			
		}

		//RMS (Root Mean Square) values
		List<Double> rmsValues = new LinkedList<Double>();
		DoubleConverter doubleConverter;
		try {
			doubleConverter = Converter.getConverter(samples.getFormat());
		} catch (LANRException e) {
			throw new LANRFileException(e.getMessage());
		}
		// Read the packets
		while (demuxer.read(packet) >= 0) {
			// Check if the packet is part of the current stream
			if (packet.getStreamIndex() == streamData.getId()) {
				int offset = 0;
				int bytesRead = 0;
				do {
					if (interrupted.get()) {
						return;
					}
					bytesRead += decoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						// Send the packet data to listeners
						getChannelSamples(channelData, samples, doubleConverter);
						
						// If enough bytes are in the buffer send data to channel
						while (channelData[0].position() >= sampleCount50Ms) {
							double[][] data = new double[channelData.length][];
							for(int i = 0; i < channelData.length; i++) {
								int pos = channelData[i].position();
								channelData[i].position(0);
								data[i] = new double[sampleCount50Ms];
								channelData[i].get(data[i], 0, sampleCount50Ms);
								channelData[i].compact();
								channelData[i].position(pos - sampleCount50Ms);
							}	
							rmsValues.add(Utils.sampleToDBFS(calculateRMS(data)));
						}												
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
		// Check if there is still data that is < sampleSize * bytesPerSample
		if (channelData[0].position() != 0) {
			double[][] data = new double[channelData.length][];
			for(int i = 0; i < channelData.length; i++) {
				int position = channelData[i].position();
				channelData[i].position(0);
				data[i] = new double[position];
				channelData[i].get(data[i], 0, position);
			}	
			rmsValues.add(Utils.sampleToDBFS(calculateRMS(data)));
		}
		Collections.sort(rmsValues);
		double replayGain = refDBValue - rmsValues.get((int) (rmsValues.size() * 0.95));
		streamData.setReplayGain(replayGain);
	}
	
	/**
	 * Calculates the Root Mean Square of the given samples.
	 * This is done by calculating the average of the squared channel
	 * samples, then calculating the average of the channel averages.
	 * And then squaring the result.
	 * @param channelSamples
	 * @return
	 */
	private static double calculateRMS(double[][] channelSamples) {
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
	
	private static void getChannelSamples(DoubleBuffer[] samples, MediaAudio audio,
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

	/**
	 * Based on code from
	 * https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src
	 * /main/java/io/humble/video/demos/DecodeAndPlayAudio.java
	 * @param streamId
	 * @param channels
	 * @param demuxer
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 * @throws LANRException
	 */
	private static void analyzeAudioStreamData(AudioStream streamData, Demuxer demuxer,
			int windowSize, AtomicBoolean interrupted)
			throws InterruptedException, IOException, LANRFileException, LANRException {

		final DemuxerStream stream = demuxer.getStream(streamData.getId());
		final Decoder decoder = stream.getDecoder();
		
		decoder.open(null, null);
	
		final MediaAudio samples = MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(),
				decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat());
				
		final MediaPacket packet = MediaPacket.make();

		// List that collects the data that will be sent to the channels
		DoubleBuffer channelData = DoubleBuffer.allocate(2048 + windowSize);
		streamData.analyseStart(windowSize);
		DoubleConverter doubleConverter;
		try {
			doubleConverter = Converter.getConverter(samples.getFormat());
		} catch (LANRException e) {
			throw new LANRFileException(e.getMessage());
		}
		// Read the packets
		while (demuxer.read(packet) >= 0) {
			// Check if the packet is part of the current stream
			if (packet.getStreamIndex() == streamData.getId()) {
				int offset = 0;
				int bytesRead = 0;
				do {
					if (interrupted.get()) {
						streamData.clearAnalysisData();
						return;
					}
					bytesRead += decoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						// Send the packet data to listeners
						getSamples(channelData, samples, doubleConverter);
						
						// If enough bytes are in the buffer send data to channel
						while (channelData.position() >= windowSize) {
							int pos = channelData.position();
							channelData.position(0);
							double[] data = new double[windowSize];
							channelData.get(data, 0, windowSize);
							streamData.analyzeData(data);
							channelData.compact();
							channelData.position(pos - windowSize);
						}												
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
		// Check if there is still data that is < sampleSize * bytesPerSample
		if (channelData.position() != 0) {
			// The final frame has to be filled to get a power of 2.
			while(channelData.position() < windowSize) {
				channelData.put(0.0);
			}
			channelData.flip();
			double[] data = new double[windowSize];
			channelData.get(data, 0, windowSize);
			streamData.analyzeData(data);
		}
		channelData.clear();
		streamData.analyseEnd();
	}
	
	/**
	 * Converts the raw audio data from the {@link MediaAudio} object to
	 * a mono signal and into samples of type double and stores them in the given buffer.
	 * @param streamData - Buffer in which the samples will be stored.
	 * @param audio - Audio data containing the raw data.
	 * @param doubleConverter - Converter for converting the sample from byte to double.
	 */
	private static void getSamples(DoubleBuffer streamData, MediaAudio audio,
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
			for(int i = 0; i < planeSamples[0].length; i++) {
				double currentSample = 0.0;				
				for(int j = 0; j < planeCount; j++) {
					currentSample += planeSamples[j][i] * multiplicationFactor; 
				}
				streamData.put(currentSample);
			}
		}else {
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
