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

package lanr.view;

/**
 * Contains different descriptions used in the GUI.
 * 
 * @author Nicolas Bruch
 *
 */
public class Descriptions {

	public static final String WINDOW_FUNCTION_DESCRIPTION = "Function used to reduce spectral leakage in the frequency domain.";
	public static final String FREQUENCY_TRANSFORM_DESCRIPTION = "Method used to transform the samples from time to frequency domain.";
	public static final String CONTRAST_DESCRIPTION = "Contrast of the drawn spectrogram.";
	public static final String WINDOW_SIZE_DESCRIPTION = "Size of the window in which the file is scanned (in samples)."
			+ " A bigger window also means more memory usage.";
	
	public static final String CLIPPING_THRESHOLD_DESCRIPTION = "Sensitivity threshold for the detection of clipping.";
	public static final String VOLUME_THRESHOLD_DESCRIPTION = "Sensitivity threshold for the detection of volume.";
	public static final String SILENCE_THRESHOLD_DESCRIPTION = "Sensitivity threshold for the detection of silence.";
	public static final String HUMMING_THRESHOLD_DESCRIPTION = "Sensitivity threshold for the detection of humming.";
	
	public static final String HUMMING_WEIGHT_DESCRIPTION = "Weighting of found humming noise in the overall severity.";
	public static final String SILENCE_WEIGHT_DESCRIPTION = "Weighting of found silence noise in the overall severity.";
	public static final String VOLUME_WEIGHT_DESCRIPTION = "Weighting of found volume noise in the overall severity.";
	public static final String CLIPPING_WEIGHT_DESCRIPTION = "Weighting of found clipping noise in the overall severity.";
	
	public static final String HUMMING_LENGTH_DESCRIPTION = "Length a humming noise has to have to be recognized.";
	public static final String SILENCE_LENGTH_DESCRIPTION = "Length a silence noise has to have to be recognized.";
	public static final String VOLUME_LENGTH_DESCRIPTION = "Length a volume noise has to have to be recognized.";
}
