package lanr.model;

import java.util.Optional;

public class SettingData {

	private Optional<Integer> frameSize = Optional.empty();
	private Optional<Double> visualisationFactor = Optional.empty();
	private Optional<Boolean> createSpectrogram = Optional.empty();
	private Optional<Boolean> showVisualisation = Optional.empty();
	private Optional<Integer> spectrogramContrast = Optional.empty();
	
	public Optional<Integer> getFrameSize() {
		return frameSize;
	}
	public void setFrameSize(int frameSize) {
		this.frameSize = Optional.of(frameSize);
	}
	public Optional<Double> getVisualisationFactor() {
		return visualisationFactor;
	}
	public void setVisualisationFactor(double visualisationFactor) {
		this.visualisationFactor = Optional.of(visualisationFactor);
	}
	public Optional<Boolean> getCreateSpectrogram() {
		return createSpectrogram;
	}
	public void setCreateSpectrogram(boolean createSpectrogram) {
		this.createSpectrogram = Optional.of(createSpectrogram);
	}
	public Optional<Boolean> getShowVisualisation() {
		return showVisualisation;
	}
	public void setShowVisualisation(boolean showVisualisation) {
		this.showVisualisation = Optional.of(showVisualisation);
	}
	public Optional<Integer> getSpectrogramContrast() {
		return spectrogramContrast;
	}
	public void setSpectrogramContrast(int spectrogramContrast) {
		this.spectrogramContrast = Optional.of(spectrogramContrast);
	}
	
}
