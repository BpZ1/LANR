package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.Rational;
import io.humble.video.javaxsound.MediaAudioConverter;
import io.humble.video.javaxsound.MediaAudioConverterFactory;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRFileException;

public class FileReader {

	public static volatile boolean interrupted = false;
	
	public static final String LOADING_STARTED_PROPERTY = "start";
	public static final String LOADING_ENDED_PROPERTY = "end";
	public static final String DECODING_STARTED_PROPERTY = "dStart";
	public static final String DECODING_ENDED_PROPERTY = "dEnd";
	
	/**
	 * Creates a {@link AudioData} object containing data about the 
	 * @param path
	 * @param listener
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 */
	public static AudioData getFile(String path, PropertyChangeListener listener) throws InterruptedException, IOException, LANRFileException {
		//Creating the audio object
		List<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
		AudioData data = new AudioData(path, audioChannels);
		
		PropertyChangeSupport state = new PropertyChangeSupport(data);
		if(listener != null) {
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
				final MediaAudio samples = MediaAudio.make(
						decoder.getFrameSize(),
						decoder.getSampleRate(),
						decoder.getChannels(),
						decoder.getChannelLayout(),
						decoder.getSampleFormat());

				//Returns not the correct bit value, therefore *4 instead of 8
				int bitDepth = samples.getBytesPerSample() * 4; 
				int sampleRate = samples.getSampleRate();
				//Calculate the time of the stream in seconds
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
	public static void readFile(AudioData data, PropertyChangeListener listener)
			throws InterruptedException, IOException {
		if(data == null) {
			throw new IllegalArgumentException("Audio data musn't be null");
		}
		PropertyChangeSupport state = new PropertyChangeSupport(data);
		if(listener != null) {
			state.addPropertyChangeListener(listener);				
		}
		state.firePropertyChange(DECODING_STARTED_PROPERTY, null, null);

		Demuxer demuxer = Demuxer.make();
		demuxer.open(data.getPath(), null, false, true, null, null);

		//Read the data for all channel
		for(AudioChannel channel : data.getAllChannel()) {
			final DemuxerStream stream = demuxer.getStream(channel.getIndex());
			final Decoder decoder = stream.getDecoder();
			readAudioChannelData(channel, demuxer, decoder);
			
		}
		data.setAnalyzed(true);
		demuxer.close();
		state.firePropertyChange(DECODING_ENDED_PROPERTY, null, null);
	}

	private static void readAudioChannelData(AudioChannel channel, Demuxer demuxer, Decoder audioDecoder)
			throws InterruptedException, IOException {
		
		/*
		 * Based on code from
		 * https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src
		 * /main/java/io/humble/video/demos/DecodeAndPlayAudio.java
		 * 
		 */
		audioDecoder.open(null, null);
		final MediaAudio samples = MediaAudio.make(
				audioDecoder.getFrameSize(),
				audioDecoder.getSampleRate(),
				audioDecoder.getChannels(),
				audioDecoder.getChannelLayout(),
				audioDecoder.getSampleFormat());
		
		final MediaAudioConverter converter = MediaAudioConverterFactory
				.createConverter(MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO, samples);
		
		ByteBuffer rawAudio = null;

		final MediaPacket packet = MediaPacket.make();

		// Read the packets
		while (demuxer.read(packet) >= 0) {
			//Check if the packet is part of the current stream
			if (packet.getStreamIndex() == channel.getIndex()) {
				int offset = 0;
				int bytesRead = 0;
				do {
					if(interrupted) {
						return;
					}
					bytesRead += audioDecoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						//Send the packet data to listeners					
						rawAudio = converter.toJavaAudio(rawAudio, samples);
						channel.addRawData(rawAudio);						
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
	}
}
