package lanr.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lanr.model.Settings;

/**
 * @author Nicolas Bruch
 *
 */
public class SpectrogramSettingsView extends GridPane {

	private SimpleBooleanProperty createSpectro = new SimpleBooleanProperty();
	private SimpleIntegerProperty contrast = new SimpleIntegerProperty();
	
	public SpectrogramSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);
		createCreateSpectrogramControl();
		createContrastControl();
		createSpectro.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}
	
	private void createCreateSpectrogramControl() {
		Label label = new Label("Create Spectrorgam");
		CheckBox box = new CheckBox();
		box.setSelected(Settings.getInstance().createSpectrogram());
		createSpectro.bind(box.selectedProperty());
		this.add(label, 0, 0);
		this.add(box, 1, 0);
	}
	
	private void createContrastControl() {
		Label label = new Label("Contrast");
		Slider slider = new Slider();
		Label sliderLabel = new Label("Contrast");	
		slider.setMin(1.0);
		slider.setMax(1000.0);
		slider.valueProperty()
			.addListener((obs, oldval, newVal) -> {
				slider.setValue(newVal.intValue());
				sliderLabel.setText(String.valueOf((int)slider.getValue()));
			});
		slider.setValue(Settings.getInstance().getSpectrogramContrast());
		contrast.bind(slider.valueProperty());
		this.add(label, 0, 1);
		this.add(slider, 1, 1);
		this.add(sliderLabel, 2, 1);
	}	
	
	public boolean getCreateSpectrogram() {
		return createSpectro.getValue();
	}
	
	public int getContrast() {
		return contrast.getValue();
	}
}
