package lanr.logic;

import jwave.Transform;
import jwave.transforms.wavelets.haar.Haar1;
import jwave.transforms.FastWaveletTransform;

public class AudioAnalyzer {

	public void anazlyze() {
		Transform t = new Transform(new FastWaveletTransform(new Haar1()));
		
		double[ ] arrTime = { 1., 1., 1., 1., 1., 1., 1., 1. };

		double[ ] arrFreq = t.forward( arrTime ); // 1-D DFT forward

		double[ ] arrReco = t.reverse( arrFreq ); // 1-D DFT reverse
		
		for(int i = 0; i< arrReco.length; i++) {
			System.out.println(arrReco[i]);			
		}
	}
}
