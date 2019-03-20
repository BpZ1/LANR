/*
 * LANR (Lecture Audio Noise Recognition) is a software that strives to automate
 * the reviewing process of lecture recordings at the WIAI faculty of the University of Bamberg.
 *
 * Copyright (C) 2019 Nicolas Bruch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package lanr.model;

import java.util.Optional;

import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.WindowFunction;

/**
 * Contains the loaded settings data.
 * For more information about the different parameter take a look at {@link Settings}.
 *
 * @author Nicolas Bruch
 */
public class SettingData {

	private Optional<Integer> windowSize = Optional.empty();
	private Optional<Double> visualisationFactor = Optional.empty();
	private Optional<Boolean> createSpectrogram = Optional.empty();
	private Optional<String> spectrogramPath = Optional.empty();
	private Optional<Boolean> showVisualisation = Optional.empty();
	private Optional<Integer> spectrogramContrast = Optional.empty();
	private Optional<Double> spectrogramScale = Optional.empty();
	private Optional<Integer> threadCount = Optional.empty();
	private Optional<FrequencyConversion> frequencyConverter = Optional.empty();
	private Optional<WindowFunction> windowFunction = Optional.empty();
	private Optional<Boolean> createLog = Optional.empty();
	private Optional<String> logPath = Optional.empty();
	
	//parameter for the algorithm
	private Optional<Integer> clippingThreshold = Optional.empty();
	private Optional<Integer> hummingThreshold = Optional.empty();
	private Optional<Integer> silenceThreshold = Optional.empty();
	private Optional<Integer> volumeThreshold = Optional.empty();	
	private Optional<Float> clippingWeight = Optional.empty();
	private Optional<Float> hummingWeight = Optional.empty();
	private Optional<Float> silenceWeight = Optional.empty();
	private Optional<Float> volumeWeight = Optional.empty();
	private Optional<Float> hummingLength = Optional.empty();
	private Optional<Float> silenceLength = Optional.empty();
	private Optional<Float> volumeLength = Optional.empty();
	
	public Optional<Integer> getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(int frameSize) {
		this.windowSize = Optional.of(frameSize);
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
	public Optional<String> getSpectrogramPath(){
		return spectrogramPath;
	}
	public void setSpectrogramPath(String path) {
		this.spectrogramPath = Optional.of(path);
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
	public Optional<FrequencyConversion> getFrequencyConverter() {
		return frequencyConverter;
	}
	public void setFrequencyConverter(FrequencyConversion frequencyConverter) {
		this.frequencyConverter = Optional.of(frequencyConverter);
	}
	public Optional<WindowFunction> getWindowFunction(){
		return windowFunction;
	}
	public void setWindowFunction(WindowFunction windowFuntion) {
		this.windowFunction = Optional.of(windowFuntion);
	}
	public Optional<Boolean> getCreateLog() {
		return createLog;
	}
	public void setCreateLog(boolean createLog) {
		this.createLog = Optional.of(createLog);
	}
	public Optional<String> getLogPath(){
		return logPath;
	}
	public void setLogPath(String path) {
		this.logPath = Optional.of(path);
	}
	public Optional<Integer> getClippingThreshold(){
		return clippingThreshold;
	}
	public void setClippingThreshold(int value) {
		this.clippingThreshold = Optional.of(value);
	}
	public Optional<Integer> getHummingThreshold(){
		return hummingThreshold;
	}
	public void setHummingThreshold(int value) {
		this.hummingThreshold = Optional.of(value);
	}
	public Optional<Integer> getSilenceThreshold(){
		return silenceThreshold;
	}
	public void setSilenceThreshold(int value) {
		this.silenceThreshold = Optional.of(value);
	}
	public Optional<Integer> getVolumeThreshold(){
		return volumeThreshold;
	}
	public void setVolumeThreshold(int value) {
		this.volumeThreshold = Optional.of(value);
	}
	public Optional<Float> getClippingWeight(){
		return clippingWeight;
	}
	public void setClippingWeight(float value) {
		this.clippingWeight = Optional.of(value);
	}
	public Optional<Float> getSilenceWeight(){
		return silenceWeight;
	}
	public void setSilenceWeight(float value) {
		this.silenceWeight = Optional.of(value);
	}
	public Optional<Float> getVolumeWeight(){
		return volumeWeight;
	}
	public void setVolumeWeight(float value) {
		this.volumeWeight = Optional.of(value);
	}
	public Optional<Float> getHummingWeight(){
		return hummingWeight;
	}
	public void setHummingWeight(float value) {
		this.hummingWeight = Optional.of(value);
	}
	public Optional<Float> getHummingLength(){
		return hummingLength;
	}
	public void setHummingLength(float value) {
		this.hummingLength = Optional.of(value);
	}
	public Optional<Float> getSilenceLength(){
		return silenceLength;
	}
	public void setSilenceLength(float value) {
		this.silenceLength = Optional.of(value);
	}
	public Optional<Float> getVolumeLength(){
		return volumeLength;
	}
	public void setVolumeLength(float value) {
		this.volumeLength = Optional.of(value);
	}
}
