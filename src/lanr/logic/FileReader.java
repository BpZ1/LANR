package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import io.humble.video.AudioFormat.Type;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.Rational;
import io.humble.video.javaxsound.AudioFrame;
import io.humble.video.javaxsound.MediaAudioConverter;
import io.humble.video.javaxsound.MediaAudioConverterFactory;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;
import lanr.logic.utils.Converter;
import lanr.logic.utils.DoubleConverter;

public class FileReader {

	public static volatile boolean interrupted = false;

	public static final String LOADING_STARTED_PROPERTY = "start";
	public static final String LOADING_ENDED_PROPERTY = "end";
	public static final String DECODING_STARTED_PROPERTY = "dStart";
	public static final String DECODING_ENDED_PROPERTY = "dEnd";

	private static int sampleSize = 1024;
	private final static int BUFFER_PUFFER = 50000; //50kb Puffer

	/**
	 * Creates a {@link AudioData} object containing data about the
	 * 
	 * @param path
	 * @param listener
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 */
	public static AudioData getFileContainer(String path, PropertyChangeListener listener)
			throws InterruptedException, IOException, LANRFileException {
		// Creating the audio object
		List<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
		AudioData data = new AudioData(path, audioChannels);

		PropertyChangeSupport state = new PropertyChangeSupport(data);
		if (listener != null) {
			state.addPropertyChangeListener(listener);
		}

		state.firePropertyChange(LOADING_STARTED_PROPERTY, null, path);

		/*
		 * Start by creating a container object, in this case a demuxer since we are
		 * reading, to get audio data from.
		 */
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
				AudioChannel channel = new AudioChannel(bitDepth, sampleRate, i, length);
				audioChannels.add(channel);
			}
		}
		if (audioChannels.isEmpty()) {
			throw new LANRFileException("No audio stream could be found in " + path);
		}
		demuxer.close();
		state.firePropertyChange(LOADING_ENDED_PROPERTY, null, null);
		return data;
	}

	/**
	 * @param path
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws LANRFileException
	 */
	public static void analyseFile(AudioData data, PropertyChangeListener listener)
			throws InterruptedException, IOException, LANRFileException {
		if (data == null) {
			throw new IllegalArgumentException("Audio data musn't be null");
		}
		path2 = data.getPath();// TODO:DEBUG------------------------------------------------
		PropertyChangeSupport state = new PropertyChangeSupport(data);
		if (listener != null) {
			state.addPropertyChangeListener(listener);
		}
		state.firePropertyChange(DECODING_STARTED_PROPERTY, null, null);

		Demuxer demuxer = Demuxer.make();
		demuxer.open(data.getPath(), null, false, true, null, null);

		// Read the data for all channel
		for (AudioChannel channel : data.getAllChannel()) {
			final DemuxerStream stream = demuxer.getStream(channel.getIndex());
			final Decoder decoder = stream.getDecoder();
			readAudioChannelData(channel, demuxer, decoder);

		}
		data.setAnalyzed(true);
		demuxer.close();
		state.firePropertyChange(DECODING_ENDED_PROPERTY, null, null);
	}

	private static String path2 = null;

	private static void readAudioChannelData(AudioChannel channel, Demuxer demuxer, Decoder audioDecoder)
			throws InterruptedException, IOException, LANRFileException {

		/*
		 * Based on code from
		 * https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src
		 * /main/java/io/humble/video/demos/DecodeAndPlayAudio.java
		 * 
		 */
		audioDecoder.open(null, null);
		final MediaAudio samples = MediaAudio.make(audioDecoder.getFrameSize(), audioDecoder.getSampleRate(),
				audioDecoder.getChannels(), audioDecoder.getChannelLayout(), audioDecoder.getSampleFormat());

		MediaAudioConverter converter = null;
		try {
			converter = MediaAudioConverterFactory
					.createConverter(MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
							samples);
		} catch (IllegalArgumentException e) {
			WAV w = new WAV();
			File f = new File(path2);
			try {
				w.createWAV(channel, f);
			} catch (UnsupportedAudioFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new LANRFileException("File format not supported", e);
		}

		int bytePerSample = samples.getBytesPerSample();
		int bufferSize = sampleSize * bytePerSample;
		ByteBuffer rawAudio = null;
		
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize + BUFFER_PUFFER); // Added extra buffer to prevent overflow
		// Data that will be processed
		byte[] bufferData = new byte[bufferSize];
		int byteInBuffer = 0;
		final MediaPacket packet = MediaPacket.make();
		channel.analyseStart(sampleSize);
		DoubleConverter doubleConverter;
		try {
			doubleConverter = Converter.getConverter(samples.getFormat());
		} catch (LANRException e) {
			throw new LANRFileException(e.getMessage());
		}
		
		// Read the packets
		while (demuxer.read(packet) >= 0) {
			// Check if the packet is part of the current stream
			if (packet.getStreamIndex() == channel.getIndex()) {
				int offset = 0;
				int bytesRead = 0;
				do {
					if (interrupted) {
						channel.clearAnalysisData();
						return;
					}				

					bytesRead += audioDecoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						// Send the packet data to listeners
						rawAudio = converter.toJavaAudio(rawAudio, samples);
						// Count number of bytes in buffer
						byteInBuffer += rawAudio.capacity();
						buffer.put(rawAudio);
						// If enough bytes are in the buffer send data to channel
						while(buffer.position() >= bufferSize) {
							buffer.flip();
							buffer.get(bufferData, 0, bufferSize);
							channel.analyzeData(doubleConverter.convert(bufferData));
							buffer.position(bufferSize);
							buffer.compact();
							byteInBuffer -= bufferSize;
							buffer.position(byteInBuffer);
						}
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
		if (buffer.position() != 0) {
			// The final frame has to be filled to get a power of 2.
			int currentPos = buffer.position();
			for (int i = currentPos; i < bufferSize; i++) {
				buffer.put((byte) 0);
			}
			buffer.flip();
			buffer.get(bufferData, 0, bufferSize);
			channel.analyzeData(doubleConverter.convert(bufferData));
		}
		buffer.clear();
		channel.analyseEnd();
	}
}
