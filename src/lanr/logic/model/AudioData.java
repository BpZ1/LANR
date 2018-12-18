package lanr.logic.model;

import java.util.List;

public class AudioData {

	private final String path;
	private List<Noise> foundNoise;
	private double severity;
	private final List<AudioChannel> audioChannels;
		
	public AudioData(String path, List<AudioChannel> audioChannels) {
		this.audioChannels = audioChannels;
		this.path = path;
		System.out.println("Created data");
		System.out.println("Channel: " + audioChannels.size());
		for(AudioChannel c : audioChannels) {
			System.out.println("Samples: " + c.getSamples().length);
			System.out.println("SampleRate: " + c.getSampleRate());
			System.out.println("BitRate: " + c.getBitDepth());
		}
	}
	
	private void calculateSeverity() {
		severity = 0;
		for(Noise noise : foundNoise) {
			severity += noise.getSeverity();
		}
	}
	
	public int getSampleRate() {
		if(audioChannels.size() > 0) {
			return audioChannels.get(0).getSampleRate();
		}else {
			return 0;
		}
	}
	
	public int getBitDepth() {
		if(audioChannels.size() > 0) {
			return audioChannels.get(0).getBitDepth();
		}else {
			return 0;
		}
	}
	
	public List<AudioChannel> getAllChannel(){
		return audioChannels;
	}

	public AudioChannel getAudioChannel(int index) {
		return audioChannels.get(index);
	}
	
	public String getPath() {
		return path;
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
