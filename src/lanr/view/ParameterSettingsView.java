package lanr.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lanr.model.Settings;

public class ParameterSettingsView extends GridPane {
	
	private SimpleIntegerProperty windowSize = new SimpleIntegerProperty();
	
	public ParameterSettingsView() {
		
		
		
		createContent();
	}
	
	
	private void createContent() {
		Label windowValueLabel = new Label();		
		windowSize.addListener((obs, oldval, newVal) -> 
				windowValueLabel.setText(String.valueOf(newVal.intValue())			
		));
		windowSize.setValue(Settings.getInstance().getWindowSize());
		Label windowSliderLabel = new Label("Window size");		
		Slider windowSizeSlider = new Slider();
		windowSizeSlider.setValue(Math.log(windowSize.getValue()) / Math.log(2));
		windowSizeSlider.setMin(7);
		windowSizeSlider.setMax(31);
		windowSizeSlider.valueProperty().addListener((obs, oldval, newVal) -> 
			windowSizeSlider.setValue(newVal.intValue()
		));
		windowSizeSlider.setOnMouseReleased(event ->{
			windowSize.setValue(Math.pow(2, windowSizeSlider.getValue()));
		});
		this.add(windowSliderLabel, 0, 0);
		this.add(windowSizeSlider, 1, 0);		
		this.add(windowValueLabel, 2, 0);
	}
}
