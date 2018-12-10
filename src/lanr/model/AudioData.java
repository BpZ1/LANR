package lanr.model;

import java.util.List;

public class AudioData {

	private String path;
	private List<Noise> foundNoise;
	private double severity;
	
	public AudioData(String path) {
		this.path = path;
	}
	
	private void calculateSeverity() {
		severity = 0;
		for(Noise noise : foundNoise) {
			severity += noise.getSeverity();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Noise> getFoundNoise() {
		return foundNoise;
	}

	public void setFoundNoise(List<Noise> foundNoise) {
		this.foundNoise = foundNoise;
	}
	
	public void addNoise(Noise noise) {
		this.foundNoise.add(noise);
		calculateSeverity();
	}
	
	public void removeNoise(Noise noise) {
		this.foundNoise.remove(noise);
		calculateSeverity();
	}
	
	public double getSeverity() {
		return severity;
	}
}
