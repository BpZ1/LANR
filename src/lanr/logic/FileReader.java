package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import io.humble.ferry.Buffer;
import io.humble.video.AudioFormat;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.Rational;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;
import lanr.logic.utils.Converter;
import lanr.logic.utils.DoubleConverter;
import lanr.model.Settings;

public class FileReader  {

	private volatile boolean interrupted = false;

	public static final String LOADING_STARTED_PROPERTY = "start";
	public static final String LOADING_ENDED_PROPERTY = "end";
	public static final String MEMORY_USAGE_PROPERTY = "memory";
	public static final String ANALYSIS_STARTED_PROPERTY = "dStart";
	public static final String ANALYSIS_ENDED_PROPERTY = "dEnd";
	public static final String ERROR_PROPERTY = "error";
	private static final int TASK_DELAY = 1000;
	private final static int BUFFER_PUFFER = 50000; // 50kb Puffer
	private static int windowSize = 1024;
	private Timer timer = new Timer();
	
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);
	/**
	 * Counter for the number of running threads
	 */
	private int processCounter = 0;
	private ReentrantLock counterLock = new ReentrantLock(true);
	private List<Future<AudioData>> runningTasks = new LinkedList<Future<AudioData>>();
	private List<Future<AudioData>> completedTasks = new LinkedList<Future<AudioData>>();
	
	/**
	 * Thread pool
	 */
	private ExecutorService executors;
	
	public FileReader(int threads, PropertyChangeListener listener) {
		this.executors = Executors.newFixedThreadPool(threads);
		this.state.addPropertyChangeListener(listener);
		//Timer to check if the analyzing tasks are finished
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						checkTasks();
						updateMemoryUsage();
					}			
				}, 1000, TASK_DELAY);
	}

	public void getFileContainer(String path) {
		incrementCounter();
		runningTasks.add(executors.submit(() -> {
			return createFileContainer(path);
		}));
	}

	public void analyze(AudioData data) {
		interrupted = false;
		Runnable algorithmRunnable = () -> {
			try {
				state.firePropertyChange(LOADING_STARTED_PROPERTY, null, null);
				analyseFile(data);
				decrementCounter();
			} catch (InterruptedException | IOException | LANRFileException | LANRException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}
		};
		incrementCounter();
		executors.execute(algorithmRunnable);
	}
	
	private void updateMemoryUsage() {
		double freeMemory = Runtime.getRuntime().freeMemory();
		double totalMemory = Runtime.getRuntime().totalMemory();
		double memoryUsage = freeMemory / totalMemory;
		state.firePropertyChange(MEMORY_USAGE_PROPERTY, null, memoryUsage);
	}
	
	private void checkTasks() {
		for(int i = 0; i < runningTasks.size(); i++) {
			Future<AudioData> task = runningTasks.get(i);
			if(task.isDone()) {
				try {
					AudioData data = task.get();
					state.firePropertyChange(LOADING_ENDED_PROPERTY, null, data);
					completedTasks.add(task);
				} catch (InterruptedException | ExecutionException e) {
					state.firePropertyChange(ERROR_PROPERTY, null, e);
				}
			}
		}
		for(Future<AudioData> task : completedTasks) {
			runningTasks.remove(task);
		}
		completedTasks.clear();
	}
	
	/**
	 * Adds a change listener that will be notified if errors occured.
	 * @param listener
	 */
	public void addChangeListener(PropertyChangeListener listener) {
		state.addPropertyChangeListener(listener);
	}

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
	private AudioData createFileContainer(String path) throws InterruptedException, IOException, LANRFileException {
		// Creating the audio object
		List<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
		AudioData data = new AudioData(path, audioChannels);

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
				for (int c = 1; c <= samples.getChannels(); c++) {
					AudioChannel channel = new AudioChannel(data, bitDepth, sampleRate, i, c, length);
					audioChannels.add(channel);
				}
			}
		}
		if (audioChannels.isEmpty()) {
			throw new LANRFileException("No audio stream could be found in " + path);
		}
		demuxer.close();
		return data;
	}

	private void analyseFile(AudioData data)
			throws InterruptedException, IOException, LANRFileException, LANRException {
		if (data == null) {
			throw new IllegalArgumentException("Audio data musn't be null");
		}

		Demuxer demuxer = Demuxer.make();
		demuxer.open(data.getPath(), null, false, true, null, null);

		Map<Integer, List<AudioChannel>> audioStreams = new HashMap<Integer, List<AudioChannel>>();

		// Sort the channels by their audio stream
		for (AudioChannel channel : data.getChannel()) {
			List<AudioChannel> stream = audioStreams.get(channel.getId());
			if (stream == null) {
				LinkedList<AudioChannel> value = new LinkedList<AudioChannel>();
				value.add(channel);
				audioStreams.put(channel.getId(), value);
			} else {
				stream.add(channel);
			}
		}
		// Read the data for all channel in a given stream
		for (Map.Entry<Integer, List<AudioChannel>> audioStream : audioStreams.entrySet()) {
			readAudioStreamData(audioStream.getKey(), audioStream.getValue(), demuxer);
		}

		data.setAnalyzed(true);
		demuxer.close();
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
	private void readAudioStreamData(int streamId, List<AudioChannel> channels, Demuxer demuxer)
			throws InterruptedException, IOException, LANRFileException, LANRException {

		final DemuxerStream stream = demuxer.getStream(streamId);
		final Decoder decoder = stream.getDecoder();

		decoder.open(null, null);
		final MediaAudio samples = MediaAudio.make(decoder.getFrameSize(), decoder.getSampleRate(),
				decoder.getChannels(), decoder.getChannelLayout(), decoder.getSampleFormat());
		
		int bytePerSample = samples.getBytesPerSample();
		int bufferSize = windowSize * bytePerSample * channels.size();
		ByteBuffer rawAudio = null;

		ByteBuffer buffer = ByteBuffer.allocate(bufferSize + BUFFER_PUFFER); // Added extra buffer to prevent overflow
		// Data that will be processed
		byte[] bufferData = new byte[bufferSize];
		int byteInBuffer = 0;
		final MediaPacket packet = MediaPacket.make();
		// List that collects the data that will be sent to the channels
		List<DoubleBuffer> channelData = new ArrayList<DoubleBuffer>();
		for (int c = 0; c < channels.size(); c++) {
			// Adds the container and notify the channel
			channelData.add(DoubleBuffer.allocate(windowSize));
			channels.get(c).analyseStart(windowSize);
		}
		DoubleConverter doubleConverter;
		try {
			doubleConverter = Converter.getConverter(samples.getFormat());
		} catch (LANRException e) {
			throw new LANRFileException(e.getMessage());
		}
		int counter = 0;
		// Read the packets
		while (demuxer.read(packet) >= 0) {
			// Check if the packet is part of the current stream
			if (packet.getStreamIndex() == streamId) {
				int offset = 0;
				int bytesRead = 0;
				do {
					if (interrupted) {
						for (AudioChannel channel : channels) {
							channel.clearAnalysisData();
						}
						return;
					}
					bytesRead += decoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						// Send the packet data to listeners
						rawAudio = null;
						rawAudio = getSamples(rawAudio, samples, channels.size());
						// Count number of bytes in buffer
						byteInBuffer += rawAudio.capacity();
						buffer.put(rawAudio);
						// If enough bytes are in the buffer send data to channel
						while (buffer.position() >= bufferSize) {
							buffer.flip();
							buffer.get(bufferData, 0, bufferSize);
							double[] convertedData = doubleConverter.convert(bufferData);
							// Sort the data for the different channel
							counter = 0;
							for (int i = 0; i < convertedData.length; i++) {
								channelData.get(counter).put(convertedData[i]);
								counter = (counter + 1) % channels.size();
							}
							// Send the data to the different channel
							for (int c = 0; c < channels.size(); c++) {
								DoubleBuffer channelDataBuffer = channelData.get(c);
								channels.get(c).analyzeData(channelDataBuffer.array());
								channelDataBuffer.clear();
							}

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
		// Check if there is still data that is < sampleSize * bytesPerSample
		if (buffer.position() != 0) {
			// The final frame has to be filled to get a power of 2.
			int currentPos = buffer.position();
			for (int i = currentPos; i < bufferSize; i++) {
				buffer.put((byte) 0);
			}
			buffer.flip();
			buffer.get(bufferData, 0, bufferSize);
			double[] convertedData = doubleConverter.convert(bufferData);	
			counter = 0;
			for (int i = 0; i < convertedData.length; i++) {
				channelData.get(counter).put(convertedData[i]);
				counter = (counter + 1) % channels.size();
			}
			// Send the data to the different channel
			for (int c = 0; c < channels.size(); c++) {
				DoubleBuffer channelDataBuffer = channelData.get(c);
				channels.get(c).analyzeData(channelDataBuffer.array());
				channelDataBuffer.clear();
			}
		}
		buffer.clear();
		for (AudioChannel channel : channels) {
			channel.analyseEnd();
		}
	}
	
	private ByteBuffer getSamples(ByteBuffer output, MediaAudio samples, int channels) {
		int size = AudioFormat.getBufferSizeNeeded(samples.getNumSamples(), channels, samples.getFormat());
		output = ByteBuffer.allocate(size);
		final Buffer buffer = samples.getData(0);
	    int bufferSize = samples.getDataPlaneSize(0);
	    byte[] bytes = output.array();
	    buffer.get(0, bytes, 0, bufferSize);
	    output.limit(size);
	    output.position(0);
	    buffer.delete();
	    return output;
	}
	
	/**
	 * @return True if there are running threads.
	 */
	public boolean isBussy() {
		if (processCounter == 0) {
			return false;
		}
		return true;
	}

	private void incrementCounter() {
		counterLock.lock();
		try {
			processCounter++;
		} finally {
			counterLock.unlock();
		}
	}

	private void decrementCounter() {
		counterLock.lock();
		try {
			processCounter--;
		} finally {
			counterLock.unlock();
		}
	}
	
	/**
	 * Ends all running threads used for analyzing data.
	 */
	public void shutdown() {
		interrupted = true;
		timer.cancel();
		executors.shutdown();
	}
	
	public static void setWindowSize(int size) {
		windowSize = size;
	}
	
	public static int getWindowSize() {
		return windowSize;
	}
}
