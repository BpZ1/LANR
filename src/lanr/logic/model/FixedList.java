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

package lanr.logic.model;

import javolution.util.FastTable;

/**
 * A list of fixed size, that removes the oldest element if a new one is added
 * and the size goes over the limit.
 * 
 * @author Nicolas Bruch
 *
 * @param <T>
 */
public class FixedList<T> {

	private int size;
	private final FastTable<T> elements;
	
	public FixedList(int size) {
		if(size < 1) {
			throw new IllegalArgumentException("Can't create queue with size smaller than one.");
		}
		elements = new FastTable<T>();
		this.size = size;
	}
	
	public void add(T element) {
		elements.add(element);
		if(elements.size() > size) {
			elements.remove(0);
		}
	}
	
	public boolean remove(T element) {
		return elements.remove(element);
	}
	
	public T get(int index) {
		return elements.get(index);
	}
	
	public FastTable<T> getAll() {
		return elements;
	}
	
	public int size() {
		return elements.size();
	}
}
