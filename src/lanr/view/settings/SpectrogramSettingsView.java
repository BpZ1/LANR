package lanr.view.settings;

import java.io.File;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lanr.model.Settings;
import lanr.view.Descriptions;
import lanr.view.Utils;

/**
 * 
 * View that contains controls for the spectrogram.
 * @author Nicolas Bruch
 *
 */
public class SpectrogramSettingsView extends GridPane {
	
	private Window parent;
	/**
	 * If true the spectrogram is created.
	 */
	private SimpleBooleanProperty createSpectro = new SimpleBooleanProperty();
	/**
	 * Contrast for the spectrogram.
	 */
	private SimpleIntegerProperty contrast = new SimpleIntegerProperty();
	/**
	 * Spectrogram output path.
	 */
	private SimpleStringProperty path = new SimpleStringProperty();
	
	public SpectrogramSettingsView(SimpleBooleanProperty changed, Window parent) {
		this.parent = parent;
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);	
		//Create the controls
		createCreateSpectrogramControl();
		createContrastControl();
		createPathControl();
		//Add listener for changes of settings
		createSpectro.addListener((obs, oldval, newVal) -> changed.setValue(true));
		contrast.addListener((obs, oldval, newVal) -> changed.setValue(true));
		path.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}
	
	private void createCreateSpectrogramControl() {
		Label label = new Label("Create Spectrorgam");
		CheckBox box = new CheckBox();
		InfoButton info = new InfoButton(Descriptions.CONTRAST_DESCRIPTION);
		box.setSelected((boolean)Settings.getInstance()
				.getPropertyValue(Settings.CREATE_SPECTRO_PROPERTY_NAME));
		createSpectro.bind(box.selectedProperty());
		this.add(label, 0, 0);
		this.add(box, 1, 0);
		this.add(info, 2, 0);
	}
	
	private void createContrastControl() {
		Label label = new Label("Contrast");
		Slider slider = new Slider();
		Label sliderLabel = new Label("Contrast");	
		slider.setMin(1.0);
		slider.setMax(1000.0);
		//Listener for change in the slider to update the label.
		slider.valueProperty()
			.addListener((obs, oldval, newVal) -> {
				slider.setValue(newVal.intValue());
				sliderLabel.setText(String.valueOf((int)slider.getValue()));
			});
		int currentValue = (int)Settings.getInstance()
				.getPropertyValue(Settings.SPECTRO_CONTRAST_PROPERTY_NAME);
		slider.setValue(currentValue);
		contrast.bind(slider.valueProperty());
		this.add(label, 0, 1);
		this.add(slider, 1, 1);
		this.add(sliderLabel, 2, 1);
	}	

	private void createPathControl() {
		TextField text = new TextField();		
		Button button = new Button("Change");
		File initialPath = new File((String)Settings.getInstance()
				.getPropertyValue(Settings.SPECTROGRAM_PATH_PROPERTY_NAME));
		text.setText(initialPath.getAbsolutePath());
		path.bind(text.textProperty());
		button.setOnAction(event ->{
			String selectedPath = Utils.showDirectoryDirectorySelect("Choose output directory", parent);
			if(selectedPath != null) {
				text.setText(selectedPath);
			}
		});
		Label label = new Label("Output path:");
		this.add(label, 0, 2, 3, 1);
		this.add(text, 0, 3, 2, 1);
		this.add(button, 2, 3);
	}
	
	public boolean getCreateSpectrogram() {
		return createSpectro.getValue();
	}
	
	public int getContrast() {
		return contrast.getValue();
	}
	
	public String getPath() {
		return path.getValue();
	}
}
