package lanr.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;

public class AnalysisBenchmark {

	private static AudioLogic logic;
	private static long[] times;
	private static AudioData audioData;
	private static boolean dataLoaded = false;
	private static boolean errorOccured = false;
	private static AtomicBoolean taskDone = new AtomicBoolean(false);
	/**
	 * 
	 * @param args
	 * <ul>
	 * <li>File path</li>
	 * <li>Number of tests</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if(args.length < 2) {
			throw new IllegalArgumentException("At least two arguments (file path and number of tests).");
		}
		//Get the first parameter
		String[] path = new String[] {
				args[0]
		};
		//Get the second parameter
		int testCount;
		try {
			testCount = Integer.parseInt(args[1]);
		}catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value for number of tests.");
		}
		times = new long[testCount];
		logic = new AudioLogic(1, getAnalysisEventHandler());
		//Load the file
		logic.getFileContainer(path);
		//Start the actual test
		startTests(testCount);
	}
	
	private static void startTests(int numberOfIterations) {
		while(!dataLoaded && !errorOccured) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//Ignore
			}
		}
		if(errorOccured) {
			System.out.println("Could not execute tests because an error occured while"
					+ " reading the file.");
			return;
		}
		System.out.println("--Starting benchmark--");
		System.out.println("TEST NR" + "\t\t" + "TIME IN MS");
		for(int i = 0; i < numberOfIterations; i++) {
			System.out.print((i + 1) + "\t\t");
			times[i] = System.currentTimeMillis();
			taskDone.set(false);
			logic.analyze(audioData);
			while(!taskDone.get()) {
				try {
					if(errorOccured) {
						return;
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
			times[i] = System.currentTimeMillis() - times[i];
			System.out.print(times[i] + System.lineSeparator());
		}
		logic.shutdown();
	}
	
	private static PropertyChangeListener getAnalysisEventHandler() {
		PropertyChangeListener eventHandler = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch(evt.getPropertyName()) {
					case AudioLogic.WORK_ENDED_PROPERTY:
						if(evt.getNewValue() != null) {
							AudioData[] data = (AudioData[]) evt.getNewValue();
							for(AudioData audio : data) {
								audioData = audio;	
								dataLoaded = true;
							}
						}					
						break;
					case AudioLogic.ERROR_PROPERTY:
						if(evt.getNewValue() instanceof LANRException) {
							LANRException e = (LANRException) evt.getNewValue();
							errorOccured = true;
							throw new RuntimeException(e);
						}else {
							Exception e = (Exception) evt.getNewValue();
							errorOccured = true;
							throw new RuntimeException(e);
						}			
					case AudioLogic.ALL_TASKS_COMPLETE:
						taskDone.set(true);
						break;
				}			
			}
		};
		return eventHandler;
	}
}
