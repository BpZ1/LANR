package lanr.model;

import java.util.Optional;

import lanr.logic.noise.FrequencyConversion;

/**
 * @author Nicolas Bruch
 * 	
 * Contains the loaded settings data.
 * For more information about the different parameter take a look at {@link Settings}
 *
 */
public class SettingData {

	private Optional<Integer> frameSize = Optional.empty();
	private Optional<Double> visualisationFactor = Optional.empty();
	private Optional<Boolean> createSpectrogram = Optional.empty();
	private Optional<Boolean> showVisualisation = Optional.empty();
	private Optional<Integer> spectrogramContrast = Optional.empty();
	private Optional<Double> spectrogramScale = Optional.empty();
	private Optional<Integer> threadCount = Optional.empty();
	private Optional<Boolean> useWindowFunction = Optional.empty();
	private Optional<FrequencyConversion> frequencyConverter = Optional.empty();
	
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
	public Optional<Double> getSpectrogramScale() {
		return spectrogramScale;
	}
	public void setSpectrogramScale(double spectrogramScale) {
		this.spectrogramScale = Optional.of(spectrogramScale);
	}
	public Optional<Integer> getThreadCount(){
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = Optional.of(threadCount);
	}
	public void setUseWindowFunction(boolean useWindowFunction) {
		this.useWindowFunction = Optional.of(useWindowFunction);
	}
	public Optional<Boolean> getUseWindowFunction(){
		return useWindowFunction;
	}
	public Optional<FrequencyConversion> getFrequencyConverter() {
		return frequencyConverter;
	}
	public void setFrequencyConverter(FrequencyConversion frequencyConverter) {
		this.frequencyConverter = Optional.of(frequencyConverter);
	}
}
