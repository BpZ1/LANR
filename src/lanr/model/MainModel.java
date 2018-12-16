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
	
	
}
