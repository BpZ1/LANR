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

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lanr.logic.model.AudioStream;
import lanr.logic.model.AudioData;

/**
 * Contains the different stream visualizations for the different audio streams.
 * 
 * @author Nicolas Bruch
 *
 */
public class AudioVisualisation extends ScrollPane {
	
	private static final String CSS_ID = "visualisationBackGround";

	public AudioVisualisation(double height, double minWidth, AudioData data) {	
		this.setVbarPolicy(ScrollBarPolicy.NEVER);
		this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setHeight(height);
		this.setId(CSS_ID);
		double canvasHeight = height / data.getStreams().size();
		VBox content = new VBox();		
		//The canvas are created in advance and drawn to when analyzing
		for (AudioStream stream : data.getStreams()) {
			content.getChildren().add(new StreamVisualisation(
					minWidth,
					canvasHeight,
					stream));
		}
		this.setContent(content);
	}
}
