package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import lanr.logic.model.AudioStream;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;
import lanr.logic.utils.Converter;
import lanr.logic.utils.DoubleConverter;

/**
 * Contains all the logic for reading audio or video files.
 * Also contains a thread pool that will be used every time an audio file
 * is being read or analyzed.
 * 
 * @author Nicolas Bruch
 *
 */
public class FileReader  {

	private volatile boolean interrupted = false;

	public static final String WORK_STARTED_PROPERTY = "start";
	public static final String WORK_ENDED_PROPERTY = "end";
	public static final String MEMORY_USAGE_PROPERTY = "memory";
	public static final String PROGRESS_PROPERTY = "progress";
	public static final String ERROR_PROPERTY = "error";
	public static final String ALL_TASKS_COMPLETE = "complete";
	private static final int TASK_DELAY = 1000;
	private static int windowSize = 1024;
	private Timer timer = new Timer();
	
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);
	/**
	 * Counter for the number of running threads
	 */
	private int processCounter = 0;
	private ReentrantLock counterLock = new ReentrantLock(true);
	
	/**
	 * Counter for the number of data that was given 
	 * to calculate to progress
	 */
	private int progressCounter = 0;
	private ReentrantLock counterLock2 = new ReentrantLock(true);
	
	
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
						updateProgress();
					}			
				}, 1000, TASK_DELAY);
	}

	/**
	 * Reads an audio file and returns its information.
	 * Registered listeners will be notified about:<br>
	 * <ul>
	 * <li>Memory usage</li>
	 * <li>Progress</li>
	 * <li>Start of workload</li>
	 * <li>End of workload</li>
	 * <li>End of all workloads</li>
	 * <li>Occurring errors</li>
	 * </ul>
	 * @param path
	 */
	public void getFileContainer(String path) {
		incrementProcessCounter();
		incrementProgressCounter();
		runningTasks.add(executors.submit(() -> {
			state.firePropertyChange(WORK_STARTED_PROPERTY, null, null);
			AudioData data = null;
			try {
				data = createFileContainer(path);
			} catch (InterruptedException | IOException | LANRFileException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}			
			decrementProcessCounter();		
			checkTasksFinised();
			return data;
		}));
	}

	/**
	 * Starts the analying process of an audio file.<br>
	 * Registered listeners will be notified about:<br>
	 * <ul>
	 * <li>Memory usage</li>
	 * <li>Progress</li>
	 * <li>Start of workload</li>
	 * <li>End of workload</li>
	 * <li>End of all workloads</li>
	 * <li>Occurring errors</li>
	 * </ul>
	 * @param data - Data to be analyzed.
	 */
	public void analyze(AudioData data) {
		interrupted = false;
		Runnable algorithmRunnable = () -> {
			try {
				state.firePropertyChange(WORK_STARTED_PROPERTY, null, null);
				analyseFile(data);
				decrementProcessCounter();
				checkTasksFinised();
			} catch (InterruptedException | IOException | LANRFileException | LANRException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}
		};
		incrementProcessCounter();
		incrementProgressCounter();
		executors.execute(algorithmRunnable);
	}
	
	/**
	 * Checks if all tasks are finished and if yes notifies
	 * the listener.
	 */
	private void checkTasksFinised() {
		if(processCounter == 0) {
			resetProgressCounter();
			state.firePropertyChange(ALL_TASKS_COMPLETE, null, null);
		}
	}
	
	private void updateMemoryUsage() {
		double freeMemory = Runtime.getRuntime().freeMemory();
		double totalMemory = Runtime.getRuntime().totalMemory();
		double memoryUsage = freeMemory / totalMemory;
		state.firePropertyChange(MEMORY_USAGE_PROPERTY, null, memoryUsage);
	}
	
	private void updateProgress() {
		double progress = (double)processCounter / (double) progressCounter;
		state.firePropertyChange(PROGRESS_PROPERTY, null, progress);
	}
	
	private void checkTasks() {
		for(int i = 0; i < runningTasks.size(); i++) {
			Future<AudioData> task = runningTasks.get(i);
			if(task.isDone()) {
				try {
					AudioData data = task.get();
					state.firePropertyChange(WORK_ENDED_PROPERTY, null, data);
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
	 * Creates a {@link AudioData} object containing data about the audio file.
	 * 
	 * @param path - Path to the file to be read.
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws LANRFileException
	 */
	private AudioData createFileContainer(String path) throws InterruptedException, IOException, LANRFileException {
		// Creating the audio object
		List<AudioStream> audioStreams = new ArrayList<AudioStream>();
		AudioData data = new AudioData(path, audioStreams);

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
	private void analyseFile(AudioData data)
			throws InterruptedException, IOException, LANRFileException, LANRException {
		if (data == null) {
			throw new IllegalArgumentException("Audio data musn't be null");
		}
		data.startAnalysis();
		Demuxer demuxer = Demuxer.make();
		demuxer.open(data.getPath(), null, false, true, null, null);

		// Sort the channels by their audio stream
		for (AudioStream audioStream : data.getStreams()) {
			readAudioStreamData(audioStream, demuxer);
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
	private void readAudioStreamData(AudioStream streamData, Demuxer demuxer)
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
					if (interrupted) {
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
	 * @param doubleConverter - Converter for convertign the sample from byte to double.
	 */
	private void getSamples(DoubleBuffer streamData, MediaAudio audio,
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
	
	/**
	 * @return True if there are running threads.
	 */
	public boolean isBussy() {
		if (processCounter == 0) {
			return false;
		}
		return true;
	}

	private void incrementProcessCounter() {
		counterLock.lock();
		try {
			processCounter++;
		} finally {
			counterLock.unlock();
		}
	}

	private void decrementProcessCounter() {
		counterLock.lock();
		try {
			processCounter--;
		} finally {
			counterLock.unlock();
		}
	}
	
	private void incrementProgressCounter() {
		counterLock2.lock();
		try {
			progressCounter++;
		} finally {
			counterLock2.unlock();
		}
	}

	private void resetProgressCounter() {
		counterLock2.lock();
		try {
			progressCounter = 0;
		} finally {
			counterLock2.unlock();
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
