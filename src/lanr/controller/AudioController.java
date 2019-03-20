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

package lanr.controller;

import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.view.Utils;

/**
 * @author Nicolas Bruch
 *
 */
public class AudioController {

	private MainModel model;
	
	public AudioController(MainModel model) {
		this.model = model;
	}
	
	public void analyze(AudioData data) {
		model.analyzeAudio(data);
	}
	
	public void createLog(AudioData data) {
		try {
			model.createLogFile(data);
		} catch (LANRException e) {
			Utils.showErrorDialog("Log creation not possible!",
					"Logs can only be created for analysed files.");
		}
	}
}
