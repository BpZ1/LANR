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
