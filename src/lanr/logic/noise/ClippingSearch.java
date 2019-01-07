package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

public class ClippingSearch extends NoiseSearch {	

	public ClippingSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
	}

	/**
	 * Number of samples that have at the top of a square wave
	 */
	private static final int THRESHOLD = 10;
	private static final double SEVERITY = 0.2;
	/**
	 * Percentage of the amplitude that constitutes as clipping.
	 */
	private static final double BORDER_SIZE = 0.9;
	
	private List<Noise> foundNoise = new LinkedList<Noise>();
	private List<PotentialClipping> potentialNoise = new LinkedList<PotentialClipping>(); 
	
	private PotentialClipping currentNoise = null;
	private long counter = 0;
	
	private double previousXCoordinate = 0.0;
	private double previousYCoordinate = 0.0;
	
	private double maximum = Double.NEGATIVE_INFINITY;
	private double minimum = Double.POSITIVE_INFINITY;
	
	@Override
	public void search(double[] samples) {
		
		for(double d : samples) {
			this.maximum = Math.max(maximum, d);
			this.minimum = Math.min(minimum, d);
			
			double slope = Math.abs(calculateSlope(previousYCoordinate, d));
			
			if(slope < 0.02 && slope > -0.02) {
				counter++;
				
				if(counter >= THRESHOLD) {
					if(currentNoise != null) {
						currentNoise.addSample(d);
					}else {
						currentNoise = new PotentialClipping(new Noise(NoiseType.Clipping,
								(int) previousXCoordinate + 1, THRESHOLD, 0));
						
						currentNoise.setMinAmp(d);
						currentNoise.setMaxAmp(d);
					}
				}
				//Set the previous point
				previousYCoordinate = d;
				previousXCoordinate++;
			}else {
				counter = 0;
				if(currentNoise != null) {
					this.potentialNoise.add(currentNoise);
					currentNoise = null;
				}
			}
			
			
		}
		//if distance is smaller than half of the average? or average count it
		
		//check the global maximum and minimum at the end and see which potential
		//noises are within a certain range
	}
	
	private double calculateSlope(double y1, double y2) {
		return y2 - y1; 
	}
	
	@Override
	public List<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		if(currentNoise != null) {
			this.potentialNoise.add(currentNoise);
			currentNoise = null;
		}
		float topClippingBorder = (float) (BORDER_SIZE * maximum);
		float bottomClippingBorder = (float) (BORDER_SIZE * minimum);
		List<Noise> actualClipping = new LinkedList<Noise>();
		for(PotentialClipping noise : potentialNoise) {
			if(noise.getMaxAmp() >= topClippingBorder ||
					noise.getMinAmp() <= bottomClippingBorder) {
				System.out.println("Borders: " + bottomClippingBorder + " -> " + topClippingBorder);
				System.out.println(noise.getMinAmp() + " -> " + noise.getMaxAmp());
				actualClipping.add(noise.getNoise());
			}
		}
		this.foundNoise = combineNoises(actualClipping, SEVERITY, sampleRate);
	}
	
	
}
