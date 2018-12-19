package lanr.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Model {

	protected final PropertyChangeSupport state = new PropertyChangeSupport(this);
	
	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
	}
}
