package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class AudioLogic  {

	private AtomicBoolean interrupted = new AtomicBoolean(false);
	/**
	 * Is fired when a new task was started.
	 */
	public static final String WORK_STARTED_PROPERTY = "start";
	/**
	 * Is fired when a task ended. The passed argument will be
	 * an array of {@link AudioData}.
	 */
	public static final String WORK_ENDED_PROPERTY = "end";
	/**
	 * Is fired every x milliseconds to give the current memory usage.
	 */
	public static final String MEMORY_USAGE_PROPERTY = "memory";
	/**
	 * Is fired every x milliseconds and reports the current progress
	 * of all submitted tasks.
	 */
	public static final String PROGRESS_PROPERTY = "progress";
	public static final String ERROR_PROPERTY = "error";
	/**
	 * Is fired when all submitted tasks are completed.
	 */
	public static final String ALL_TASKS_COMPLETE = "complete";
	/**
	 * Delay of the timer.
	 */
	private static final int TIMER_DELAY = 1000;
	/**
	 * Size of the windows that will be analyzed in samples.
	 */
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
	
	
	private List<Future<AudioData[]>> runningTasks = new LinkedList<Future<AudioData[]>>();
	private List<Future<AudioData[]>> completedTasks = new LinkedList<Future<AudioData[]>>();
	
	/**
	 * Thread pool
	 */
	private ExecutorService executors;
	
	public AudioLogic(int threads, PropertyChangeListener listener) {
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
				}, 1000, TIMER_DELAY);
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
	public void getFileContainer(String[] paths) {
		incrementProcessCounter();
		incrementProgressCounter();
		runningTasks.add(executors.submit(() -> {
			state.firePropertyChange(WORK_STARTED_PROPERTY, null, null);
			AudioData[] data = new AudioData[paths.length];
			try {
				for(int i = 0; i < paths.length; i++) {
					data[i] = FileReader.createFileContainer(paths[i]);
				}
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
		interrupted.set(false);
		Runnable algorithmRunnable = () -> {
			try {
				state.firePropertyChange(WORK_STARTED_PROPERTY, null, null);
				FileReader.analyseFile(data, windowSize, interrupted);
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
			Future<AudioData[]> task = runningTasks.get(i);
			if(task.isDone()) {
				try {
					AudioData[] data = task.get();
					state.firePropertyChange(WORK_ENDED_PROPERTY, null, data);
					completedTasks.add(task);
				} catch (InterruptedException | ExecutionException e) {
					state.firePropertyChange(ERROR_PROPERTY, null, e);
				}
			}
		}
		for(Future<AudioData[]> task : completedTasks) {
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
	
	public void interrupt() {
		interrupted.set(true);
	}
	
	/**
	 * Ends all running threads used for analyzing data.
	 */
	public void shutdown() {
		interrupted.set(true);
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
