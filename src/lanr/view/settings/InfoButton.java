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

package lanr.view.settings;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * Info button control that displays information when pressed.
 * 
 * @author Nicolas Bruch
 *
 */
public class InfoButton extends Button {
	
	private static List<Tooltip> tooltips = new ArrayList<Tooltip>();
	
	private static final double SIZE = 18;
	
	public InfoButton(String message) {
		super("?");
		this.setId("infoButton");
		this.setMinWidth(SIZE);
		this.setMinHeight(SIZE);
		this.setMaxWidth(SIZE);
		this.setMaxHeight(SIZE);
		this.setStyle(
				String.format("-fx-font-size: %dpx;", (int)(0.5 * SIZE))
				+ "-fx-font-weight: bold;"	);
		Tooltip tt = new Tooltip();
		tt.setText(message);
		tooltips.add(tt);
		this.setOnAction(event ->{
			if(tt.isShowing()) {
				hideAllTooltips();
			}else {
				hideAllTooltips();
				tt.show(this,
						this.localToScreen(this.getBoundsInLocal()).getCenterX() + 30,
						this.localToScreen(this.getBoundsInLocal()).getCenterY() - (tt.getHeight() / 2));
			}			
		});		
	}
	
	public static void hideAllTooltips() {
		for(Tooltip tt : tooltips) {
			tt.hide();
		}
	}
}
