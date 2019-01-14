package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;
import lanr.logic.model.Tuple;
import lanr.model.Interval;

/**
 * 
 * Implementation based on the algorithm proposed by
 * Fanhu Bie, Dong Wang, Jun Wang, Thomas Fang Zheng.
 * in their paper
 * Detection and reconstruction of clipped speech for speaker recognition
 * (see https://www.sciencedirect.com/science/article/abs/pii/S0167639315000722).
 * 
 */
public class ClippingSearch extends NoiseSearch {	

	public ClippingSearch(int sampleRate, int windowSize, double replayGain) {
		super(sampleRate, windowSize, replayGain);
	}

	private long locationCounter = 0;
	
	/**
	 * Samples under this value will not be taken into account.
	 */
	private static final double THRESHOLD = 0.02;
	/**
	 * Number of sub intervals in which the samples are divided.
	 */
	private static final int INTERVALS = 20;
	/**
	 * If this percentage of samples is found in one interval
	 * the samples are classified as clipped.
	 */
	private static double maxProportion = 0.40;
	
	private List<Noise> foundNoise = new LinkedList<Noise>();
	
	/**
	 * List of intervals.
	 */
	private List<Tuple<Interval, Integer>> subIntervals;	
	
	@Override
	public void search(double[] samples) {
		double maximum = getMaximumAmp(samples);
		subIntervals = createSubIntervals(maximum);
		//Sort all samples in their intervals
		int samplesOverThreshold = 0;
		for(double d : samples) {
			for(Tuple<Interval, Integer> subInterval : subIntervals) {
				double value = Math.abs(d);
				if(subInterval.x.contains(value)) {
					subInterval.y = subInterval.y + 1;
					samplesOverThreshold++;
				}
			}
		}
		double intervalValue = (double) subIntervals.get(INTERVALS - 1).y + (double) subIntervals.get(INTERVALS - 2).y;
		double proportion = intervalValue / (double) samplesOverThreshold;
		if(proportion > maxProportion) {
			/*
			 * Add noise if the current window has more than the above defined percentage of its
			 * samples in the last interval.
			 */
			Noise noise = new Noise(NoiseType.Clipping, locationCounter, windowSize, 0);
			foundNoise.add(noise);
		}	
		locationCounter += windowSize;
	}
	
	/**
	 * Returns the maximum absolute value of the given samples.
	 * @param samples - Samples for which the maximum is to be returned.
	 * @return Maximum of the given samples.
	 */
	private double getMaximumAmp(double[] samples) {
		double max = Double.NEGATIVE_INFINITY;
		for(double d : samples) {
			max = Math.max(Math.abs(d), max);		
		}
		return max;
	}
	
	/**
	 * Creates a list with the above defined number of intervals and an integer as counter.
	 * The size of each interval is defined by the maximum amplitude given.
	 * The first interval is smaller by the above defined threshold to filter out
	 * white noise.
	 * @param maximum - Maximum amplitude in the current window.
	 * @return List of all intervals and their counter.
	 */
	private List<Tuple<Interval, Integer>> createSubIntervals(double maximum){
		List<Tuple<Interval, Integer>> subIntervals = new LinkedList<Tuple<Interval, Integer>>();
		double intervalSize = maximum / INTERVALS;	
		double currentInterval = THRESHOLD;
		for(int i = 1; i < INTERVALS; i++) {
			double updatedInterval = i * intervalSize;
			Interval interval = new Interval(currentInterval, updatedInterval);
			currentInterval = updatedInterval;
			subIntervals.add(new Tuple<Interval, Integer>(interval, 0));
		}
		//Adding last interval extra to avoid rounding errors
		Interval interval = new Interval(currentInterval, maximum);
		subIntervals.add(new Tuple<Interval, Integer>(interval, 0));
		
		return subIntervals;
	}
	
	@Override
	public List<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		this.foundNoise = combineNoises(foundNoise, 1, sampleRate * 3);
	}
	
	
}
