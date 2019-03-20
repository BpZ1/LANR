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

import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lanr.logic.model.NoiseType;

/**
 * @author Nicolas Bruch
 *
 */
public class NoiseInfoView extends Stage {

	public NoiseInfoView() {
		Accordion root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 300, 250);
		this.setScene(scene);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
	}
	
	private Accordion createRootPane() {
		Accordion ac = new Accordion();
		for(NoiseType type : NoiseType.values()) {
			ac.getPanes().add(createNoiseTypePane(type));			
		}
		return ac;
	}
	
	private TitledPane createNoiseTypePane(NoiseType type) {
		TitledPane tp = new TitledPane();
		tp.setText(type.toString());
		Rectangle rect = new Rectangle(12,12);
		rect.setFill(StreamVisualisation.getNoiseColor(type));
		tp.setGraphic(rect);
		Label description = new Label();
		description.setText(type.getDefinition());
		description.setWrapText(true);
		tp.setContent(description);
		return tp;
	}
}
