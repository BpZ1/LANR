package lanr.logic;


import jwave.Transform;
import jwave.transforms.wavelets.coiflet.Coiflet4;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import jwave.transforms.FastWaveletTransform;

public class AudioAnalyzer {

	public void anazlyze() {
		
		
		MultimediaInfo d;
		MultimediaObject object = new MultimediaObject(null);
		
		Transform t = new Transform(new FastWaveletTransform(new Coiflet4()));
		
		double[ ] arrTime = { 1., 1., 1., 1., 1., 1., 1., 1. };

		double[ ] arrFreq = t.forward( arrTime ); // 1-D DFT forward

		for(int i = 0; i< arrFreq.length; i++) {
			System.out.println(arrFreq[i]);			
		}
		
		double[ ] arrReco = t.reverse( arrFreq ); // 1-D DFT reverse
	
		
		for(int i = 0; i< arrReco.length; i++) {
			System.out.println(arrReco[i]);			
		}
	}
}
