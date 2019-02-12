package lanr.logic.noise;

import java.util.Arrays;

import javolution.util.FastTable;
import lanr.logic.Utils;
import lanr.logic.model.Interval;
import lanr.logic.model.Tuple;

/**
 * Contains different types of filter.
 * 
 * @author Nicolas Bruch
 *
 */
public class Filter {

	/**
	 * Subtracts the median of the values from all values.
	 * @param values
	 * @return
	 */
	public static double[] medianFilter(double[] values) {
		double[] sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
		double middle = sorted[sorted.length / 2];
		
		for(int i = 0; i < values.length; i++) {
			values[i] -= middle;
			if(values[i] < 0){
				values[i] = 0;
			}
		}
		return values;
	}
	
	/**
	 * Subtracts the median of the values from all values.
	 * @param values
	 * @return
	 */
	public static double[] lowFrequencyMedianFilter(double[] values) {
		double[] sorted = Arrays.copyOf(values, values.length / 20);
		Arrays.sort(sorted);
		double middle = sorted[sorted.length / 2];
		
		for(int i = 0; i < values.length; i++) {
			values[i] -= middle;
			if(values[i] < 0){
				values[i] = 0;
			}
		}
		return values;
	}
	
	/**
	 * Sorts all values into 19 intervals of equal size
	 * and then subtracts the 10th interval from all values.
	 * @param values
	 * @return
	 */
	public static double[] medianIntervalFilter(double[] values) {
		FastTable<Tuple<Interval, FastTable<Double>>> intervals 
		= new FastTable<Tuple<Interval, FastTable<Double>>>();
		
		double max = Utils.getMax(values);
		double intervalSize =  max / 19;
		double currentPos = 0;
		for(int i = 0; i < 20; i++) {
			Interval interval = new Interval(currentPos, currentPos + intervalSize);
			currentPos += intervalSize;
			intervals.add(new Tuple<Interval, FastTable<Double>>(interval, new FastTable<Double>()));
		}
		
		for(int i = 0; i < values.length; i++) {
			for(Tuple<Interval,FastTable<Double>> interval : intervals) {
				if(interval.x.contains(values[i])) {
					interval.y.add(values[i]);
				}
			}
		}
		double middle = -1;
		int index = 10;
		while(middle == -1 || index == -1) {
			if(!intervals.get(index).y.isEmpty()) {
				middle = intervals.get(index).y.get(0);
			}else {
				index--;
			}
		}
		if(index == -1) return values;
		
		for(int i = 0; i < values.length; i++) {
			values[i] -= middle;
			if(values[i] < 0){
				values[i] = 0;
			}
		}
		return values;
	}
}
