package lanr.model;

import java.util.ArrayList;
import java.util.List;

import lanr.logic.AudioAnalyzer;
import lanr.logic.model.AudioData;

public class MainModel extends Model {

	private List<AudioData> audioData = new ArrayList<AudioData>();
	private AudioAnalyzer analyzer = new AudioAnalyzer();
	
	
	public void analyze() {
		analyzer.anazlyze();
	}
	
	public void addAudioData(AudioData data) {
		audioData.add(data);
	}
	
	public List<AudioData> getAudioData(){
		return this.audioData;
	}
	
	
}
